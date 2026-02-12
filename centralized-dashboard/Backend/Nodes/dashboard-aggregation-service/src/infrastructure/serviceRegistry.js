const axios = require('axios')
const { logger } = require('../bootstrap/server')
const { getCache, setCache } = require('./cache/redis')

const services = {
  API_GATEWAY: {
    name: 'API Gateway',
    url: process.env.API_GATEWAY_URL || 'http://localhost:8080',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  },
  BILLING_SERVICE: {
    name: 'Billing Service',
    url: process.env.BILLING_SERVICE_URL || 'http://localhost:8081',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  },
  NOTIFICATION_SERVICE: {
    name: 'Notification Service',
    url: process.env.NOTIFICATION_SERVICE_URL || 'http://localhost:8082',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  },
  GEOLOCATION_SERVICE: {
    name: 'Geolocation Service',
    url: process.env.GEOLOCATION_SERVICE_URL || 'http://localhost:8083',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  },
  AI_LEADS_SERVICE: {
    name: 'AI Leads Service',
    url: process.env.AI_LEADS_SERVICE_URL || 'http://localhost:8084',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  },
  CONFIG_SERVICE: {
    name: 'Configuration Service',
    url: process.env.CONFIG_SERVICE_URL || 'http://localhost:8085',
    healthEndpoint: '/actuator/health',
    timeout: 5000
  }
}

const serviceHealth = new Map()

async function initializeServiceRegistry() {
  logger.info('Initializing service registry...')

  for (const [serviceKey, service] of Object.entries(services)) {
    serviceHealth.set(serviceKey, {
      ...service,
      status: 'unknown',
      lastCheck: null,
      responseTime: null,
      error: null
    })

    checkServiceHealth(serviceKey)
  }

  const healthCheckInterval = parseInt(process.env.SERVICE_HEALTH_CHECK_INTERVAL) || 30000
  setInterval(() => {
    Object.keys(services).forEach(checkServiceHealth)
  }, healthCheckInterval)

  logger.info('Service registry initialized with health checks')
}

async function checkServiceHealth(serviceKey) {
  const service = services[serviceKey]
  const healthKey = `service:health:${serviceKey}`

  try {
    const startTime = Date.now()
    const response = await axios.get(`${service.url}${service.healthEndpoint}`, {
      timeout: service.timeout,
      validateStatus: (status) => status >= 200 && status < 300
    })
    const responseTime = Date.now() - startTime

    const healthInfo = {
      ...serviceHealth.get(serviceKey),
      status: 'healthy',
      lastCheck: new Date().toISOString(),
      responseTime,
      error: null
    }

    serviceHealth.set(serviceKey, healthInfo)
    await setCache(healthKey, healthInfo, 60)

    logger.debug(`Service ${service.name} is healthy (${responseTime}ms)`)
  } catch (error) {
    const healthInfo = {
      ...serviceHealth.get(serviceKey),
      status: 'unhealthy',
      lastCheck: new Date().toISOString(),
      responseTime: null,
      error: error.message
    }

    serviceHealth.set(serviceKey, healthInfo)
    await setCache(healthKey, healthInfo, 60)

    logger.warn(`Service ${service.name} is unhealthy: ${error.message}`)
  }
}

function getServiceUrl(serviceKey) {
  const service = services[serviceKey]
  if (!service) {
    throw new Error(`Service ${serviceKey} not found`)
  }

  const health = serviceHealth.get(serviceKey)
  if (health && health.status === 'unhealthy') {
    logger.warn(`Service ${service.name} is currently unhealthy`)
  }

  return service.url
}

async function getServiceHealth(serviceKey) {
  const healthKey = `service:health:${serviceKey}`

  let health = serviceHealth.get(serviceKey)
  if (!health) {
    health = await getCache(healthKey)
  }

  return health || { status: 'unknown' }
}

async function getAllServicesHealth() {
  const healthData = {}

  for (const [serviceKey] of Object.entries(services)) {
    healthData[serviceKey] = await getServiceHealth(serviceKey)
  }

  return healthData
}

function getHealthyServices() {
  const healthyServices = {}

  for (const [serviceKey, health] of serviceHealth.entries()) {
    if (health.status === 'healthy') {
      healthyServices[serviceKey] = {
        ...services[serviceKey],
        ...health
      }
    }
  }

  return healthyServices
}

function getUnhealthyServices() {
  const unhealthyServices = {}

  for (const [serviceKey, health] of serviceHealth.entries()) {
    if (health.status === 'unhealthy' || health.status === 'unknown') {
      unhealthyServices[serviceKey] = {
        ...services[serviceKey],
        ...health
      }
    }
  }

  return unhealthyServices
}

async function callService(serviceKey, endpoint, options = {}) {
  const serviceUrl = getServiceUrl(serviceKey)
  const fullUrl = `${serviceUrl}${endpoint}`

  try {
    const response = await axios({
      url: fullUrl,
      method: options.method || 'GET',
      data: options.data,
      params: options.params,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      timeout: options.timeout || services[serviceKey].timeout || 10000,
      ...options.axiosOptions
    })

    return {
      success: true,
      data: response.data,
      status: response.status,
      headers: response.headers
    }
  } catch (error) {
    logger.error(`Error calling service ${serviceKey} at ${endpoint}:`, error.message)

    if (error.response) {
      return {
        success: false,
        error: error.response.data,
        status: error.response.status
      }
    }

    return {
      success: false,
      error: error.message,
      status: 503
    }
  }
}

async function aggregateFromMultipleServices(requests) {
  const promises = requests.map(async (request) => {
    const { serviceKey, endpoint, options, name } = request
    try {
      const result = await callService(serviceKey, endpoint, options)
      return {
        name: name || serviceKey,
        success: result.success,
        data: result.data,
        error: result.error,
        status: result.status
      }
    } catch (error) {
      return {
        name: name || serviceKey,
        success: false,
        error: error.message
      }
    }
  })

  const results = await Promise.allSettled(promises)

  return results.map((result, index) => {
    if (result.status === 'fulfilled') {
      return result.value
    } else {
      return {
        name: requests[index].name || requests[index].serviceKey,
        success: false,
        error: result.reason.message
      }
    }
  })
}

module.exports = {
  initializeServiceRegistry,
  getServiceUrl,
  getServiceHealth,
  getAllServicesHealth,
  getHealthyServices,
  getUnhealthyServices,
  callService,
  aggregateFromMultipleServices,
  checkServiceHealth
}