const { logger } = require('../../bootstrap/server')
const { getCache, incrementCache } = require('../../infrastructure/cache/redis')
const { v4: uuidv4 } = require('uuid')

// Sensitive data patterns to redact
const sensitivePatterns = [
  /password/i,
  /token/i,
  /secret/i,
  /key/i,
  /authorization/i,
  /cookie/i,
  /session/i,
  /credit.?card/i,
  /ssn/i,
  /social.?security/i,
  /bank.?account/i,
  /cvv/i,
  /cvc/i,
  /pin/i
]

// Fields that should always be redacted
const redactedFields = new Set([
  'password',
  'token',
  'secret',
  'key',
  'authorization',
  'cookie',
  'session',
  'creditCard',
  'ssn',
  'socialSecurity',
  'bankAccount',
  'cvv',
  'cvc',
  'pin',
  'apiKey',
  'accessToken',
  'refreshToken',
  'privateKey',
  'publicKey',
  'x-api-key',
  'x-auth-token',
  'x-session-id'
])

class AuditLogger {
  constructor(options = {}) {
    this.logLevel = options.logLevel || 'info'
    this.samplingRate = options.samplingRate || 1.0 // 100% by default
    this.maxPayloadSize = options.maxPayloadSize || 1024 // 1KB
    this.excludePaths = options.excludePaths || ['/health', '/metrics', '/status']
    this.includeBody = options.includeBody !== false
    this.includeHeaders = options.includeHeaders !== false
    this.persistToDatabase = options.persistToDatabase || false
    this.indexFields = options.indexFields || ['userId', 'tenantId', 'service', 'action']
  }

  // Sanitize and redact sensitive data
  sanitize(obj, depth = 0) {
    if (depth > 10) return '[Max Depth Reached]' // Prevent infinite recursion

    if (obj === null || obj === undefined) {
      return obj
    }

    if (typeof obj === 'string') {
      // Check if string matches sensitive patterns
      if (sensitivePatterns.some(pattern => pattern.test(obj))) {
        return '[REDACTED]'
      }
      // Check for potential API keys/tokens (long alphanumeric strings)
      if (obj.length > 30 && /^[a-zA-Z0-9_-]+$/.test(obj)) {
        return '[REDACTED]'
      }
      return obj
    }

    if (typeof obj !== 'object') {
      return obj
    }

    if (Array.isArray(obj)) {
      return obj.map(item => this.sanitize(item, depth + 1))
    }

    const sanitized = {}
    for (const key in obj) {
      const lowerKey = key.toLowerCase()

      // Redact known sensitive fields
      if (redactedFields.has(lowerKey) || redactedFields.has(key)) {
        sanitized[key] = '[REDACTED]'
      }
      // Redact fields matching sensitive patterns
      else if (sensitivePatterns.some(pattern => pattern.test(lowerKey) || pattern.test(key))) {
        sanitized[key] = '[REDACTED]'
      }
      // Recursively sanitize nested objects
      else if (typeof obj[key] === 'object') {
        sanitized[key] = this.sanitize(obj[key], depth + 1)
      }
      // Limit string length
      else if (typeof obj[key] === 'string' && obj[key].length > 100) {
        sanitized[key] = obj[key].substring(0, 100) + '...[TRUNCATED]'
      }
      else {
        sanitized[key] = obj[key]
      }
    }

    return sanitized
  }

  // Generate audit entry
  generateAuditEntry(req, res, responseTime) {
    const requestId = req.id || uuidv4()
    const timestamp = new Date().toISOString()
    const userAgent = req.get('User-Agent') || 'Unknown'

    // Extract user information
    const user = {
      id: req.user?.id || 'anonymous',
      email: req.user?.email || null,
      roles: req.user?.roles || [],
      tenantId: req.query.tenantId || req.body?.tenantId || req.user?.tenantId || 'default'
    }

    // Request information
    const request = {
      method: req.method,
      url: req.originalUrl,
      path: req.path,
      query: this.sanitize(req.query),
      params: this.sanitize(req.params),
      headers: this.includeHeaders ? this.sanitize(req.headers) : undefined,
      body: this.includeBody ? this.sanitize(req.body) : undefined,
      ip: req.ip || req.connection.remoteAddress,
      userAgent,
      timestamp: req.timestamp || timestamp,
      size: req.get('content-length') || 0
    }

    // Response information
    const response = {
      statusCode: res.statusCode,
      headers: this.includeHeaders ? this.sanitize(res.getHeaders()) : undefined,
      size: res.get('content-length') || 0,
      duration: responseTime
    }

    // Additional context
    const context = {
      requestId,
      service: req.service || 'dashboard-aggregation',
      action: this.determineAction(req),
      resource: this.determineResource(req),
      success: res.statusCode < 400,
      error: res.statusCode >= 400 ? {
        message: res.locals.errorMessage,
        type: res.locals.errorType
      } : null
    }

    return {
      timestamp,
      user,
      request,
      response,
      context
    }
  }

