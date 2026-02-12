const test = require('node:test')
const assert = require('node:assert/strict')
const {
  AppError,
  ValidationError,
  AuthenticationError,
  AuthorizationError,
  NotFoundError,
  BusinessError,
  ExternalServiceError,
  DatabaseError,
  RateLimitError,
  errorHandler
} = require('../src/adapters/in/http/middleware/errorHandler')

test.describe('Custom Error Classes', () => {
  test('should create AppError with all properties', () => {
    const error = new AppError('Test error', 500, 'system', { field: 'value' })

    assert.strictEqual(error.message, 'Test error')
    assert.strictEqual(error.statusCode, 500)
    assert.strictEqual(error.type, 'system')
    assert.deepStrictEqual(error.details, { field: 'value' })
    assert.strictEqual(error.isOperational, true)
    assert.ok(error.timestamp)
    assert.ok(error.stack)
  })

  test('should create ValidationError with correct status code', () => {
    const error = new ValidationError('Invalid input', [{ field: 'email', message: 'Invalid format' }])

    assert.strictEqual(error.name, 'ValidationError')
    assert.strictEqual(error.statusCode, 400)
    assert.strictEqual(error.type, 'validation')
    assert.deepStrictEqual(error.details, [{ field: 'email', message: 'Invalid format' }])
  })

  test('should create AuthenticationError with default message', () => {
    const error = new AuthenticationError()

    assert.strictEqual(error.name, 'AuthenticationError')
    assert.strictEqual(error.statusCode, 401)
    assert.strictEqual(error.type, 'authentication')
    assert.strictEqual(error.message, 'Authentication failed')
  })

  test('should create AuthenticationError with custom message', () => {
    const error = new AuthenticationError('Invalid token')

    assert.strictEqual(error.name, 'AuthenticationError')
    assert.strictEqual(error.message, 'Invalid token')
  })

  test('should create AuthorizationError with default message', () => {
    const error = new AuthorizationError()

    assert.strictEqual(error.name, 'AuthorizationError')
    assert.strictEqual(error.statusCode, 403)
    assert.strictEqual(error.type, 'authorization')
    assert.strictEqual(error.message, 'Access denied')
  })

  test('should create NotFoundError with default message', () => {
    const error = new NotFoundError()

    assert.strictEqual(error.name, 'NotFoundError')
    assert.strictEqual(error.statusCode, 404)
    assert.strictEqual(error.type, 'not_found')
    assert.strictEqual(error.message, 'Resource not found')
  })

  test('should create BusinessError with details', () => {
    const error = new BusinessError('Business rule violated', { rule: 'min_balance', threshold: 100 })

    assert.strictEqual(error.name, 'BusinessError')
    assert.strictEqual(error.statusCode, 422)
    assert.strictEqual(error.type, 'business')
    assert.deepStrictEqual(error.details, { rule: 'min_balance', threshold: 100 })
  })

  test('should create ExternalServiceError with service name', () => {
    const originalError = new Error('Connection timeout')
    const error = new ExternalServiceError('PaymentGateway', 'Failed to process payment', originalError)

    assert.strictEqual(error.name, 'ExternalServiceError')
    assert.strictEqual(error.statusCode, 502)
    assert.strictEqual(error.type, 'external_service')
    assert.strictEqual(error.service, 'PaymentGateway')
    assert.ok(error.message.includes('PaymentGateway'))
    assert.strictEqual(error.originalError, originalError)
  })

  test('should create DatabaseError with operation', () => {
    const originalError = new Error('Duplicate key')
    const error = new DatabaseError('insert', 'Failed to insert document', originalError)

    assert.strictEqual(error.name, 'DatabaseError')
    assert.strictEqual(error.statusCode, 500)
    assert.strictEqual(error.type, 'database')
    assert.strictEqual(error.operation, 'insert')
    assert.ok(error.message.includes('insert'))
    assert.strictEqual(error.originalError, originalError)
  })

  test('should create RateLimitError with retry time', () => {
    const error = new RateLimitError(60)

    assert.strictEqual(error.name, 'RateLimitError')
    assert.strictEqual(error.statusCode, 429)
    assert.strictEqual(error.type, 'rate_limit')
    assert.strictEqual(error.retryAfter, 60)
  })
})

