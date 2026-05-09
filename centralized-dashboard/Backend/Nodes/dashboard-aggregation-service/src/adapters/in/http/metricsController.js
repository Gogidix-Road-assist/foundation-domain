const { query, param, validationResult } = require('express-validator')
const { logger } = require('../../bootstrap/server')
const { getCache, setCache } = require('../../infrastructure/cache/redis')
const { aggregateFromMultipleServices, callService, getAllServicesHealth } = require('../../infrastructure/serviceRegistry')

async function getMetricsSummary(req, res) {
  try {
    const { tenantId, timeRange = '24h', services } = req.query
    const cacheKey = `metrics:summary:${tenantId || 'default'}:${timeRange}:${services || 'all'}`

    const cachedMetrics = await getCache(cacheKey)
    if (cachedMetrics) {
      return res.json(cachedMetrics)
    }

    const serviceList = services ? services.split(',') : ['BILLING_SERVICE', 'AI_LEADS_SERVICE', 'NOTIFICATION_SERVICE']

    const requests = serviceList.map(serviceKey => ({
      serviceKey,
      endpoint: `/api/v1/metrics/summary?timeRange=${timeRange}`,
      name: serviceKey.toLowerCase().replace('_service', '')
    }))

    const results = await aggregateFromMultipleServices(requests)

    const summary = {
      tenantId: tenantId || 'default',
      timeRange,
      timestamp: new Date().toISOString(),
      services: {},
      summary: {
        totalRequests: results.length,
        successfulRequests: results.filter(r => r.success).length,
        failedRequests: results.filter(r => !r.success).length,
        overallHealth: calculateOverallHealth(results)
      },
      aggregatedMetrics: calculateAggregatedMetrics(results)
    }

    results.forEach(result => {
      summary.services[result.name] = {
        status: result.success ? 'healthy' : 'unhealthy',
        metrics: result.success ? result.data : null,
        error: result.success ? null : result.error,
        lastUpdated: new Date().toISOString()
      }
    })

    await setCache(cacheKey, summary, 300)

    res.json(summary)
  } catch (error) {
    logger.error('Error getting metrics summary:', error)
    res.status(500).json({
      error: 'Failed to retrieve metrics summary',
      message: error.message
    })
  }
}

async function getServiceMetrics(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { serviceId } = req.params
    const { tenantId, timeRange = '24h', metrics } = req.query
    const cacheKey = `metrics:service:${serviceId}:${tenantId || 'default'}:${timeRange}`

    const cachedMetrics = await getCache(cacheKey)
    if (cachedMetrics) {
      return res.json(cachedMetrics)
    }

    const serviceKey = serviceId.toUpperCase() + '_SERVICE'
    const endpoint = `/api/v1/metrics/detailed${metrics ? `?metrics=${metrics}` : `?timeRange=${timeRange}`}`

    const result = await callService(serviceKey, endpoint)

    const serviceMetrics = {
      serviceId,
      tenantId: tenantId || 'default',
      timeRange,
      timestamp: new Date().toISOString(),
      status: result.success ? 'healthy' : 'unhealthy',
      metrics: result.success ? result.data : null,
      error: result.success ? null : result.error,
      responseTime: result.responseTime || null,
      httpStatus: result.status || null
    }

    await setCache(cacheKey, serviceMetrics, 300)

    res.json(serviceMetrics)
  } catch (error) {
    logger.error('Error getting service metrics:', error)
    res.status(500).json({
      error: 'Failed to retrieve service metrics',
      message: error.message
    })
  }
}

