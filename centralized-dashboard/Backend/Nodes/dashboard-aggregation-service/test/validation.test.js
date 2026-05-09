const test = require('node:test')
const assert = require('node:assert/strict')
const { validate, validateBody, validateQuery, schemas } = require('../src/adapters/in/http/middleware/validation')

test.describe('Validation Middleware - Widget Schema', () => {
  test('should validate valid widget creation request', (t) => {
    const mockReq = {
      body: {
        name: 'Test Widget',
        type: 'chart',
        title: 'Test Chart',
        description: 'A test chart widget',
        position: {
          x: 0,
          y: 0,
          width: 6,
          height: 4
        },
        config: {
          chartType: 'line',
          showLegend: true
        },
        dataSource: {
          type: 'service',
          endpoint: '/api/v1/metrics/summary',
          method: 'GET',
          refreshInterval: 30000
        },
        tenantId: 'tenant-123'
      }
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateBody(schemas.widget.create)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
    assert.strictEqual(mockReq.body.name, 'Test Widget')
  })

  test('should reject widget creation with missing required fields', (t) => {
    const mockReq = {
      body: {
        name: 'Test Widget'
        // Missing required fields: type, title, position, config, dataSource, tenantId
      }
    }

    let statusCode = 200
    let responseBody = null
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: (data) => {
        responseBody = data
        return mockRes
      }
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.widget.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
    assert.ok(responseBody)
    assert.strictEqual(responseBody.error, 'Validation failed')
    assert.ok(Array.isArray(responseBody.details))
    assert.ok(responseBody.details.length > 0)
  })

  test('should reject widget with invalid position dimensions', (t) => {
    const mockReq = {
      body: {
        name: 'Test Widget',
        type: 'chart',
        title: 'Test Chart',
        position: {
          x: 0,
          y: 0,
          width: 15, // Invalid: max is 12
          height: 4
        },
        config: {},
        dataSource: {
          type: 'service',
          endpoint: '/api/v1/metrics'
        },
        tenantId: 'tenant-123'
      }
    }

    let statusCode = 200
    let responseBody = null
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: (data) => {
        responseBody = data
        return mockRes
      }
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.widget.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
    assert.ok(responseBody.details.some(d => d.field === 'position.width'))
  })

  test('should reject widget with invalid refresh interval', (t) => {
    const mockReq = {
      body: {
        name: 'Test Widget',
        type: 'chart',
        title: 'Test Chart',
        position: {
          x: 0,
          y: 0,
          width: 6,
          height: 4
        },
        config: {},
        dataSource: {
          type: 'service',
          endpoint: '/api/v1/metrics',
          refreshInterval: 1000 // Invalid: minimum is 5000
        },
        tenantId: 'tenant-123'
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: () => mockRes
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.widget.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
  })
})

test.describe('Validation Middleware - Analytics Query Schema', () => {
  test('should validate analytics query with valid parameters', (t) => {
    const mockReq = {
      query: {
        timeRange: '7d',
        granularity: 'day',
        tenantId: 'tenant-123'
      }
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateQuery(schemas.analytics.query)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
  })

  test('should apply default values for optional parameters', (t) => {
    const mockReq = {
      query: {} // No parameters provided
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateQuery(schemas.analytics.query)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
    assert.strictEqual(mockReq.query.timeRange, '30d') // Default value
    assert.strictEqual(mockReq.query.granularity, 'day') // Default value
  })

  test('should reject invalid timeRange value', (t) => {
    const mockReq = {
      query: {
        timeRange: '2weeks' // Invalid: must be 7d, 30d, 90d, or 1y
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: () => mockRes
    }
    const mockNext = () => {}

    const middleware = validateQuery(schemas.analytics.query)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
  })
})

test.describe('Validation Middleware - Alert Creation Schema', () => {
  test('should validate valid alert creation request', (t) => {
    const mockReq = {
      body: {
        title: 'High CPU Usage',
        description: 'CPU usage exceeds 80%',
        severity: 'critical',
        source: 'monitoring',
        service: 'api-gateway',
        tenantId: 'tenant-123'
      }
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateBody(schemas.alerts.create)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
  })

  test('should reject alert with invalid severity', (t) => {
    const mockReq = {
      body: {
        title: 'Test Alert',
        description: 'Test description',
        severity: 'urgent', // Invalid: must be critical, warning, or info
        source: 'monitoring',
        service: 'api-gateway',
        tenantId: 'tenant-123'
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: () => mockRes
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.alerts.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
  })

  test('should reject alert with invalid email in schedule', (t) => {
    const mockReq = {
      body: {
        title: 'Test Alert',
        description: 'Test description',
        severity: 'warning',
        source: 'monitoring',
        service: 'api-gateway',
        schedule: {
          enabled: true,
          frequency: 'daily',
          recipients: ['invalid-email', 'not-an-email'] // Invalid email format
        },
        tenantId: 'tenant-123'
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: () => mockRes
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.alerts.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
  })
})

test.describe('Validation Middleware - Report Creation Schema', () => {
  test('should validate valid report creation request', (t) => {
    const mockReq = {
      body: {
        name: 'Monthly Analytics Report',
        description: 'Monthly usage and performance analytics',
        type: 'analytics',
        format: 'pdf',
        parameters: {
          timeRange: '30d',
          includeCharts: true,
          includeTables: true
        },
        tenantId: 'tenant-123'
      }
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateBody(schemas.reports.create)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
  })

  test('should reject report with invalid format', (t) => {
    const mockReq = {
      body: {
        name: 'Test Report',
        type: 'analytics',
        format: 'docx', // Invalid: must be pdf, csv, excel, or json
        parameters: {},
        tenantId: 'tenant-123'
      }
    }

    let statusCode = 200
    const mockRes = {
      status: (code) => {
        statusCode = code
        return mockRes
      },
      json: () => mockRes
    }
    const mockNext = () => {}

    const middleware = validateBody(schemas.reports.create)
    middleware(mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
  })

  test('should validate report export request', (t) => {
    const mockReq = {
      body: {
        format: 'csv',
        filters: {
          dateRange: '30d'
        }
      }
    }

    let validated = false
    const mockRes = {
      status: () => mockRes,
      json: () => mockRes
    }
    const mockNext = () => { validated = true }

    const middleware = validateBody(schemas.reports.export)
    middleware(mockReq, mockRes, mockNext)

    assert.ok(validated)
  })
})
