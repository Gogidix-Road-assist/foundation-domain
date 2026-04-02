const { logger } = require('../../bootstrap/server')
const { getCache, setCache } = require('../../infrastructure/cache/redis')

// Error classification
const ErrorTypes = {
  VALIDATION: 'validation',
  AUTHENTICATION: 'authentication',
  AUTHORIZATION: 'authorization',
  NOT_FOUND: 'not_found',
  BUSINESS: 'business',
  EXTERNAL_SERVICE: 'external_service',
  DATABASE: 'database',
  RATE_LIMIT: 'rate_limit',
  SYSTEM: 'system'
}

// Custom error classes
class AppError extends Error {
  constructor(message, statusCode = 500, type = ErrorTypes.SYSTEM, details = null) {
    super(message)
    this.name = 'AppError'
    this.statusCode = statusCode
    this.type = type
    this.details = details
    this.isOperational = true
    this.timestamp = new Date().toISOString()
    Error.captureStackTrace(this, this.constructor)
  }
}

class ValidationError extends AppError {
  constructor(message, details = null) {
    super(message, 400, ErrorTypes.VALIDATION, details)
    this.name = 'ValidationError'
  }
}

class AuthenticationError extends AppError {
  constructor(message = 'Authentication failed') {
    super(message, 401, ErrorTypes.AUTHENTICATION)
    this.name = 'AuthenticationError'
  }
}

class AuthorizationError extends AppError {
  constructor(message = 'Access denied') {
    super(message, 403, ErrorTypes.AUTHORIZATION)
    this.name = 'AuthorizationError'
  }
}

class NotFoundError extends AppError {
  constructor(message = 'Resource not found') {
    super(message, 404, ErrorTypes.NOT_FOUND)
    this.name = 'NotFoundError'
  }
}

class BusinessError extends AppError {
  constructor(message, details = null) {
    super(message, 422, ErrorTypes.BUSINESS, details)
    this.name = 'BusinessError'
  }
}

class ExternalServiceError extends AppError {
  constructor(service, message, originalError = null) {
    super(`${service} service error: ${message}`, 502, ErrorTypes.EXTERNAL_SERVICE)
    this.name = 'ExternalServiceError'
    this.service = service
    this.originalError = originalError
  }
}

class DatabaseError extends AppError {
  constructor(operation, message, originalError = null) {
    super(`Database ${operation} error: ${message}`, 500, ErrorTypes.DATABASE)
    this.name = 'DatabaseError'
    this.operation = operation
    this.originalError = originalError
  }
}

class RateLimitError extends AppError {
  constructor(retryAfter) {
    super('Rate limit exceeded', 429, ErrorTypes.RATE_LIMIT)
    this.name = 'RateLimitError'
    this.retryAfter = retryAfter
  }
}

// Circuit breaker pattern for external services
class CircuitBreaker {
  constructor(options = {}) {
    this.failureThreshold = options.failureThreshold || 5
    this.resetTimeout = options.resetTimeout || 60000
    this.monitoringPeriod = options.monitoringPeriod || 10000
    this.state = 'CLOSED' // CLOSED, OPEN, HALF_OPEN
    this.failureCount = 0
    this.successCount = 0
    this.lastFailureTime = null
    this.nextAttempt = null
  }

  async execute(operation, context = {}) {
    const key = `circuit:${context.service || 'unknown'}`

    if (this.state === 'OPEN') {
      if (Date.now() < this.nextAttempt) {
        throw new ExternalServiceError(
          context.service || 'Unknown',
          'Circuit breaker is OPEN',
          new Error('Service temporarily unavailable')
        )
      } else {
        this.state = 'HALF_OPEN'
        logger.info('Circuit breaker moving to HALF_OPEN', { service: context.service })
      }
    }

    try {
      const result = await operation()
      await this.onSuccess(key, context)
      return result
    } catch (error) {
      await this.onFailure(key, context, error)
      throw error
    }
  }

  async onSuccess(key, context) {
    if (this.state === 'HALF_OPEN') {
      this.successCount++
      if (this.successCount >= 2) {
        this.state = 'CLOSED'
        this.failureCount = 0
        this.successCount = 0
        logger.info('Circuit breaker moving to CLOSED', { service: context.service })
      }
    } else if (this.state === 'CLOSED') {
      this.failureCount = 0
    }

    await setCache(key, JSON.stringify({
      state: this.state,
      failureCount: this.failureCount,
      successCount: this.successCount,
      lastFailureTime: this.lastFailureTime
    }), 3600)
  }

