const Joi = require('joi')
const { logger } = require('../../bootstrap/server')

const schemas = {
  dashboard: {
    query: Joi.object({
      tenantId: Joi.string().optional(),
      refresh: Joi.boolean().optional().default(false),
      includeWidgets: Joi.boolean().optional().default(true),
      includeMetrics: Joi.boolean().optional().default(true)
    })
  },

  widget: {
    create: Joi.object({
      name: Joi.string().min(1).max(100).required(),
      type: Joi.string().valid('metric', 'chart', 'table', 'list', 'gauge', 'progress').required(),
      title: Joi.string().min(1).max(200).required(),
      description: Joi.string().max(500).optional(),
      position: Joi.object({
        x: Joi.number().min(0).required(),
        y: Joi.number().min(0).required(),
        width: Joi.number().min(1).max(12).required(),
        height: Joi.number().min(1).max(12).required()
      }).required(),
      config: Joi.object().required(),
      dataSource: Joi.object({
        type: Joi.string().valid('service', 'api', 'database', 'custom').required(),
        endpoint: Joi.string().required(),
        method: Joi.string().valid('GET', 'POST', 'PUT', 'DELETE').default('GET'),
        headers: Joi.object().optional(),
        params: Joi.object().optional(),
        refreshInterval: Joi.number().min(5000).default(30000)
      }).required(),
      visibility: Joi.object({
        roles: Joi.array().items(Joi.string()).optional(),
        users: Joi.array().items(Joi.string()).optional(),
        tenants: Joi.array().items(Joi.string()).optional()
      }).optional(),
      tenantId: Joi.string().required()
    }),

    update: Joi.object({
      name: Joi.string().min(1).max(100).optional(),
      title: Joi.string().min(1).max(200).optional(),
      description: Joi.string().max(500).optional(),
      position: Joi.object({
        x: Joi.number().min(0).required(),
        y: Joi.number().min(0).required(),
        width: Joi.number().min(1).max(12).required(),
        height: Joi.number().min(1).max(12).required()
      }).optional(),
      config: Joi.object().optional(),
      dataSource: Joi.object({
        type: Joi.string().valid('service', 'api', 'database', 'custom').optional(),
        endpoint: Joi.string().optional(),
        method: Joi.string().valid('GET', 'POST', 'PUT', 'DELETE').optional(),
        headers: Joi.object().optional(),
        params: Joi.object().optional(),
        refreshInterval: Joi.number().min(5000).optional()
      }).optional(),
      visibility: Joi.object({
        roles: Joi.array().items(Joi.string()).optional(),
        users: Joi.array().items(Joi.string()).optional(),
        tenants: Joi.array().items(Joi.string()).optional()
      }).optional()
    })
  },

  metrics: {
    query: Joi.object({
      tenantId: Joi.string().optional(),
      serviceId: Joi.string().optional(),
      metricType: Joi.string().valid('performance', 'usage', 'business', 'technical').optional(),
      timeRange: Joi.string().valid('1h', '6h', '24h', '7d', '30d', '90d').default('24h'),
      granularity: Joi.string().valid('1m', '5m', '15m', '1h', '1d').default('5m'),
      includeRealtime: Joi.boolean().default(false)
    }),

    service: Joi.object({
      serviceId: Joi.string().required()
    })
  },

  analytics: {
    query: Joi.object({
      tenantId: Joi.string().optional(),
      timeRange: Joi.string().valid('7d', '30d', '90d', '1y').default('30d'),
      granularity: Joi.string().valid('hour', 'day', 'week', 'month').default('day'),
      dimensions: Joi.array().items(Joi.string()).optional(),
      metrics: Joi.array().items(Joi.string()).optional(),
      filters: Joi.object().optional()
    }),

    custom: Joi.object({
      name: Joi.string().min(1).max(100).required(),
      description: Joi.string().max(500).optional(),
      query: Joi.object({
        collection: Joi.string().required(),
        pipeline: Joi.array().items(Joi.object()).required(),
        parameters: Joi.object().optional()
      }).required(),
      visualization: Joi.object({
        type: Joi.string().valid('line', 'bar', 'pie', 'table', 'heatmap').required(),
        config: Joi.object().required()
      }).optional(),
      schedule: Joi.object({
        enabled: Joi.boolean().default(false),
        frequency: Joi.string().valid('hourly', 'daily', 'weekly', 'monthly').optional(),
        recipients: Joi.array().items(Joi.string().email()).optional()
      }).optional(),
      tenantId: Joi.string().required()
    })
  },

  alerts: {
    query: Joi.object({
      tenantId: Joi.string().optional(),
      status: Joi.string().valid('active', 'acknowledged', 'resolved').optional(),
      severity: Joi.string().valid('critical', 'warning', 'info').optional(),
      source: Joi.string().optional(),
      limit: Joi.number().min(1).max(100).default(50),
      page: Joi.number().min(1).default(1)
    }),

    create: Joi.object({
      title: Joi.string().min(1).max(200).required(),
      description: Joi.string().max(1000).required(),
      severity: Joi.string().valid('critical', 'warning', 'info').required(),
      source: Joi.string().required(),
      service: Joi.string().required(),
      metadata: Joi.object().optional(),
      tags: Joi.array().items(Joi.string()).optional(),
      tenantId: Joi.string().required()
    }),

    acknowledge: Joi.object({
      acknowledgedBy: Joi.string().required(),
      note: Joi.string().max(500).optional()
    }),

    resolve: Joi.object({
      resolvedBy: Joi.string().required(),
      resolution: Joi.string().min(1).max(1000).required()
    })
  },

  reports: {
    query: Joi.object({
      tenantId: Joi.string().optional(),
      type: Joi.string().valid('analytics', 'financial', 'usage', 'performance', 'health', 'custom').optional(),
      status: Joi.string().valid('pending', 'generating', 'completed', 'failed').optional(),
      page: Joi.number().min(1).default(1),
      limit: Joi.number().min(1).max(100).default(20)
    }),

    create: Joi.object({
      name: Joi.string().min(1).max(100).required(),
      description: Joi.string().max(500).optional(),
      type: Joi.string().valid('analytics', 'financial', 'usage', 'performance', 'health', 'custom').required(),
      format: Joi.string().valid('pdf', 'csv', 'excel', 'json').required(),
      parameters: Joi.object().required(),
      schedule: Joi.object({
        frequency: Joi.string().valid('daily', 'weekly', 'monthly').optional(),
        time: Joi.string().pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/).optional(),
        day: Joi.number().min(1).max(31).optional()
      }).optional(),
      recipients: Joi.array().items(Joi.string().email()).optional(),
      retention: Joi.number().min(1).max(365).default(90),
      tenantId: Joi.string().required()
    }),

    export: Joi.object({
      format: Joi.string().valid('csv', 'json', 'pdf', 'excel').required(),
      email: Joi.string().email().optional(),
      filters: Joi.object().optional()
    }),

    data: Joi.object({
      format: Joi.string().valid('json', 'csv').default('json'),
      filters: Joi.object().optional()
    })
  },

  health: {
    serviceId: Joi.string().required(),
    trigger: Joi.object({
      force: Joi.boolean().default(false),
      timeout: Joi.number().min(1000).max(60000).default(10000)
    })
  }
}

