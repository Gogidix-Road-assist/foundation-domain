const test = require('node:test')
const assert = require('node:assert/strict')
const request = require('supertest')
const { createApp } = require('../src/bootstrap/server')
const { generateTestToken } = require('../src/adapters/in/http/middleware/auth')

test.describe('API Integration Tests', () => {
  let app

  test.before(() => {
    // Create Express app for testing
    app = createApp()
  })

  test.describe('Public Endpoints', () => {
    test('GET /status should return 200', async () => {
      const response = await request(app)
        .get('/status')
        .expect('Content-Type', /json/)
        .expect(200)

      assert.ok(response.body)
      assert.ok(response.body.status)
    })

    test('GET /api/v1/health should return health status', async () => {
      const response = await request(app)
        .get('/api/v1/health')
        .expect('Content-Type', /json/)
        .expect(200)

      assert.strictEqual(response.body.status, 'healthy')
      assert.ok(response.body.timestamp)
      assert.strictEqual(response.body.service, 'dashboard-aggregation-service')
      assert.ok(response.body.version)
    })

    test('GET /api-docs.json should return OpenAPI spec', async () => {
      const response = await request(app)
        .get('/api-docs.json')
        .expect('Content-Type', /json/)
        .expect(200)

      assert.ok(response.body.openapi)
      assert.ok(response.body.info)
      assert.strictEqual(response.body.info.title, 'Dashboard Aggregation Service API')
    })

    test('GET /api-docs should serve Swagger UI', async () => {
      const response = await request(app)
        .get('/api-docs')
        .expect(200)

      assert.ok(response.text)
      assert.ok(response.text.includes('swagger') || response.text.includes('Swagger'))
    })
  })

  test.describe('Protected Endpoints - Authentication Required', () => {
    const validToken = generateTestToken()

    test('GET /api/v1/dashboard should fail without authentication', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .expect(401)

      assert.ok(response.body.error)
      assert.strictEqual(response.body.type, 'authentication')
    })

    test('GET /api/v1/dashboard should succeed with valid token', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('Authorization', `Bearer ${validToken}`)
        .expect(200)

      // Response format depends on controller implementation
      assert.ok(response.body)
    })

    test('GET /api/v1/dashboard/overview should require authentication', async () => {
      await request(app)
        .get('/api/v1/dashboard/overview')
        .expect(401)
    })

    test('GET /api/v1/dashboard/overview should succeed with authentication', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard/overview')
        .set('Authorization', `Bearer ${validToken}`)
        .expect(200)

      assert.ok(response.body)
    })

    test('GET /api/v1/metrics/summary should require authentication', async () => {
      await request(app)
        .get('/api/v1/metrics/summary')
        .expect(401)
    })

    test('GET /api/v1/analytics/overview should require authentication', async () => {
      await request(app)
        .get('/api/v1/analytics/overview')
        .expect(401)
    })

    test('GET /api/v1/services/health should require authentication', async () => {
      await request(app)
        .get('/api/v1/services/health')
        .expect(401)
    })

    test('GET /api/v1/alerts should require authentication', async () => {
      await request(app)
        .get('/api/v1/alerts')
        .expect(401)
    })

    test('GET /api/v1/reports should require authentication', async () => {
      await request(app)
        .get('/api/v1/reports')
        .expect(401)
    })
  })

  test.describe('Validation Tests', () => {
    const validToken = generateTestToken()

    test('POST /api/v1/dashboard/widgets should validate request body', async () => {
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .set('Authorization', `Bearer ${validToken}`)
        .send({
          // Missing required fields
          name: 'Test Widget'
        })
        .expect(400)

      assert.strictEqual(response.body.error, 'Validation failed')
      assert.ok(Array.isArray(response.body.details))
    })

    test('POST /api/v1/dashboard/widgets should accept valid request', async () => {
      const validWidget = {
        name: 'Test Widget',
        type: 'chart',
        title: 'Test Chart',
        position: {
          x: 0,
          y: 0,
          width: 6,
          height: 4
        },
        config: {
          chartType: 'line'
        },
        dataSource: {
          type: 'service',
          endpoint: '/api/v1/metrics'
        },
        tenantId: 'tenant-123'
      }

      // This will likely fail with 500 if controller not implemented,
      // but should not fail with 400 (validation error)
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .set('Authorization', `Bearer ${validToken}`)
        .send(validWidget)

      // Should not be a validation error (400)
      assert.notStrictEqual(response.status, 400)
    })

    test('GET /api/v1/analytics/overview should validate query parameters', async () => {
      const response = await request(app)
        .get('/api/v1/analytics/overview?timeRange=invalid')
        .set('Authorization', `Bearer ${validToken}`)
        .expect(400)

      assert.strictEqual(response.body.error, 'Validation failed')
    })

    test('GET /api/v1/metrics/summary should accept valid query parameters', async () => {
      const response = await request(app)
        .get('/api/v1/metrics/summary?timeRange=24h&granularity=5m')
        .set('Authorization', `Bearer ${validToken}`)

      // Should not be a validation error
      assert.notStrictEqual(response.status, 400)
    })
  })

  test.describe('404 Error Handling', () => {
    test('GET /invalid/endpoint should return 404', async () => {
      const response = await request(app)
        .get('/invalid/endpoint')
        .expect(404)

      assert.ok(response.body.error)
      assert.ok(response.body.path)
      assert.ok(response.body.method)
    })

    test('POST /api/v1/unknown should return 404', async () => {
      const response = await request(app)
        .post('/api/v1/unknown')
        .expect(404)

      assert.strictEqual(response.body.error, 'Endpoint not found')
    })
  })

  test.describe('Invalid Token Handling', () => {
    test('should reject invalid JWT format', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('Authorization', 'Bearer invalid-token')
        .expect(401)

      assert.strictEqual(response.body.type, 'authentication')
    })

    test('should reject missing Authorization header', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .expect(401)

      assert.strictEqual(response.body.type, 'authentication')
    })

    test('should reject malformed Authorization header', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('Authorization', 'InvalidFormat token')
        .expect(401)
    })
  })

  test.describe('Rate Limiting Headers', () => {
    test('should include rate limit headers on protected endpoints', async () => {
      const validToken = generateTestToken()

      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('Authorization', `Bearer ${validToken}`)
        .expect(200)

      // Rate limit headers should be present
      assert.ok(response.headers['x-ratelimit-limit'] !== undefined ||
                  response.headers['X-RateLimit-Limit'] !== undefined)
    })
  })

  test.describe('CORS Headers', () => {
    test('should include CORS headers', async () => {
      const response = await request(app)
        .get('/api/v1/health')
        .expect(200)

      // CORS headers should be present
      assert.ok(response.headers['access-control-allow-origin'] !== undefined ||
                  response.headers['Access-Control-Allow-Origin'] !== undefined)
    })
  })

  test.describe('Security Headers', () => {
    test('should include security headers', async () => {
      const response = await request(app)
        .get('/api/v1/health')
        .expect(200)

      // Security headers from helmet
      assert.ok(response.headers['x-dns-prefetch-control'] !== undefined ||
                  response.headers['X-DNS-Prefetch-Control'] !== undefined)
    })
  })
})