  async onFailure(key, context, error) {
    this.failureCount++
    this.lastFailureTime = Date.now()

    if (this.state === 'HALF_OPEN' ||
        (this.state === 'CLOSED' && this.failureCount >= this.failureThreshold)) {
      this.state = 'OPEN'
      this.nextAttempt = Date.now() + this.resetTimeout
      logger.error('Circuit breaker moving to OPEN', {
        service: context.service,
        failureCount: this.failureCount,
        nextAttempt: new Date(this.nextAttempt).toISOString()
      })
    }

    await setCache(key, JSON.stringify({
      state: this.state,
      failureCount: this.failureCount,
      successCount: this.successCount,
      lastFailureTime: this.lastFailureTime,
      nextAttempt: this.nextAttempt
    }), 3600)
  }
}

// Global circuit breakers for different services
const circuitBreakers = {
  mongodb: new CircuitBreaker({ failureThreshold: 3, resetTimeout: 30000 }),
  redis: new CircuitBreaker({ failureThreshold: 5, resetTimeout: 10000 }),
  externalApis: new CircuitBreaker({ failureThreshold: 3, resetTimeout: 60000 })
}

// Error handling middleware
function errorHandler(error, req, res, next) {
  // Don't send response if headers already sent
  if (res.headersSent) {
    return next(error)
  }

  let err = { ...error }
  err.message = error.message

  // Log error details
  const errorContext = {
    url: req.originalUrl,
    method: req.method,
    ip: req.ip,
    userAgent: req.get('User-Agent'),
    userId: req.user?.id,
    tenantId: req.query.tenantId || req.body?.tenantId,
    timestamp: new Date().toISOString(),
    stack: error.stack
  }

  // Handle different error types
  if (err.name === 'ValidationError') {
    handleValidationError(err, res, errorContext)
  } else if (err.name === 'CastError') {
    handleCastError(err, res, errorContext)
  } else if (err.code === 11000) {
    handleDuplicateFieldsError(err, res, errorContext)
  } else if (err.name === 'JsonWebTokenError') {
    handleJWTError(res, errorContext)
  } else if (err.name === 'TokenExpiredError') {
    handleJWTExpiredError(res, errorContext)
  } else if (err instanceof AppError) {
    handleAppError(err, res, errorContext)
  } else {
    handleUnexpectedError(err, res, errorContext)
  }
}

function handleValidationError(err, res, context) {
  const errors = Object.values(err.errors).map(e => ({
    field: e.path,
    message: e.message
  }))

  logger.warn('Validation error', { ...context, errors })

  res.status(400).json({
    error: 'Validation failed',
    message: 'Invalid input data',
    type: ErrorTypes.VALIDATION,
    details: errors,
    timestamp: new Date().toISOString(),
    requestId: context.requestId
  })
}

function handleCastError(err, res, context) {
  logger.warn('Cast error', { ...context, value: err.value })

  res.status(400).json({
    error: 'Invalid data format',
    message: `Invalid ${err.path}: ${err.value}`,
    type: ErrorTypes.VALIDATION,
    timestamp: new Date().toISOString(),
    requestId: context.requestId
  })
}

function handleDuplicateFieldsError(err, res, context) {
  const field = Object.keys(err.keyValue)[0]
  const value = err.keyValue[field]

  logger.warn('Duplicate field error', { ...context, field, value })

  res.status(409).json({
    error: 'Duplicate entry',
    message: `${field} with value '${value}' already exists`,
    type: ErrorTypes.BUSINESS,
    timestamp: new Date().toISOString(),
    requestId: context.requestId
  })
}

function handleJWTError(res, context) {
  logger.warn('JWT error', context)

  res.status(401).json({
    error: 'Authentication failed',
    message: 'Invalid authentication token',
    type: ErrorTypes.AUTHENTICATION,
    timestamp: new Date().toISOString(),
    requestId: context.requestId
  })
}