async function getPerformanceMetrics(req, res) {
  try {
    const { tenantId, timeRange = '1h', granularity = '5m' } = req.query
    const cacheKey = `metrics:performance:${tenantId || 'default'}:${timeRange}:${granularity}`

    const cachedMetrics = await getCache(cacheKey)
    if (cachedMetrics) {
      return res.json(cachedMetrics)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/metrics/performance?timeRange=${timeRange}&granularity=${granularity}`,
        name: 'billing'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/metrics/performance?timeRange=${timeRange}&granularity=${granularity}`,
        name: 'leads'
      },
      {
        serviceKey: 'NOTIFICATION_SERVICE',
        endpoint: `/api/v1/metrics/performance?timeRange=${timeRange}&granularity=${granularity}`,
        name: 'notifications'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const performanceMetrics = {
      tenantId: tenantId || 'default',
      timeRange,
      granularity,
      timestamp: new Date().toISOString(),
      metrics: {
        responseTime: calculateAverageResponseTime(results),
        throughput: calculateThroughput(results),
        errorRate: calculateErrorRate(results),
        cpuUtilization: calculateCPUUtilization(results),
        memoryUsage: calculateMemoryUsage(results),
        availability: calculateAvailability(results)
      },
      breakdown: generatePerformanceBreakdown(results)
    }

    await setCache(cacheKey, performanceMetrics, 300)

    res.json(performanceMetrics)
  } catch (error) {
    logger.error('Error getting performance metrics:', error)
    res.status(500).json({
      error: 'Failed to retrieve performance metrics',
      message: error.message
    })
  }
}

async function getTrends(req, res) {
  try {
    const { tenantId, timeRange = '7d', metric } = req.query
    const cacheKey = `metrics:trends:${tenantId || 'default'}:${timeRange}:${metric || 'all'}`

    const cachedTrends = await getCache(cacheKey)
    if (cachedTrends) {
      return res.json(cachedTrends)
    }

    const trends = await generateTrendsData(tenantId, timeRange, metric)

    await setCache(cacheKey, trends, 600)

    res.json(trends)
  } catch (error) {
    logger.error('Error getting trends:', error)
    res.status(500).json({
      error: 'Failed to retrieve trends',
      message: error.message
    })
  }
}

async function getRealTimeMetrics(req, res) {
  try {
    const { tenantId } = req.query

    const serviceHealth = await getAllServicesHealth()
    const systemMetrics = await getSystemMetrics()

    const realTimeMetrics = {
      tenantId: tenantId || 'default',
      timestamp: new Date().toISOString(),
      services: serviceHealth,
      system: systemMetrics,
      alerts: await getActiveAlerts(tenantId)
    }

    res.json(realTimeMetrics)
  } catch (error) {
    logger.error('Error getting real-time metrics:', error)
    res.status(500).json({
      error: 'Failed to retrieve real-time metrics',
      message: error.message
    })
  }
}

function calculateOverallHealth(results) {
  const healthyServices = results.filter(r => r.success).length
  const totalServices = results.length

  if (healthyServices === totalServices) return 'healthy'
  if (healthyServices > totalServices / 2) return 'degraded'
  return 'unhealthy'
}

function calculateAggregatedMetrics(results) {
  const aggregated = {
    totalRevenue: 0,
    totalLeads: 0,
    totalNotifications: 0,
    totalRequests: 0,
    successfulOperations: 0
  }

  results.forEach(result => {
    if (result.success && result.data) {
      aggregated.totalRevenue += result.data.revenue || 0
      aggregated.totalLeads += result.data.leads || 0
      aggregated.totalNotifications += result.data.notifications || 0
      aggregated.totalRequests += result.data.requests || 0
      aggregated.successfulOperations += 1
    }
  })

  return aggregated
}

function calculateAverageResponseTime(results) {
  const responseTimes = results
    .filter(r => r.success && r.data && r.data.averageResponseTime)
    .map(r => r.data.averageResponseTime)

  if (responseTimes.length === 0) return 0

  const sum = responseTimes.reduce((a, b) => a + b, 0)
  return sum / responseTimes.length
}

function calculateThroughput(results) {
  const throughputs = results
    .filter(r => r.success && r.data && r.data.throughput)
    .map(r => r.data.throughput)

  if (throughputs.length === 0) return 0

  return throughputs.reduce((a, b) => a + b, 0)
}

function calculateErrorRate(results) {
  const errorRates = results
    .filter(r => r.success && r.data && r.data.errorRate !== undefined)
    .map(r => r.data.errorRate)

  if (errorRates.length === 0) return 0

  const sum = errorRates.reduce((a, b) => a + b, 0)
  return sum / errorRates.length
}

function calculateCPUUtilization(results) {
  const cpuUsages = results
    .filter(r => r.success && r.data && r.data.cpuUtilization)
    .map(r => r.data.cpuUtilization)

  if (cpuUsages.length === 0) return 0

  const sum = cpuUsages.reduce((a, b) => a + b, 0)
  return sum / cpuUsages.length
}

function calculateMemoryUsage(results) {
  const memoryUsages = results
    .filter(r => r.success && r.data && r.data.memoryUsage)
    .map(r => r.data.memoryUsage)

  if (memoryUsages.length === 0) return 0

  const sum = memoryUsages.reduce((a, b) => a + b, 0)
  return sum / memoryUsages.length
}

function calculateAvailability(results) {
  const availabilities = results
    .filter(r => r.success && r.data && r.data.availability)
    .map(r => r.data.availability)

  if (availabilities.length === 0) return 100

  const sum = availabilities.reduce((a, b) => a + b, 0)
  return sum / availabilities.length
}

function generatePerformanceBreakdown(results) {
  const breakdown = {}

  results.forEach(result => {
    if (result.success && result.data) {
      breakdown[result.name] = {
        responseTime: result.data.averageResponseTime || 0,
        throughput: result.data.throughput || 0,
        errorRate: result.data.errorRate || 0,
        status: 'healthy'
      }
    } else {
      breakdown[result.name] = {
        responseTime: 0,
        throughput: 0,
        errorRate: 100,
        status: 'unhealthy'
      }
    }
  })

  return breakdown
}

async function generateTrendsData(tenantId, timeRange, metric) {
  const requests = [
    {
      serviceKey: 'BILLING_SERVICE',
      endpoint: `/api/v1/metrics/trends?timeRange=${timeRange}${metric ? `&metric=${metric}` : ''}`,
      name: 'billing'
    },
    {
      serviceKey: 'AI_LEADS_SERVICE',
      endpoint: `/api/v1/metrics/trends?timeRange=${timeRange}${metric ? `&metric=${metric}` : ''}`,
      name: 'leads'
    }
  ]

  const results = await aggregateFromMultipleServices(requests)

  return {
    tenantId: tenantId || 'default',
    timeRange,
    metric: metric || 'all',
    timestamp: new Date().toISOString(),
    data: {
      billing: results.find(r => r.name === 'billing')?.data || [],
      leads: results.find(r => r.name === 'leads')?.data || [],
      aggregated: aggregateTrends(results)
    }
  }
}

function aggregateTrends(results) {
  const aggregated = []
  const timePoints = new Set()

  results.forEach(result => {
    if (result.success && result.data) {
      result.data.forEach(point => {
        timePoints.add(point.timestamp)
      })
    }
  })

  Array.from(timePoints).sort().forEach(timestamp => {
    const point = { timestamp }

    results.forEach(result => {
      if (result.success && result.data) {
        const dataPoint = result.data.find(d => d.timestamp === timestamp)
        point[result.name] = dataPoint ? dataPoint.value : 0
      } else {
        point[result.name] = 0
      }
    })

    point.total = Object.values(point)
      .filter(v => typeof v === 'number')
      .reduce((a, b) => a + b, 0)

    aggregated.push(point)
  })

  return aggregated
}

async function getSystemMetrics() {
  return {
    uptime: process.uptime(),
    memory: process.memoryUsage(),
    cpu: process.cpuUsage(),
    nodeVersion: process.version,
    platform: process.platform
  }
}

async function getActiveAlerts(tenantId) {
  const cacheKey = `alerts:active:${tenantId || 'default'}`
  const cached = await getCache(cacheKey)

  return cached || []
}

module.exports = {
  getMetricsSummary,
  getServiceMetrics,
  getPerformanceMetrics,
  getTrends,
  getRealTimeMetrics
}