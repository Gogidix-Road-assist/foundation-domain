const test = require('node:test')
const assert = require('node:assert/strict')
const { jwtAuth, generateTestToken } = require('../src/adapters/in/http/middleware/auth')

test.describe('JWT Authentication', () => {
  test('should generate a valid token', () => {
    const payload = {
      sub: 'user-123',
      email: 'test@example.com',
      name: 'Test User',
      role: 'user'
    }

    const token = jwtAuth.generateToken(payload)

    assert.ok(typeof token === 'string')
    assert.ok(token.length > 0)
    assert.ok(token.split('.').length === 3) // JWT has 3 parts
  })

  test('should verify a valid token', () => {
    const payload = {
      sub: 'user-123',
      email: 'test@example.com',
      name: 'Test User',
      role: 'user'
    }

    const token = jwtAuth.generateToken(payload)
    const decoded = jwtAuth.verifyToken(token)

    assert.strictEqual(decoded.sub, 'user-123')
    assert.strictEqual(decoded.email, 'test@example.com')
    assert.strictEqual(decoded.name, 'Test User')
    assert.strictEqual(decoded.role, 'user')
  })

  test('should reject invalid token', () => {
    assert.throws(
      () => jwtAuth.verifyToken('invalid.token.here'),
      (err) => {
        return err.name === 'AuthenticationError' &&
               err.message === 'Invalid authentication token'
      }
    )
  })

  test('should extract token from Authorization header', () => {
    const mockReq = {
      get: (header) => header === 'Authorization' ? 'Bearer valid-token-123' : null
    }

    const token = jwtAuth.extractToken(mockReq)
    assert.strictEqual(token, 'valid-token-123')
  })

  test('should return null when no token present', () => {
    const mockReq = {
      get: () => null,
      cookies: null,
      query: {}
    }

    const token = jwtAuth.extractToken(mockReq)
    assert.strictEqual(token, null)
  })

  test('should generate test token with default values', () => {
    const token = generateTestToken()
    const decoded = jwtAuth.verifyToken(token)

    assert.strictEqual(decoded.sub, 'test-user-123')
    assert.strictEqual(decoded.email, 'test@example.com')
    assert.strictEqual(decoded.name, 'Test User')
  })

  test('should generate test token with overrides', () => {
    const overrides = {
      email: 'admin@example.com',
      role: 'admin',
      permissions: ['read:all', 'write:all']
    }

    const token = generateTestToken(overrides)
    const decoded = jwtAuth.verifyToken(token)

    assert.strictEqual(decoded.email, 'admin@example.com')
    assert.strictEqual(decoded.role, 'admin')
    assert.deepStrictEqual(decoded.permissions, ['read:all', 'write:all'])
  })
})

test.describe('Authentication Middleware', () => {
  test('should set user context in request when valid token provided', (t) => {
    const mockReq = {
      get: (header) => header === 'Authorization' ? `Bearer ${generateTestToken()}` : null,
      ip: '127.0.0.1',
      originalUrl: '/api/v1/dashboard'
    }

    const mockRes = {}
    let nextCalled = false
    const mockNext = () => { nextCalled = true }

    const middleware = jwtAuth.authenticate()
    middleware(mockReq, mockRes, mockNext)

    assert.ok(nextCalled)
    assert.ok(mockReq.user)
    assert.strictEqual(mockReq.user.id, 'test-user-123')
    assert.strictEqual(mockReq.user.email, 'test@example.com')
  })

  test('should return 401 when no token provided', (t) => {
    const mockReq = {
      get: () => null,
      cookies: null,
      query: {},
      ip: '127.0.0.1',
      originalUrl: '/api/v1/dashboard'
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return {
          json: (data) => ({ statusCode, data })
        }
      }
    }

    const mockNext = () => {}

    const middleware = jwtAuth.authenticate()
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 401)
  })
})

test.describe('Authorization Middleware', () => {
  test('should allow access with correct permissions', () => {
    const mockReq = {
      user: {
        id: 'user-123',
        permissions: ['read:dashboard', 'write:dashboard']
      }
    }

    let nextCalled = false
    const mockNext = () => { nextCalled = true }
    const mockRes = {}

    const middleware = jwtAuth.requirePermissions(['read:dashboard'])
    middleware(mockReq, mockRes, mockNext)

    assert.ok(nextCalled)
  })

  test('should deny access with missing permissions', () => {
    const mockReq = {
      user: {
        id: 'user-123',
        permissions: ['read:dashboard']
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return {
          json: () => mockRes
        }
      }
    }

    const mockNext = () => {}

    const middleware = jwtAuth.requirePermissions(['write:dashboard'])
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 403)
  })

  test('should allow access with wildcard permission', () => {
    const mockReq = {
      user: {
        id: 'user-123',
        permissions: ['*']
      }
    }

    let nextCalled = false
    const mockNext = () => { nextCalled = true }
    const mockRes = {}

    const middleware = jwtAuth.requirePermissions(['admin:only'])
    middleware(mockReq, mockRes, mockNext)

    assert.ok(nextCalled)
  })

  test('should allow access with correct role', () => {
    const mockReq = {
      user: {
        id: 'user-123',
        role: 'admin'
      }
    }

    let nextCalled = false
    const mockNext = () => { nextCalled = true }
    const mockRes = {}

    const middleware = jwtAuth.requireRoles(['admin'])
    middleware(mockReq, mockRes, mockNext)

    assert.ok(nextCalled)
  })

  test('should deny access with incorrect role', () => {
    const mockReq = {
      user: {
        id: 'user-123',
        role: 'user'
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return {
          json: () => mockRes
        }
      }
    }

    const mockNext = () => {}

    const middleware = jwtAuth.requireRoles(['admin'])
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 403)
  })
})