function handleJWTExpiredError(res, context) {
  logger.warn('JWT expired error', context)

  res.status(401).json({
    error: 'Authentication expired',
    message: 'Authentication token has expired',
    type: ErrorTypes.AUTHENTICATION,
    timestamp: new Date().toISOString(),
    requestId: context.requestId
  })
}

function handleAppError(err, res, context) {
  const response = {
    error: err.name,
    message: err.message,
    type: err.type,
    timestamp: err.timestamp,
    requestId: context.requestId
  }

  if (err.details) {
    response.details = err.details
  }

  if (err.retryAfter) {
    res.set('Retry-After', err.retryAfter)
  }

  const logLevel = err.statusCode >= 500 ? 'error' : 'warn'
  logger[logLevel]('Application error', { ...context, error: err })

  res.status(err.statusCode).json(response)
}

function handleUnexpectedError(err, res, context) {
  logger.error('Unexpected error', { ...context, error: err })

  // In production, don't expose stack trace
  const isDevelopment = process.env.NODE_ENV === 'development'

  res.status(500).json({
    error: 'Internal server error',
    message: 'An unexpected error occurred',
    type: ErrorTypes.SYSTEM,
    timestamp: new Date().toISOString(),
    requestId: context.requestId,
    ...(isDevelopment && { stack: err.stack })
  })
}

// Graceful shutdown handler
class GracefulShutdown {
  constructor() {
    this.shutdown = false
    this.activeConnections = new Set()
    this.maxShutdownTime = 30000 // 30 seconds
  }

  addConnection(req) {
    if (this.shutdown) {
      return false
    }
    this.activeConnections.add(req)
    return true
  }

  removeConnection(req) {
    this.activeConnections.delete(req)
  }

  async shutdownServer(server) {
    logger.info('Starting graceful shutdown')
    this.shutdown = true

    // Stop accepting new connections
    server.close(async () => {
      logger.info('HTTP server closed')

      // Wait for active connections to finish
      const startTime = Date.now()
      while (this.activeConnections.size > 0 && Date.now() - startTime < this.maxShutdownTime) {
        await new Promise(resolve => setTimeout(resolve, 1000))
        logger.info(`Waiting for ${this.activeConnections.size} active connections`)
      }

      if (this.activeConnections.size > 0) {
        logger.warn(`Force closing ${this.activeConnections.size} active connections`)
      }

      logger.info('Graceful shutdown complete')
      process.exit(0)
    })

    // Force shutdown after timeout
    setTimeout(() => {
      logger.error('Graceful shutdown timeout, forcing exit')
      process.exit(1)
    }, this.maxShutdownTime)
  }
}

// Error recovery utilities
class ErrorRecovery {
  static async retry(operation, options = {}) {
    const {
      retries = 3,
      delay = 1000,
      backoff = 2,
      condition = (err) => true
    } = options

    let lastError

    for (let i = 0; i <= retries; i++) {
      try {
        return await operation()
      } catch (error) {
        lastError = error

        if (!condition(error) || i === retries) {
          throw error
        }

        const waitTime = delay * Math.pow(backoff, i)
        logger.warn(`Retrying operation in ${waitTime}ms (attempt ${i + 1}/${retries})`, {
          error: error.message
        })

        await new Promise(resolve => setTimeout(resolve, waitTime))
      }
    }

    throw lastError
  }

  static async withFallback(primary, fallback, context = {}) {
    try {
      return await primary()
    } catch (primaryError) {
      logger.warn('Primary operation failed, using fallback', {
        context,
        error: primaryError.message
      })

      try {
        return await fallback()
      } catch (fallbackError) {
        logger.error('Both primary and fallback operations failed', {
          context,
          primaryError: primaryError.message,
          fallbackError: fallbackError.message
        })

        throw new AppError(
          'All operations failed',
          500,
          ErrorTypes.SYSTEM,
          {
            primary: primaryError.message,
            fallback: fallbackError.message
          }
        )
      }
    }
  }
}

// Async error wrapper
function asyncHandler(fn) {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next)
  }
}

module.exports = {
  errorHandler,
  asyncHandler,
  GracefulShutdown,
  ErrorRecovery,
  CircuitBreaker,
  circuitBreakers,
  ErrorTypes,
  AppError,
  ValidationError,
  AuthenticationError,
  AuthorizationError,
  NotFoundError,
  BusinessError,
  ExternalServiceError,
  DatabaseError,
  RateLimitError
}