function validate(schema, source = 'body') {
  return (req, res, next) => {
    const validationSchema = schema[source] || schema
    const { error, value } = validationSchema.validate(req[source], {
      abortEarly: false,
      allowUnknown: false,
      stripUnknown: true
    })

    if (error) {
      const validationErrors = error.details.map(detail => ({
        field: detail.path.join('.'),
        message: detail.message.replace(/['"]/g, ''),
        value: detail.context?.value
      }))

      logger.warn('Validation failed', {
        url: req.originalUrl,
        method: req.method,
        errors: validationErrors,
        ip: req.ip,
        userAgent: req.get('User-Agent')
      })

      return res.status(400).json({
        error: 'Validation failed',
        message: 'Request validation failed',
        details: validationErrors,
        timestamp: new Date().toISOString()
      })
    }

    // Set validated values back to request
    req[source] = value
    next()
  }
}

function validateParams(schema) {
  return validate(schema, 'params')
}

function validateQuery(schema) {
  return validate(schema, 'query')
}

function validateBody(schema) {
  return validate(schema, 'body')
}

function sanitizeInput(req, res, next) {
  // XSS prevention
  const sanitizeString = (str) => {
    if (typeof str !== 'string') return str
    return str.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
      .replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, '')
      .replace(/javascript:/gi, '')
      .replace(/on\w+\s*=/gi, '')
  }

  const sanitizeObject = (obj) => {
    if (typeof obj !== 'object' || obj === null) return obj

    for (const key in obj) {
      if (typeof obj[key] === 'string') {
        obj[key] = sanitizeString(obj[key])
      } else if (typeof obj[key] === 'object') {
        obj[key] = sanitizeObject(obj[key])
      }
    }
    return obj
  }

  try {
    req.query = sanitizeObject(req.query)
    req.body = sanitizeObject(req.body)
    req.params = sanitizeObject(req.params)
    next()
  } catch (error) {
    logger.error('Input sanitization failed', error)
    res.status(400).json({
      error: 'Invalid input',
      message: 'Input contains invalid characters'
    })
  }
}

function checkTenantAccess(req, res, next) {
  const tenantId = req.query.tenantId || req.body?.tenantId || req.params.tenantId
  const userTenants = req.user?.tenants || ['default'] // Would come from JWT/auth

  if (!tenantId) {
    return next() // No tenant restriction
  }

  if (!userTenants.includes(tenantId) && !userTenants.includes('*')) {
    logger.warn('Unauthorized tenant access attempt', {
      tenantId,
      userTenants,
      ip: req.ip,
      userAgent: req.get('User-Agent')
    })

    return res.status(403).json({
      error: 'Access denied',
      message: 'You do not have access to this tenant\'s data'
    })
  }

  next()
}

function validateApiKey(req, res, next) {
  const apiKey = req.get('X-API-Key')

  if (!apiKey) {
    return res.status(401).json({
      error: 'API key required',
      message: 'Please provide a valid API key in X-API-Key header'
    })
  }

  // In production, validate against stored API keys
  // For now, just check format
  if (!/^gogidix_[a-zA-Z0-9]{32}$/.test(apiKey)) {
    return res.status(401).json({
      error: 'Invalid API key',
      message: 'API key format is invalid'
    })
  }

  req.apiKey = apiKey
  next()
}

module.exports = {
  schemas,
  validate,
  validateParams,
  validateQuery,
  validateBody,
  sanitizeInput,
  checkTenantAccess,
  validateApiKey
}