  // Determine action type from request
  determineAction(req) {
    const method = req.method.toLowerCase()
    const path = req.path.toLowerCase()

    if (path.includes('/login') || path.includes('/auth')) return 'authenticate'
    if (path.includes('/logout')) return 'logout'
    if (path.includes('/create') || method === 'post') return 'create'
    if (path.includes('/update') || method === 'put' || method === 'patch') return 'update'
    if (path.includes('/delete') || method === 'delete') return 'delete'
    if (path.includes('/export') || path.includes('/download')) return 'export'
    if (path.includes('/upload')) return 'upload'
    if (method === 'get') return 'read'

    return 'unknown'
  }

  // Determine resource type from request path
  determineResource(req) {
    const path = req.path.toLowerCase()

    if (path.includes('/dashboard')) return 'dashboard'
    if (path.includes('/widget')) return 'widget'
    if (path.includes('/metric')) return 'metric'
    if (path.includes('/analytic')) return 'analytics'
    if (path.includes('/alert')) return 'alert'
    if (path.includes('/report')) return 'report'
    if (path.includes('/health')) return 'health'
    if (path.includes('/config')) return 'configuration'
    if (path.includes('/user')) return 'user'
    if (path.includes('/tenant')) return 'tenant'

    return 'unknown'
  }

  // Check if request should be logged
  shouldLog(req) {
    // Skip excluded paths
    if (this.excludePaths.some(path => req.path.startsWith(path))) {
      return false
    }

    // Apply sampling rate
    if (this.samplingRate < 1 && Math.random() > this.samplingRate) {
      return false
    }

    return true
  }

  // Log audit entry
  async log(entry) {
    try {
      // Log to application logger
      logger[this.logLevel]('Audit log', {
        type: 'audit',
        requestId: entry.context.requestId,
        userId: entry.user.id,
        tenantId: entry.user.tenantId,
        action: entry.context.action,
        resource: entry.context.resource,
        method: entry.request.method,
        path: entry.request.path,
        statusCode: entry.response.statusCode,
        duration: entry.response.duration,
        success: entry.context.success
      })

      // Increment metrics
      await this.incrementMetrics(entry)

      // Persist to database if enabled
      if (this.persistToDatabase) {
        await this.persistAuditEntry(entry)
      }
    } catch (error) {
      logger.error('Failed to log audit entry', error)
    }
  }

  // Increment usage metrics
  async incrementMetrics(entry) {
    const keys = [
      `audit:total:${entry.user.tenantId}`,
      `audit:${entry.context.action}:${entry.user.tenantId}`,
      `audit:${entry.context.resource}:${entry.user.tenantId}`,
      `audit:status:${entry.response.statusCode}:${entry.user.tenantId}`
    ]

    const promises = keys.map(key => incrementCache(key, 1, 86400)) // 24 hour TTL
    await Promise.all(promises)
  }

  // Persist audit entry to database
  async persistAuditEntry(entry) {
    // In production, this would save to MongoDB or another database
    // For now, we'll just log it
    logger.debug('Audit entry persisted', {
      requestId: entry.context.requestId,
      timestamp: entry.timestamp
    })
  }

