const request = require('supertest')
const express = require('express')
const { dashboardController } = require('../../src/adapters/in/http/dashboardController')
const { validateBody, validateQuery, schemas } = require('../../src/adapters/in/http/middleware/validation')
const { apiLimiter } = require('../../src/adapters/in/http/middleware/rateLimiting')
const { auditMiddleware } = require('../../src/adapters/in/http/middleware/auditLogger')
const { errorHandler } = require('../../src/adapters/in/http/middleware/errorHandler')

describe('Dashboard Controller', () => {
  let app

  beforeEach(() => {
    app = express()
    app.use(express.json())
    app.use(auditMiddleware())

    // Apply rate limiting to dashboard routes
    app.use('/api/v1/dashboard', apiLimiter.middleware())

    // Dashboard routes with validation
    app.get('/api/v1/dashboard', validateQuery(schemas.dashboard.query), dashboardController.getDashboard)
    app.get('/api/v1/dashboard/overview', validateQuery(schemas.dashboard.query), dashboardController.getOverview)
    app.get('/api/v1/dashboard/widgets', validateQuery(schemas.dashboard.query), dashboardController.getWidgets)
    app.post('/api/v1/dashboard/widgets', validateBody(schemas.widget.create), dashboardController.createWidget)
    app.put('/api/v1/dashboard/widgets/:widgetId', validateBody(schemas.widget.update), dashboardController.updateWidget)
    app.delete('/api/v1/dashboard/widgets/:widgetId', dashboardController.deleteWidget)

    app.use(errorHandler)
  })

  describe('GET /api/v1/dashboard', () => {
    it('should return dashboard data for valid request', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: 'test-tenant' })
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('data')
      expect(response.body.data).toHaveProperty('dashboard')
      expect(response.body.data).toHaveProperty('widgets')
      expect(response.body.data).toHaveProperty('lastUpdated')
      expect(response.headers).toHaveProperty('x-ratelimit-limit')
      expect(response.headers).toHaveProperty('x-ratelimit-remaining')
    })

    it('should handle dashboard refresh', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: 'test-tenant', refresh: true })
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('message', 'Dashboard refreshed successfully')
    })

    it('should validate query parameters', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .query({ includeWidgets: 'invalid' }) // Should be boolean
        .expect(400)

      expect(response.body).toHaveProperty('error', 'Validation failed')
      expect(response.body).toHaveProperty('details')
      expect(Array.isArray(response.body.details)).toBe(true)
    })

    it('should handle rate limiting', async () => {
      // Make many requests quickly to trigger rate limit
      const promises = Array(35).fill().map(() =>
        request(app).get('/api/v1/dashboard')
      )

      const responses = await Promise.all(promises)
      const rateLimitedResponse = responses.find(r => r.status === 429)

      expect(rateLimitedResponse).toBeDefined()
      expect(rateLimitedResponse.body).toHaveProperty('error', 'Too many requests')
    })
  })

  describe('GET /api/v1/dashboard/overview', () => {
    it('should return dashboard overview metrics', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard/overview')
        .query({ tenantId: 'test-tenant' })
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('data')
      expect(response.body.data).toHaveProperty('summary')
      expect(response.body.data).toHaveProperty('recentActivity')
      expect(response.body.data).toHaveProperty('quickStats')
    })

    it('should handle missing tenant', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard/overview')
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      // Should use default tenant
    })
  })

  describe('GET /api/v1/dashboard/widgets', () => {
    it('should return widgets list', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard/widgets')
        .query({ tenantId: 'test-tenant' })
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('data')
      expect(Array.isArray(response.body.data.widgets)).toBe(true)
    })

    it('should filter widgets by visibility', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard/widgets')
        .query({
          tenantId: 'test-tenant',
          role: 'admin',
          includeWidgets: true
        })
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
    })
  })

  describe('POST /api/v1/dashboard/widgets', () => {
    const validWidget = {
      name: 'Test Widget',
      type: 'metric',
      title: 'Test Metric Widget',
      description: 'A test widget for unit testing',
      position: {
        x: 0,
        y: 0,
        width: 4,
        height: 3
      },
      config: {
        metric: 'response_time',
        unit: 'ms'
      },
      dataSource: {
        type: 'service',
        endpoint: '/api/v1/metrics/performance',
        method: 'GET',
        refreshInterval: 30000
      },
      tenantId: 'test-tenant'
    }

    it('should create a new widget', async () => {
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(validWidget)
        .expect(201)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('data')
      expect(response.body.data).toHaveProperty('widget')
      expect(response.body.data.widget.name).toBe(validWidget.name)
      expect(response.body.data.widget).toHaveProperty('id')
      expect(response.body.data.widget).toHaveProperty('createdAt')
    })

    it('should validate widget creation request', async () => {
      const invalidWidget = {
        // Missing required fields
        name: '',
        type: 'invalid-type',
        position: { x: -1, y: -1 } // Invalid coordinates
      }

      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(invalidWidget)
        .expect(400)

      expect(response.body).toHaveProperty('error', 'Validation failed')
      expect(response.body.details).toEqual(
        expect.arrayContaining([
          expect.objectContaining({
            field: expect.stringMatching(/name|type|position|dataSource|tenantId/)
          })
        ])
      )
    })

    it('should validate widget type', async () => {
      const invalidTypeWidget = {
        ...validWidget,
        type: 'invalid-widget-type'
      }

      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(invalidTypeWidget)
        .expect(400)

      expect(response.body).toHaveProperty('error', 'Validation failed')
      expect(response.body.details).toEqual(
        expect.arrayContaining([
          expect.objectContaining({
            field: 'type'
          })
        ])
      )
    })

    it('should validate position coordinates', async () => {
      const invalidPositionWidget = {
        ...validWidget,
        position: {
          x: -1,
          y: 0,
          width: 0,
          height: 13
        }
      }

      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(invalidPositionWidget)
        .expect(400)

      expect(response.body.details).toEqual(
        expect.arrayContaining([
          expect.objectContaining({
            field: expect.stringMatching(/position\.x|position\.width|position\.height/)
          })
        ])
      )
    })
  })

  describe('PUT /api/v1/dashboard/widgets/:widgetId', () => {
    const validUpdate = {
      name: 'Updated Widget',
      title: 'Updated Title',
      position: {
        x: 1,
        y: 1,
        width: 6,
        height: 4
      }
    }

    it('should update existing widget', async () => {
      const response = await request(app)
        .put('/api/v1/dashboard/widgets/test-widget-id')
        .send(validUpdate)
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('message', 'Widget updated successfully')
      expect(response.body).toHaveProperty('data')
      expect(response.body.data.widget.name).toBe(validUpdate.name)
    })

    it('should validate widget update request', async () => {
      const invalidUpdate = {
        name: '', // Invalid empty name
        position: {
          x: 'invalid', // Should be number
          width: 20 // Exceeds max
        }
      }

      const response = await request(app)
        .put('/api/v1/dashboard/widgets/test-widget-id')
        .send(invalidUpdate)
        .expect(400)

      expect(response.body).toHaveProperty('error', 'Validation failed')
    })
  })

  describe('DELETE /api/v1/dashboard/widgets/:widgetId', () => {
    it('should delete existing widget', async () => {
      const response = await request(app)
        .delete('/api/v1/dashboard/widgets/test-widget-id')
        .expect(200)

      expect(response.body).toHaveProperty('success', true)
      expect(response.body).toHaveProperty('message', 'Widget deleted successfully')
    })

    it('should handle deletion of non-existent widget', async () => {
      const response = await request(app)
        .delete('/api/v1/dashboard/widgets/non-existent-id')
        .expect(404)

      expect(response.body).toHaveProperty('error', 'Widget not found')
    })
  })

  describe('Error Handling', () => {
    it('should handle malformed JSON', async () => {
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .set('Content-Type', 'application/json')
        .send('{"invalid": json}')
        .expect(400)

      expect(response.body).toHaveProperty('error')
    })

    it('should include request ID in error responses', async () => {
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send({})
        .expect(400)

      expect(response.body).toHaveProperty('requestId')
      expect(response.body).toHaveProperty('timestamp')
    })
  })

  describe('Security', () => {
    it('should prevent XSS in widget names', async () => {
      const maliciousWidget = {
        name: '<script>alert("xss")</script>',
        type: 'metric',
        title: 'Safe Title',
        position: { x: 0, y: 0, width: 4, height: 3 },
        config: {},
        dataSource: {
          type: 'service',
          endpoint: '/test',
          method: 'GET'
        },
        tenantId: 'test'
      }

      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(maliciousWidget)
        .expect(201)

      // Script should be sanitized
      expect(response.body.data.widget.name).not.toContain('<script>')
    })

    it('should sanitize dangerous patterns', async () => {
      const dangerousWidget = {
        name: 'javascript:alert(1)',
        type: 'metric',
        title: 'Test',
        position: { x: 0, y: 0, width: 4, height: 3 },
        config: {
          url: 'javascript:malicious()'
        },
        dataSource: {
          type: 'service',
          endpoint: '/test',
          method: 'GET'
        },
        tenantId: 'test'
      }

      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(dangerousWidget)
        .expect(201)

      expect(response.body.data.widget.name).not.toContain('javascript:')
    })
  })
})