test.describe('Error Handler Middleware', () => {
  test('should handle ValidationError correctly', (t) => {
    const mockReq = {
      originalUrl: '/api/v1/widgets',
      method: 'POST',
      ip: '127.0.0.1',
      get: () => 'Mozilla/5.0'
    }

    let statusCode = 200
    let responseData = null
    const mockRes = {
      headersSent: false,
      status: (code) => {
        statusCode = code
        return {
          json: (data) => {
            responseData = data
            return mockRes
          }
        }
      }
    }

    const mockNext = () => {}

    const error = new ValidationError('Validation failed', [
      { field: 'name', message: 'Name is required' }
    ])

    errorHandler(error, mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 400)
    assert.ok(responseData)
    assert.strictEqual(responseData.error, 'Validation failed')
    assert.strictEqual(responseData.type, 'validation')
    assert.ok(Array.isArray(responseData.details))
  })

  test('should handle AppError with custom status code', (t) => {
    const mockReq = {
      originalUrl: '/api/v1/reports',
      method: 'POST',
      ip: '127.0.0.1',
      get: () => 'Mozilla/5.0'
    }

    let statusCode = 200
    let responseData = null
    const mockRes = {
      headersSent: false,
      status: (code) => {
        statusCode = code
        return {
          json: (data) => {
            responseData = data
            return mockRes
          }
        }
      }
    }

    const mockNext = () => {}

    const error = new NotFoundError('Report not found')

    errorHandler(error, mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 404)
    assert.ok(responseData)
    assert.strictEqual(responseData.error, 'NotFoundError')
    assert.strictEqual(responseDta.type, 'not_found')
  })

  test('should handle unexpected errors', (t) => {
    const mockReq = {
      originalUrl: '/api/v1/dashboard',
      method: 'GET',
      ip: '127.0.0.1',
      get: () => 'Mozilla/5.0'
    }

    let statusCode = 200
    let responseData = null
    const mockRes = {
      headersSent: false,
      status: (code) => {
        statusCode = code
        return {
          json: (data) => {
            responseData = data
            return mockRes
          }
        }
      }
    }

    const mockNext = () => {}

    const error = new Error('Unexpected error')

    errorHandler(error, mockReq, mockRes, mockNext)

    assert.strictEqual(statusCode, 500)
    assert.ok(responseData)
    assert.strictEqual(responseData.error, 'Internal server error')
  })

  test('should not send response if headers already sent', (t) => {
    const mockReq = {
      originalUrl: '/api/v1/test',
      method: 'GET',
      ip: '127.0.0.1',
      get: () => 'Mozilla/5.0'
    }

    const mockRes = {
      headersSent: true
    }

    const mockNext = () => {}

    const error = new AppError('Test error')

    // Should not throw and should call next
    errorHandler(error, mockReq, mockRes, mockNext)

    assert.ok(true) // If we get here without throwing, test passes
  })
})

test.describe('Error Recovery Utilities', () => {
  test('should import ErrorRecovery from errorHandler', () => {
    const { ErrorRecovery } = require('../src/adapters/in/http/middleware/errorHandler')

    assert.ok(ErrorRecovery)
    assert.strictEqual(typeof ErrorRecovery.retry, 'function')
    assert.strictEqual(typeof ErrorRecovery.withFallback, 'function')
  })

  test('should import CircuitBreaker from errorHandler', () => {
    const { CircuitBreaker } = require('../src/adapters/in/http/middleware/errorHandler')

    assert.ok(CircuitBreaker)
    assert.strictEqual(typeof CircuitBreaker, 'function')
  })

  test('should import asyncHandler from errorHandler', () => {
    const { asyncHandler } = require('../src/adapters/in/http/middleware/errorHandler')

    assert.strictEqual(typeof asyncHandler, 'function')
  })
})

test.describe('Error Type Constants', () => {
  test('should export all error types', () => {
    const { ErrorTypes } = require('../src/adapters/in/http/middleware/errorHandler')

    assert.strictEqual(ErrorTypes.VALIDATION, 'validation')
    assert.strictEqual(ErrorTypes.AUTHENTICATION, 'authentication')
    assert.strictEqual(ErrorTypes.AUTHORIZATION, 'authorization')
    assert.strictEqual(ErrorTypes.NOT_FOUND, 'not_found')
    assert.strictEqual(ErrorTypes.BUSINESS, 'business')
    assert.strictEqual(ErrorTypes.EXTERNAL_SERVICE, 'external_service')
    assert.strictEqual(ErrorTypes.DATABASE, 'database')
    assert.strictEqual(ErrorTypes.RATE_LIMIT, 'rate_limit')
    assert.strictEqual(ErrorTypes.SYSTEM, 'system')
  })
})