  // Express middleware
  middleware() {
    return async (req, res, next) => {
      // Skip if not configured to log
      if (!this.shouldLog(req)) {
        return next()
      }

      // Add request ID
      req.id = req.id || uuidv4()
      req.timestamp = new Date().toISOString()

      // Capture start time
      const startTime = Date.now()

      // Store original res.end
      const originalEnd = res.end

      // Override res.end to capture response
      res.end = function(chunk, encoding) {
        // Calculate response time
        const responseTime = Date.now() - startTime

        // Generate audit entry
        const auditEntry = this.auditLogger.generateAuditEntry(req, res, responseTime)

        // Log asynchronously
        this.auditLogger.log(auditEntry).catch(error => {
          logger.error('Audit logging failed', error)
        })

        // Call original end
        originalEnd.call(this, chunk, encoding)
      }.bind({ auditLogger: this })

      // Store error messages from error handler
      res.on('error', (error) => {
        res.locals.errorMessage = error.message
        res.locals.errorType = error.type || 'unknown'
      })

      next()
    }
  }
}

// Security event logger
class SecurityLogger {
  constructor() {
    this.threatThresholds = {
      failedLogins: 5,
      suspiciousRequests: 20,
      unauthorizedAccess: 3
    }
  }

  async logSecurityEvent(event, req, details = {}) {
    const securityEvent = {
      timestamp: new Date().toISOString(),
      type: event,
      severity: this.determineSeverity(event),
      source: {
        ip: req.ip || req.connection.remoteAddress,
        userAgent: req.get('User-Agent'),
        userId: req.user?.id || 'anonymous',
        tenantId: req.query.tenantId || req.body?.tenantId || 'default'
      },
      details,
      request: {
        method: req.method,
        url: req.originalUrl,
        path: req.path,
        headers: this.sanitizeHeaders(req.headers)
      }
    }

    logger.warn('Security event detected', securityEvent)

    // Check for threat patterns
    await this.checkThreatPatterns(securityEvent)

    // Store security events for analysis
    await this.storeSecurityEvent(securityEvent)
  }

  determineSeverity(event) {
    const criticalEvents = ['brute_force_attack', 'data_breach_attempt', 'privilege_escalation']
    const warningEvents = ['failed_login', 'suspicious_activity', 'unauthorized_access']

    if (criticalEvents.includes(event)) return 'critical'
    if (warningEvents.includes(event)) return 'warning'
    return 'info'
  }

  sanitizeHeaders(headers) {
    const sanitized = {}
    for (const key in headers) {
      if (!redactedFields.has(key.toLowerCase())) {
        sanitized[key] = headers[key]
      }
    }
    return sanitized
  }

  async checkThreatPatterns(event) {
    const key = `security:${event.source.ip}:${event.type}`
    const count = await incrementCache(key, 1, 3600) // 1 hour window

    const threshold = this.threatThresholds[event.type] || 10
    if (count > threshold) {
      logger.error('Threat threshold exceeded', {
        event,
        ip: event.source.ip,
        count,
        threshold
      })

      // Trigger incident response
      await this.triggerIncidentResponse(event)
    }
  }

  async triggerIncidentResponse(event) {
    // Log critical incident
    logger.error('Security incident triggered', event)

    // In production, this would:
    // 1. Block the IP address
    // 2. Notify security team
    // 3. Enable additional monitoring
    // 4. Potentially lock affected accounts

    // Store incident for review
    const incidentKey = `incident:${event.source.ip}:${Date.now()}`
    await this.storeSecurityEvent({
      ...event,
      type: 'security_incident',
      action: 'automatic_response_triggered'
    })
  }

  async storeSecurityEvent(event) {
    // In production, store in security events collection
    logger.debug('Security event stored', {
      type: event.type,
      ip: event.source.ip,
      timestamp: event.timestamp
    })
  }
}

// Create default instances
const auditLogger = new AuditLogger({
  samplingRate: 1.0,
  maxPayloadSize: 2048,
  excludePaths: ['/health', '/metrics', '/status', '/favicon.ico'],
  persistToDatabase: process.env.AUDIT_PERSIST === 'true'
})

const securityLogger = new SecurityLogger()

// Middleware exports
function auditMiddleware() {
  return auditLogger.middleware()
}

function logSecurityEvent(event, req, details) {
  return securityLogger.logSecurityEvent(event, req, details)
}

module.exports = {
  AuditLogger,
  SecurityLogger,
  auditLogger,
  securityLogger,
  auditMiddleware,
  logSecurityEvent
}