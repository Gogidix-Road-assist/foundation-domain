const { logger } = require('../src/bootstrap/server')

// Suppress logs during tests
logger.transports.forEach(transport => {
  if (transport.level) {
    transport.level = 'error'
  }
})

// Global test utilities
global.testUtils = {
  generateRandomString: (length = 10) => {
    return Math.random().toString(36).substring(2, 2 + length)
  },

  generateValidWidget: (overrides = {}) => {
    return {
      name: `Test Widget ${global.testUtils.generateRandomString()}`,
      type: 'metric',
      title: 'Test Metric Widget',
      position: {
        x: 0,
        y: 0,
        width: 4,
        height: 3
      },
      config: {
        metric: 'response_time'
      },
      dataSource: {
        type: 'service',
        endpoint: '/api/v1/metrics',
        method: 'GET'
      },
      tenantId: 'test-tenant',
      ...overrides
    }
  },

  generateValidReport: (overrides = {}) => {
    return {
      name: `Test Report ${global.testUtils.generateRandomString()}`,
      type: 'analytics',
      format: 'pdf',
      parameters: {
        timeRange: '7d'
      },
      tenantId: 'test-tenant',
      ...overrides
    }
  },

  generateValidAlert: (overrides = {}) => {
    return {
      title: `Test Alert ${global.testUtils.generateRandomString()}`,
      description: 'Test alert description',
      severity: 'warning',
      source: 'test',
      service: 'test-service',
      tenantId: 'test-tenant',
      ...overrides
    }
  },

  // Helper to wait for async operations
  wait: (ms = 100) => new Promise(resolve => setTimeout(resolve, ms)),

  // Helper to create mock request object
  mockRequest: (overrides = {}) => ({
    method: 'GET',
    url: '/test',
    path: '/test',
    query: {},
    params: {},
    body: {},
    headers: {},
    ip: '127.0.0.1',
    user: {
      id: 'test-user',
      tenantId: 'test-tenant'
    },
    ...overrides
  }),

  // Helper to create mock response object
  mockResponse: () => {
    const res = {
      statusCode: 200,
      headers: {},
      data: null,

      status(code) {
        this.statusCode = code
        return this
      },

      json(data) {
        this.data = data
        return this
      },

      set(name, value) {
        if (typeof name === 'object') {
          this.headers = { ...this.headers, ...name }
        } else {
          this.headers[name] = value
        }
        return this
      },

      get(name) {
        return this.headers[name]
      },

      getHeaders() {
        return this.headers
      }
    }

    return res
  }
}

// Extend Jest matchers
expect.extend({
  toBeValidWidget(received) {
    const required = ['name', 'type', 'title', 'position', 'config', 'dataSource', 'tenantId']
    const missing = required.filter(field => !(field in received))

    if (missing.length === 0) {
      return {
        message: () => `expected ${received} not to be a valid widget`,
        pass: true
      }
    }

    return {
      message: () => `expected widget to have fields: ${missing.join(', ')}`,
      pass: false
    }
  },

  toHaveValidMetrics(received) {
    if (!Array.isArray(received)) {
      return {
        message: () => `expected ${received} to be an array`,
        pass: false
      }
    }

    const hasValidMetrics = received.every(metric =>
      metric.name && metric.value && metric.timestamp
    )

    if (hasValidMetrics) {
      return {
        message: () => `expected metrics not to be valid`,
        pass: true
      }
    }

    return {
      message: () => `expected all metrics to have name, value, and timestamp`,
      pass: false
    }
  },

  toBeWithinTimeRange(received, start, end) {
    const timestamp = new Date(received).getTime()
    const startTime = new Date(start).getTime()
    const endTime = new Date(end).getTime()

    if (timestamp >= startTime && timestamp <= endTime) {
      return {
        message: () => `expected ${received} not to be within time range`,
        pass: true
      }
    }

    return {
      message: () => `expected ${received} to be between ${start} and ${end}`,
      pass: false
    }
  }
})

// Clean up after each test
afterEach(async () => {
  // Clean up any test data if needed
})