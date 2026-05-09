const { param, body, validationResult } = require('express-validator')
const { logger } = require('../../bootstrap/server')
const {
  getAllServicesHealth,
  getServiceHealth,
  checkServiceHealth,
  getHealthyServices,
  getUnhealthyServices
} = require('../../infrastructure/serviceRegistry')
const { setCache } = require('../../infrastructure/cache/redis')

async function getAllServicesHealth(req, res) {
  try {
    const { detailed = false } = req.query

    const healthData = await getAllServicesHealth()

    const response = {
      timestamp: new Date().toISOString(),
      summary: {
        total: Object.keys(healthData).length,
        healthy: Object.values(healthData).filter(s => s.status === 'healthy').length,
        unhealthy: Object.values(healthData).filter(s => s.status === 'unhealthy').length,
        unknown: Object.values(healthData).filter(s => s.status === 'unknown').length
      },
      services: detailed ? healthData : Object.keys(healthData).map(key => ({
        id: key,
        status: healthData[key].status,
        lastCheck: healthData[key].lastCheck,
        responseTime: healthData[key].responseTime
      }))
    }

    res.json(response)
  } catch (error) {
    logger.error('Error getting all services health:', error)
    res.status(500).json({
      error: 'Failed to retrieve services health',
      message: error.message
    })
  }
}

async function getServiceHealth(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { serviceId } = req.params
    const { history = false } = req.query

    const healthData = await getServiceHealth(serviceId)

    if (!healthData || healthData.status === 'unknown') {
      return res.status(404).json({
        error: 'Service not found',
        serviceId
      })
    }

    const response = {
      serviceId,
      ...healthData,
      additionalInfo: await getServiceAdditionalInfo(serviceId),
      history: history ? await getServiceHealthHistory(serviceId) : []
    }

    res.json(response)
  } catch (error) {
    logger.error('Error getting service health:', error)
    res.status(500).json({
      error: 'Failed to retrieve service health',
      message: error.message
    })
  }
}

async function triggerHealthCheck(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { serviceIds } = req.body
    const checkMultiple = Array.isArray(serviceIds)
    const servicesToCheck = checkMultiple ? serviceIds : [req.body.serviceId]

    const results = []

    for (const serviceId of servicesToCheck) {
      try {
        await checkServiceHealth(serviceId)
        const healthData = await getServiceHealth(serviceId)

        results.push({
          serviceId,
          status: healthData.status || 'unknown',
          responseTime: healthData.responseTime,
          error: healthData.error,
          timestamp: new Date().toISOString()
        })
      } catch (error) {
        results.push({
          serviceId,
          status: 'error',
          error: error.message,
          timestamp: new Date().toISOString()
        })
      }
    }

    const response = {
      timestamp: new Date().toISOString(),
      results: checkMultiple ? results : results[0],
      summary: checkMultiple ? {
        total: results.length,
        healthy: results.filter(r => r.status === 'healthy').length,
        unhealthy: results.filter(r => r.status === 'unhealthy').length,
        errors: results.filter(r => r.status === 'error').length
      } : null
    }

    res.json(response)
  } catch (error) {
    logger.error('Error triggering health check:', error)
    res.status(500).json({
      error: 'Failed to trigger health check',
      message: error.message
    })
  }
}

async function getServiceAdditionalInfo(serviceId) {
  try {
    const serviceKey = serviceId.toUpperCase() + '_SERVICE'

    const additionalInfo = {
      endpoints: await getServiceEndpoints(serviceKey),
      configuration: await getServiceConfiguration(serviceKey),
      dependencies: await getServiceDependencies(serviceKey),
      metrics: await getServiceMetrics(serviceKey)
    }

    return additionalInfo
  } catch (error) {
    logger.error(`Error getting additional info for service ${serviceId}:`, error)
    return {}
  }
}

async function getServiceEndpoints(serviceKey) {
  const commonEndpoints = [
    '/actuator/health',
    '/actuator/metrics',
    '/actuator/info',
    '/actuator/prometheus'
  ]

  return commonEndpoints.map(endpoint => ({
    path: endpoint,
    description: endpoint.split('/')[2] || 'Endpoint',
    methods: ['GET']
  }))
}

async function getServiceConfiguration(serviceKey) {
  const envVarPrefix = serviceKey.toLowerCase().replace('_service', '').replace('_', '-')

  return {
    serviceKey,
    environmentVariables: [
      `${envVarPrefix.toUpperCase()}_URL`,
      `${envVarPrefix.toUpperCase()}_TIMEOUT`,
      `${envVarPrefix.toUpperCase()}_RETRIES`
    ],
    currentConfig: {
      timeout: process.env[`${envVarPrefix.toUpperCase()}_TIMEOUT`] || 'default',
      retries: process.env[`${envVarPrefix.toUpperCase()}_RETRIES`] || 'default'
    }
  }
}

async function getServiceDependencies(serviceKey) {
  const dependencies = {
    BILLING_SERVICE: ['mongodb', 'redis'],
    AI_LEADS_SERVICE: ['mongodb', 'redis', 'openai'],
    NOTIFICATION_SERVICE: ['redis', 'sendgrid', 'twilio'],
    GEOLOCATION_SERVICE: ['mongodb', 'maxmind'],
    CONFIG_SERVICE: ['mongodb', 'redis']
  }

  return dependencies[serviceKey] || []
}

async function getServiceMetrics(serviceKey) {
  const cacheKey = `service:metrics:${serviceKey}`

  return {
    availability: '99.9%',
    avgResponseTime: '150ms',
    requestRate: '1000/min',
    errorRate: '0.1%'
  }
}

async function getServiceHealthHistory(serviceId) {
  const historyKey = `service:health:history:${serviceId}`

  return [
    {
      timestamp: new Date(Date.now() - 3600000).toISOString(),
      status: 'healthy',
      responseTime: 120
    },
    {
      timestamp: new Date(Date.now() - 7200000).toISOString(),
      status: 'healthy',
      responseTime: 135
    },
    {
      timestamp: new Date(Date.now() - 10800000).toISOString(),
      status: 'unhealthy',
      responseTime: null,
      error: 'Connection timeout'
    }
  ]
}

async function getServicesHealthMatrix() {
  try {
    const services = await getAllServicesHealth()
    const matrix = []

    Object.entries(services).forEach(([serviceId, health]) => {
      matrix.push({
        service: serviceId,
        status: health.status,
        responseTime: health.responseTime || 0,
        lastCheck: health.lastCheck,
        uptime: health.uptime || 0,
        error: health.error
      })
    })

    return matrix
  } catch (error) {
    logger.error('Error getting services health matrix:', error)
    return []
  }
}

async function getServiceHealthTrends(serviceId, hours = 24) {
  try {
    const trends = []
    const now = new Date()

    for (let i = hours; i >= 0; i--) {
      const timestamp = new Date(now.getTime() - i * 3600000)

      trends.push({
        timestamp: timestamp.toISOString(),
        status: Math.random() > 0.1 ? 'healthy' : 'unhealthy',
        responseTime: Math.floor(Math.random() * 200) + 50,
        availability: Math.random() * 5 + 95
      })
    }

    return trends
  } catch (error) {
    logger.error('Error getting service health trends:', error)
    return []
  }
}

module.exports = {
  getAllServicesHealth,
  getServiceHealth,
  triggerHealthCheck
}