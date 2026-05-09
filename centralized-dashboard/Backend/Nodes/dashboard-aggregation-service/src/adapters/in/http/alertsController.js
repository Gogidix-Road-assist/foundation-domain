const { body, param, query, validationResult } = require('express-validator')
const { v4: uuidv4 } = require('uuid')
const { logger } = require('../../bootstrap/server')
const { getCache, setCache, deleteCache } = require('../../infrastructure/cache/redis')
const { broadcastRealTimeUpdate } = require('../../infrastructure/scheduler')

async function getAlerts(req, res) {
  try {
    const { tenantId, status, severity, page = 1, limit = 50 } = req.query
    const cacheKey = `alerts:${tenantId || 'default'}:${status || 'all'}:${severity || 'all'}:${page}:${limit}`

    const cachedAlerts = await getCache(cacheKey)
    if (cachedAlerts) {
      return res.json(cachedAlerts)
    }

    const filters = { tenantId: tenantId || 'default', status, severity }
    const alerts = await getAlertsFromStorage(filters, page, limit)

    const response = {
      alerts: alerts.data,
      pagination: {
        page: parseInt(page),
        limit: parseInt(limit),
        total: alerts.total,
        pages: Math.ceil(alerts.total / limit)
      },
      summary: {
        total: alerts.total,
        critical: alerts.critical,
        warning: alerts.warning,
        info: alerts.info
      }
    }

    await setCache(cacheKey, response, 60)

    res.json(response)
  } catch (error) {
    logger.error('Error getting alerts:', error)
    res.status(500).json({
      error: 'Failed to retrieve alerts',
      message: error.message
    })
  }
}

async function getAlert(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { alertId } = req.params
    const cacheKey = `alert:${alertId}`

    const cachedAlert = await getCache(cacheKey)
    if (cachedAlert) {
      return res.json(cachedAlert)
    }

    const alert = await getAlertFromStorage(alertId)

    if (!alert) {
      return res.status(404).json({
        error: 'Alert not found',
        alertId
      })
    }

    await setCache(cacheKey, alert, 300)

    res.json(alert)
  } catch (error) {
    logger.error('Error getting alert:', error)
    res.status(500).json({
      error: 'Failed to retrieve alert',
      message: error.message
    })
  }
}

async function createAlert(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const alertData = req.body
    const alert = {
      id: uuidv4(),
      ...alertData,
      status: 'active',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      acknowledgedAt: null,
      acknowledgedBy: null,
      resolvedAt: null,
      resolvedBy: null
    }

    await saveAlertToStorage(alert)

    const cacheKey = `alert:${alert.id}`
    await setCache(cacheKey, alert, 3600)

    await invalidateAlertsCache(alert.tenantId)

    broadcastRealTimeUpdate({
      type: 'alert-created',
      data: alert,
      timestamp: new Date().toISOString()
    })

    res.status(201).json(alert)
  } catch (error) {
    logger.error('Error creating alert:', error)
    res.status(500).json({
      error: 'Failed to create alert',
      message: error.message
    })
  }
}

async function acknowledgeAlert(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { alertId } = req.params
    const { acknowledgedBy, note } = req.body

    const alert = await getAlertFromStorage(alertId)
    if (!alert) {
      return res.status(404).json({
        error: 'Alert not found',
        alertId
      })
    }

    const updatedAlert = {
      ...alert,
      status: 'acknowledged',
      acknowledgedAt: new Date().toISOString(),
      acknowledgedBy,
      acknowledgedNote: note,
      updatedAt: new Date().toISOString()
    }

    await saveAlertToStorage(updatedAlert)

    const cacheKey = `alert:${alertId}`
    await setCache(cacheKey, updatedAlert, 3600)

    await invalidateAlertsCache(alert.tenantId)

    broadcastRealTimeUpdate({
      type: 'alert-acknowledged',
      data: updatedAlert,
      timestamp: new Date().toISOString()
    })

    res.json(updatedAlert)
  } catch (error) {
    logger.error('Error acknowledging alert:', error)
    res.status(500).json({
      error: 'Failed to acknowledge alert',
      message: error.message
    })
  }
}

async function resolveAlert(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { alertId } = req.params
    const { resolvedBy, resolution } = req.body

    const alert = await getAlertFromStorage(alertId)
    if (!alert) {
      return res.status(404).json({
        error: 'Alert not found',
        alertId
      })
    }

    const updatedAlert = {
      ...alert,
      status: 'resolved',
      resolvedAt: new Date().toISOString(),
      resolvedBy,
      resolution,
      updatedAt: new Date().toISOString()
    }

    await saveAlertToStorage(updatedAlert)

    const cacheKey = `alert:${alertId}`
    await setCache(cacheKey, updatedAlert, 3600)

    await invalidateAlertsCache(alert.tenantId)

    broadcastRealTimeUpdate({
      type: 'alert-resolved',
      data: updatedAlert,
      timestamp: new Date().toISOString()
    })

    res.json(updatedAlert)
  } catch (error) {
    logger.error('Error resolving alert:', error)
    res.status(500).json({
      error: 'Failed to resolve alert',
      message: error.message
    })
  }
}

