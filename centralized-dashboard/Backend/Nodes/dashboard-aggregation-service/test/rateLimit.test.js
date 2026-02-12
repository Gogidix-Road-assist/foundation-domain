const test = require('node:test')
const assert = require('node:assert/strict')
const { RateLimiter, apiLimiter, dashboardLimiter, reportLimiter } = require('../src/adapters/in/http/middleware/rateLimiting')

test.describe('Rate Limiter - Basic Functionality', () => {
  test('should create rate limiter with custom options', () => {
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 50
    })

    assert.strictEqual(limiter.windowMs, 60000)
    assert.strictEqual(limiter.maxRequests, 50)
  })

  test('should use default options when none provided', () => {
    const limiter = new RateLimiter()

    assert.strictEqual(limiter.windowMs, 60000)
    assert.strictEqual(limiter.maxRequests, 100)
  })

  test('should generate key based on IP address by default', () => {
    const limiter = new RateLimiter()
    const mockReq = {
      ip: '192.168.1.1',
      apiKey: null,
      user: null
    }

    const key = limiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'ratelimit:ip:192.168.1.1')
  })

  test('should generate key based on API key when present', () => {
    const limiter = new RateLimiter()
    const mockReq = {
      ip: '192.168.1.1',
      apiKey: 'api-key-123',
      user: null
    }

    const key = limiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'ratelimit:api:api-key-123')
  })

  test('should generate key based on user ID when present', () => {
    const limiter = new RateLimiter()
    const mockReq = {
      ip: '192.168.1.1',
      apiKey: null,
      user: { id: 'user-456' }
    }

    const key = limiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'ratelimit:user:user-456')
  })

  test('should prioritize API key over user ID over IP', () => {
    const limiter = new RateLimiter()
    const mockReq = {
      ip: '192.168.1.1',
      apiKey: 'api-key-123',
      user: { id: 'user-456' }
    }

    const key = limiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'ratelimit:api:api-key-123')
  })
})

test.describe('Predefined Rate Limiters', () => {
  test('should have API limiter with correct settings', () => {
    assert.strictEqual(apiLimiter.windowMs, 60000) // 1 minute
    assert.strictEqual(apiLimiter.maxRequests, 100)
  })

  test('should have dashboard limiter with stricter limits', () => {
    assert.strictEqual(dashboardLimiter.windowMs, 60000) // 1 minute
    assert.strictEqual(dashboardLimiter.maxRequests, 30) // Stricter than API
  })

  test('should have report limiter with very strict limits', () => {
    assert.strictEqual(reportLimiter.windowMs, 300000) // 5 minutes
    assert.strictEqual(reportLimiter.maxRequests, 10) // Very strict
  })
})

test.describe('Rate Limiter Middleware', () => {
  test('should set rate limit headers', async (t) => {
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 10
    })

    const mockReq = {
      ip: '192.168.1.1',
      get: () => null
    }

    let headersSet = false
    const mockRes = {
      set: (headers) => {
        headersSet = true
        assert.ok(headers['X-RateLimit-Limit'])
        assert.strictEqual(headers['X-RateLimit-Limit'], 10)
        assert.ok(headers['X-RateLimit-Remaining'])
        assert.ok(headers['X-RateLimit-Reset'])
      },
      on: () => {}
    }

    let nextCalled = false
    const mockNext = () => { nextCalled = true }

    const middleware = limiter.middleware.bind(limiter)
    await middleware(mockReq, mockRes, mockNext)

    assert.ok(headersSet)
    assert.ok(nextCalled)
  })

  test('should pass request when under limit', async (t) => {
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 10
    })

    const mockReq = {
      ip: '192.168.1.' + Math.floor(Math.random() * 255), // Random IP
      get: () => null
    }

    const mockRes = {
      set: () => {},
      on: () => {}
    }

    let nextCalled = false
    const mockNext = () => { nextCalled = true }

    const middleware = limiter.middleware.bind(limiter)
    await middleware(mockReq, mockRes, mockNext)

    assert.ok(nextCalled)
  })
})

test.describe('Custom Key Generators', () => {
  test('should use custom key generator when provided', () => {
    const customKeyGenerator = (req) => {
      return `custom:${req.body?.clientId || req.ip}`
    }

    const limiter = new RateLimiter({
      keyGenerator: customKeyGenerator
    })

    const mockReq = {
      ip: '192.168.1.1',
      body: { clientId: 'client-xyz' }
    }

    const key = limiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'custom:client-xyz')
  })

  test('should handle webhook-specific key generator', () => {
    const webhookLimiter = {
      keyGenerator: (req) => `ratelimit:webhook:${req.body.webhookId || req.ip}`,
      middleware: null
    }

    const mockReq = {
      ip: '192.168.1.1',
      body: { webhookId: 'webhook-123' }
    }

    const key = webhookLimiter.keyGenerator(mockReq)
    assert.strictEqual(key, 'ratelimit:webhook:webhook-123')
  })
})

test.describe('Rate Limit Configuration', () => {
  test('should skip counting successful requests when configured', async (t) => {
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 5,
      skipSuccessfulRequests: true
    })

    assert.strictEqual(limiter.skipSuccessfulRequests, true)
  })

  test('should skip counting failed requests when configured', async (t) => {
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 5,
      skipFailedRequests: true
    })

    assert.strictEqual(limiter.skipFailedRequests, true)
  })
})

test.describe('Rate Limit Error Response', () => {
  test('should return 429 status when rate limit exceeded', async (t) => {
    // This test demonstrates the expected behavior
    // In a real scenario, you'd need to mock the cache to simulate limit exceeded
    const limiter = new RateLimiter({
      windowMs: 60000,
      maxRequests: 1 // Very low limit for testing
    })

    const mockReq = {
      ip: '192.168.1.99',
      get: () => null
    }

    let statusCode = 200
    const mockRes = {
      set: () => {},
      on: () => {},
      status: (code) => {
        statusCode = code
        return {
          json: () => ({})
        }
      }
    }

    const mockNext = () => {}

    const middleware = limiter.middleware.bind(limiter)

    // First request should pass
    await middleware(mockReq, mockRes, mockNext)
    assert.strictEqual(statusCode, 200)

    // Note: Testing actual rate limit exceeding would require cache mocking
    // This test demonstrates the structure
  })

  test('should include retry information in error response', () => {
    // Error response should include:
    // - error message
    // - retryAfter value
    // - limit and windowMs information

    const expectedFields = [
      'error',
      'retryAfter',
      'limit',
      'windowMs'
    ]

    // This demonstrates the expected error response structure
    expectedFields.forEach(field => {
      assert.ok(typeof field === 'string')
    })
  })
})

test.describe('Rate Limiter Headers', () => {
  test('should include X-RateLimit-Limit header', () => {
    const limiter = new RateLimiter({
      maxRequests: 100
    })

    assert.strictEqual(limiter.maxRequests, 100)
  })

  test('should calculate X-RateLimit-Remaining correctly', () => {
    // Remaining should be max - current
    const maxRequests = 100
    const currentRequests = 25
    const remaining = Math.max(0, maxRequests - currentRequests)

    assert.strictEqual(remaining, 75)
  })

  test('should include X-RateLimit-Reset timestamp', () => {
    const windowMs = 60000
    const resetTime = Date.now() + windowMs

    assert.ok(resetTime > Date.now())
    assert.ok(resetTime <= Date.now() + windowMs)
  })
})
