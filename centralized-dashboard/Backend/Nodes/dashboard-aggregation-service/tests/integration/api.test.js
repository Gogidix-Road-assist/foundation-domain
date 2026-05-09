const request = require('supertest')
const { app } = require('../../src/bootstrap/server')
const { getCache, setCache, deleteCache } = require('../../src/infrastructure/cache/redis')

describe('API Integration Tests', () => {
  let authToken
  const testTenant = 'integration-test-tenant'

  beforeAll(async () => {
    // Set up test authentication token
    authToken = 'Bearer test-jwt-token'
  })

  beforeEach(async () => {
    // Clear test data
    await deleteCache(`dashboard:${testTenant}:*`)
    await deleteCache(`widgets:${testTenant}:*`)
    await deleteCache(`metrics:${testTenant}:*`)
  })

  describe('Dashboard API Flow', () => {
    test('Complete dashboard workflow', async () => {
      // 1. Get initial dashboard
      const dashboardResponse = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      expect(dashboardResponse.body.success).toBe(true)
      expect(dashboardResponse.body.data).toHaveProperty('dashboard')
      expect(dashboardResponse.body.data).toHaveProperty('widgets')

      // 2. Get dashboard overview
      const overviewResponse = await request(app)
        .get('/api/v1/dashboard/overview')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      expect(overviewResponse.body.success).toBe(true)
      expect(overviewResponse.body.data).toHaveProperty('summary')

      // 3. Create a new widget
      const widgetData = {
        name: 'Integration Test Widget',
        type: 'chart',
        title: 'Test Chart',
        description: 'Widget created during integration testing',
        position: {
          x: 0,
          y: 0,
          width: 6,
          height: 4
        },
        config: {
          chartType: 'line',
          dataSource: 'metrics'
        },
        dataSource: {
          type: 'service',
          endpoint: '/api/v1/metrics/performance',
          method: 'GET',
          refreshInterval: 30000
        },
        tenantId: testTenant
      }

      const createResponse = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send(widgetData)
        .set('Authorization', authToken)
        .expect(201)

      expect(createResponse.body.success).toBe(true)
      const widgetId = createResponse.body.data.widget.id
      expect(widgetId).toBeDefined()

      // 4. Update the widget
      const updateData = {
        name: 'Updated Integration Widget',
        title: 'Updated Test Chart',
        position: {
          x: 1,
          y: 1,
          width: 8,
          height: 5
        }
      }

      const updateResponse = await request(app)
        .put(`/api/v1/dashboard/widgets/${widgetId}`)
        .send(updateData)
        .set('Authorization', authToken)
        .expect(200)

      expect(updateResponse.body.success).toBe(true)
      expect(updateResponse.body.data.widget.name).toBe(updateData.name)

      // 5. Get updated widgets list
      const widgetsResponse = await request(app)
        .get('/api/v1/dashboard/widgets')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      expect(widgetsResponse.body.success).toBe(true)
      const widget = widgetsResponse.body.data.widgets.find(w => w.id === widgetId)
      expect(widget).toBeDefined()
      expect(widget.name).toBe(updateData.name)

      // 6. Delete the widget
      const deleteResponse = await request(app)
        .delete(`/api/v1/dashboard/widgets/${widgetId}`)
        .set('Authorization', authToken)
        .expect(200)

      expect(deleteResponse.body.success).toBe(true)

      // 7. Verify widget is deleted
      await request(app)
        .get(`/api/v1/dashboard/widgets/${widgetId}`)
        .set('Authorization', authToken)
        .expect(404)
    })
  })

  describe('Metrics API Integration', () => {
    test('Metrics aggregation flow', async () => {
      // 1. Get metrics summary
      const summaryResponse = await request(app)
        .get('/api/v1/metrics/summary')
        .query({
          tenantId: testTenant,
          timeRange: '24h'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(summaryResponse.body.success).toBe(true)
      expect(summaryResponse.body.data).toHaveProperty('summary')
      expect(summaryResponse.body.data).toHaveProperty('metrics')

      // 2. Get performance metrics
      const performanceResponse = await request(app)
        .get('/api/v1/metrics/performance')
        .query({
          tenantId: testTenant,
          granularity: '5m'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(performanceResponse.body.success).toBe(true)
      expect(Array.isArray(performanceResponse.body.data.metrics)).toBe(true)

      // 3. Get real-time metrics
      const realtimeResponse = await request(app)
        .get('/api/v1/metrics/realtime')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      expect(realtimeResponse.body.success).toBe(true)
      expect(realtimeResponse.body.data).toHaveProperty('realTime')
    })
  })

  describe('Analytics API Integration', () => {
    test('Analytics workflow', async () => {
      // 1. Get analytics overview
      const overviewResponse = await request(app)
        .get('/api/v1/analytics/overview')
        .query({
          tenantId: testTenant,
          timeRange: '7d'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(overviewResponse.body.success).toBe(true)
      expect(overviewResponse.body.data).toHaveProperty('overview')

      // 2. Create custom analytics
      const customAnalytics = {
        name: 'Test Analytics Report',
        description: 'Custom analytics for integration testing',
        query: {
          collection: 'metrics',
          pipeline: [
            { $match: { tenantId: testTenant } },
            { $group: { _id: '$type', count: { $sum: 1 } } }
          ]
        },
        visualization: {
          type: 'bar',
          config: {
            title: 'Metrics by Type'
          }
        },
        tenantId: testTenant
      }

      const createResponse = await request(app)
        .post('/api/v1/analytics/custom')
        .send(customAnalytics)
        .set('Authorization', authToken)
        .expect(201)

      expect(createResponse.body.success).toBe(true)
      const analyticsId = createResponse.body.data.analytics.id

      // 3. Get custom analytics
      await request(app)
        .get(`/api/v1/analytics/custom/${analyticsId}`)
        .set('Authorization', authToken)
        .expect(200)
    })
  })

  describe('Alerts API Integration', () => {
    test('Alert lifecycle', async () => {
      // 1. Create an alert
      const alertData = {
        title: 'Integration Test Alert',
        description: 'Alert created during integration testing',
        severity: 'warning',
        source: 'integration-test',
        service: 'dashboard-service',
        metadata: {
          test: true,
          timestamp: new Date().toISOString()
        },
        tenantId: testTenant
      }

      const createResponse = await request(app)
        .post('/api/v1/alerts')
        .send(alertData)
        .set('Authorization', authToken)
        .expect(201)

      expect(createResponse.body.success).toBe(true)
      const alertId = createResponse.body.data.alert.id

      // 2. Get alerts list
      const alertsResponse = await request(app)
        .get('/api/v1/alerts')
        .query({
          tenantId: testTenant,
          status: 'active'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(alertsResponse.body.success).toBe(true)
      expect(Array.isArray(alertsResponse.body.data.alerts)).toBe(true)

      // 3. Acknowledge the alert
      const acknowledgeResponse = await request(app)
        .put(`/api/v1/alerts/${alertId}/acknowledge`)
        .send({
          acknowledgedBy: 'test-user',
          note: 'Acknowledged during integration test'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(acknowledgeResponse.body.success).toBe(true)
      expect(acknowledgeResponse.body.data.alert.status).toBe('acknowledged')

      // 4. Resolve the alert
      const resolveResponse = await request(app)
        .put(`/api/v1/alerts/${alertId}/resolve`)
        .send({
          resolvedBy: 'test-user',
          resolution: 'Resolved during integration test'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(resolveResponse.body.success).toBe(true)
      expect(resolveResponse.body.data.alert.status).toBe('resolved')
    })
  })

  describe('Reports API Integration', () => {
    test('Report generation and export', async () => {
      // 1. Create a report
      const reportData = {
        name: 'Integration Test Report',
        description: 'Report for integration testing',
        type: 'analytics',
        format: 'pdf',
        parameters: {
          timeRange: '7d',
          includeCharts: true,
          metrics: ['usage', 'performance']
        },
        recipients: ['test@example.com'],
        retention: 30,
        tenantId: testTenant
      }

      const createResponse = await request(app)
        .post('/api/v1/reports')
        .send(reportData)
        .set('Authorization', authToken)
        .expect(201)

      expect(createResponse.body.success).toBe(true)
      const reportId = createResponse.body.data.report.id

      // 2. Get reports list
      const reportsResponse = await request(app)
        .get('/api/v1/reports')
        .query({
          tenantId: testTenant,
          type: 'analytics'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(reportsResponse.body.success).toBe(true)
      expect(Array.isArray(reportsResponse.body.data.reports)).toBe(true)

      // 3. Get specific report
      const reportDetailResponse = await request(app)
        .get(`/api/v1/reports/${reportId}`)
        .set('Authorization', authToken)
        .expect(200)

      expect(reportDetailResponse.body.success).toBe(true)
      expect(reportDetailResponse.body.data.report.id).toBe(reportId)

      // 4. Get report data
      const dataResponse = await request(app)
        .get(`/api/v1/reports/${reportId}/data`)
        .query({ format: 'json' })
        .set('Authorization', authToken)
        .expect(200)

      expect(dataResponse.body.success).toBe(true)

      // 5. Export report
      const exportResponse = await request(app)
        .post(`/api/v1/reports/${reportId}/export`)
        .send({
          format: 'csv',
          email: 'test@example.com'
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(exportResponse.body.success).toBe(true)
      expect(exportResponse.body.data.format).toBe('csv')
    })
  })

  describe('Service Health Integration', () => {
    test('Health monitoring workflow', async () => {
      // 1. Get all services health
      const allServicesResponse = await request(app)
        .get('/api/v1/services/health')
        .set('Authorization', authToken)
        .expect(200)

      expect(allServicesResponse.body.success).toBe(true)
      expect(Array.isArray(allServicesResponse.body.data.services)).toBe(true)

      // 2. Get specific service health
      const serviceId = allServicesResponse.body.data.services[0]?.id
      if (serviceId) {
        const serviceResponse = await request(app)
          .get(`/api/v1/services/health/${serviceId}`)
          .set('Authorization', authToken)
          .expect(200)

        expect(serviceResponse.body.success).toBe(true)
        expect(serviceResponse.body.data.service.id).toBe(serviceId)
      }

      // 3. Trigger health check
      const triggerResponse = await request(app)
        .post('/api/v1/services/health/check')
        .send({
          serviceIds: ['test-service'],
          force: false
        })
        .set('Authorization', authToken)
        .expect(200)

      expect(triggerResponse.body.success).toBe(true)
    })
  })

  describe('Error Handling Integration', () => {
    test('404 Not Found handling', async () => {
      const response = await request(app)
        .get('/api/v1/nonexistent-endpoint')
        .set('Authorization', authToken)
        .expect(404)

      expect(response.body).toHaveProperty('error', 'Endpoint not found')
      expect(response.body).toHaveProperty('timestamp')
    })

    test('Validation error handling', async () => {
      const response = await request(app)
        .post('/api/v1/dashboard/widgets')
        .send({
          // Invalid widget data
          name: '',
          type: 'invalid'
        })
        .set('Authorization', authToken)
        .expect(400)

      expect(response.body).toHaveProperty('error', 'Validation failed')
      expect(response.body).toHaveProperty('details')
    })

    test('Rate limiting integration', async () => {
      // Make rapid requests
      const requests = Array(105).fill().map(() =>
        request(app)
          .get('/api/v1/dashboard')
          .set('Authorization', authToken)
      )

      const responses = await Promise.allSettled(requests)
      const rateLimited = responses.find(r =>
        r.status === 'fulfilled' && r.value.status === 429
      )

      expect(rateLimited).toBeDefined()
      if (rateLimited.status === 'fulfilled') {
        expect(rateLimited.value.body).toHaveProperty('error', 'Too many requests')
        expect(rateLimited.value.headers).toHaveProperty('retry-after')
      }
    })
  })

  describe('Authentication and Authorization', () => {
    test('Unauthenticated request', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .expect(401)

      expect(response.body).toHaveProperty('error')
    })

    test('API key authentication', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('X-API-Key', 'gogidix_testapikey1234567890123456789012')
        .expect(200)

      expect(response.body.success).toBe(true)
    })

    test('Invalid API key', async () => {
      const response = await request(app)
        .get('/api/v1/dashboard')
        .set('X-API-Key', 'invalid-key')
        .expect(401)

      expect(response.body).toHaveProperty('error', 'Invalid API key')
    })
  })

  describe('Caching Integration', () => {
    test('Response caching', async () => {
      // First request
      const firstResponse = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      const firstTimestamp = firstResponse.body.data.lastUpdated

      // Second request (should use cache)
      const secondResponse = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)
        .expect(200)

      const secondTimestamp = secondResponse.body.data.lastUpdated

      // Cached response should have same timestamp
      expect(firstTimestamp).toBe(secondTimestamp)

      // Verify cache was set
      const cacheKey = `dashboard:${testTenant}:default:true:true`
      const cached = await getCache(cacheKey)
      expect(cached).toBeDefined()
    })

    test('Cache invalidation on update', async () => {
      // Get initial data (populates cache)
      await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: testTenant })
        .set('Authorization', authToken)

      // Update data (should invalidate cache)
      await request(app)
        .post('/api/v1/dashboard/widgets')
        .send({
          name: 'Cache Test Widget',
          type: 'metric',
          title: 'Test',
          position: { x: 0, y: 0, width: 4, height: 3 },
          config: {},
          dataSource: {
            type: 'service',
            endpoint: '/test',
            method: 'GET'
          },
          tenantId: testTenant
        })
        .set('Authorization', authToken)

      // Get data again (should not use stale cache)
      const response = await request(app)
        .get('/api/v1/dashboard')
        .query({ tenantId: testTenant, refresh: true })
        .set('Authorization', authToken)

      expect(response.body).toHaveProperty('message', 'Dashboard refreshed successfully')
    })
  })

  afterAll(async () => {
    // Clean up test data
    await deleteCache(`dashboard:${testTenant}:*`)
    await deleteCache(`widgets:${testTenant}:*`)
    await deleteCache(`metrics:${testTenant}:*`)
    await deleteCache(`alerts:${testTenant}:*`)
    await deleteCache(`reports:${testTenant}:*`)
  })
})