async function getAlertsFromStorage(filters, page, limit) {
  const cacheKey = `alerts:storage:${JSON.stringify(filters)}`
  const cached = await getCache(cacheKey)

  if (cached) {
    return paginateAlerts(cached, page, limit)
  }

  const mockAlerts = generateMockAlerts(filters)
  await setCache(cacheKey, mockAlerts, 300)

  return paginateAlerts(mockAlerts, page, limit)
}

function generateMockAlerts(filters) {
  const alerts = []
  const count = Math.floor(Math.random() * 50) + 10

  for (let i = 0; i < count; i++) {
    const severities = ['critical', 'warning', 'info']
    const statuses = ['active', 'acknowledged', 'resolved']
    const severity = severities[Math.floor(Math.random() * severities.length)]
    const status = statuses[Math.floor(Math.random() * statuses.length)]

    if (filters.severity && filters.severity !== severity) continue
    if (filters.status && filters.status !== status) continue

    alerts.push({
      id: `alert_${i + 1}`,
      tenantId: filters.tenantId || 'default',
      title: generateAlertTitle(severity),
      description: `This is a ${severity} alert requiring attention.`,
      severity,
      status,
      source: generateAlertSource(),
      service: generateServiceName(),
      createdAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
      acknowledgedAt: status === 'acknowledged' ? new Date().toISOString() : null,
      acknowledgedBy: status === 'acknowledged' ? 'user_' + Math.floor(Math.random() * 100) : null,
      resolvedAt: status === 'resolved' ? new Date().toISOString() : null,
      resolvedBy: status === 'resolved' ? 'user_' + Math.floor(Math.random() * 100) : null,
      metadata: {
        metric: 'response_time',
        threshold: 5000,
        actual: Math.floor(Math.random() * 10000),
        unit: 'ms'
      }
    })
  }

  return alerts
}

function paginateAlerts(alerts, page, limit) {
  const start = (page - 1) * limit
  const end = start + limit
  const paginatedAlerts = alerts.slice(start, end)

  return {
    data: paginatedAlerts,
    total: alerts.length,
    critical: alerts.filter(a => a.severity === 'critical').length,
    warning: alerts.filter(a => a.severity === 'warning').length,
    info: alerts.filter(a => a.severity === 'info').length
  }
}

async function getAlertFromStorage(alertId) {
  const cacheKey = `alert:${alertId}`
  const cached = await getCache(cacheKey)

  if (cached) {
    return cached
  }

  const mockAlert = {
    id: alertId,
    tenantId: 'default',
    title: 'Sample Alert',
    description: 'This is a sample alert',
    severity: 'warning',
    status: 'active',
    source: 'system',
    service: 'dashboard-aggregation-service',
    createdAt: new Date().toISOString()
  }

  await setCache(cacheKey, mockAlert, 300)
  return mockAlert
}

async function saveAlertToStorage(alert) {
  const cacheKey = `alert:${alert.id}`
  await setCache(cacheKey, alert, 3600)
}

async function invalidateAlertsCache(tenantId) {
  const { invalidatePattern } = require('../../infrastructure/cache/redis')
  await invalidatePattern(`alerts:${tenantId}:*`)
}

function generateAlertTitle(severity) {
  const titles = {
    critical: [
      'Service Down',
      'High Error Rate Detected',
      'Database Connection Failed',
      'Memory Usage Critical'
    ],
    warning: [
      'High Response Time',
      'Disk Space Running Low',
      'CPU Usage High',
      'Queue Length Increasing'
    ],
    info: [
      'Service Restarted',
      'Configuration Updated',
      'Scheduled Maintenance',
      'New Deployment'
    ]
  }

  const alertTitles = titles[severity] || titles.info
  return alertTitles[Math.floor(Math.random() * alertTitles.length)]
}

function generateAlertSource() {
  const sources = ['system', 'user', 'automation', 'monitoring', 'external']
  return sources[Math.floor(Math.random() * sources.length)]
}

function generateServiceName() {
  const services = [
    'billing-service',
    'leads-service',
    'notification-service',
    'geolocation-service',
    'config-service'
  ]
  return services[Math.floor(Math.random() * services.length)]
}

module.exports = {
  getAlerts,
  getAlert,
  createAlert,
  acknowledgeAlert,
  resolveAlert
}