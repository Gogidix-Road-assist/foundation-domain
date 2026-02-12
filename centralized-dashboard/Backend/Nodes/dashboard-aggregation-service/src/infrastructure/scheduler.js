const cron = require('node-cron')
const { logger } = require('../bootstrap/server')
const { aggregateFromMultipleServices } = require('./serviceRegistry')
const { setCache } = require('./cache/redis')

const scheduledJobs = new Map()

async function initializeScheduler() {
  logger.info('Initializing scheduler...')

  scheduleMetricsAggregation()
  scheduleSystemHealthCheck()
  scheduleCacheCleanup()
  scheduleAnalyticsRefresh()

  logger.info('Scheduler initialized with scheduled jobs')
}

function scheduleMetricsAggregation() {
  const jobName = 'metrics-aggregation'

  if (scheduledJobs.has(jobName)) {
    logger.warn(`Job ${jobName} already scheduled`)
    return
  }

  const job = cron.schedule('*/5 * * * *', async () => {
    logger.info('Running metrics aggregation job...')
    try {
      await performMetricsAggregation()
    } catch (error) {
      logger.error('Error in metrics aggregation job:', error)
    }
  }, {
    scheduled: true,
    timezone: 'UTC'
  })

  scheduledJobs.set(jobName, job)
  logger.info('Scheduled metrics aggregation job (every 5 minutes)')
}

async function performMetricsAggregation() {
  const requests = [
    {
      serviceKey: 'BILLING_SERVICE',
      endpoint: '/api/v1/billing/metrics/summary',
      name: 'billing'
    },
    {
      serviceKey: 'AI_LEADS_SERVICE',
      endpoint: '/api/v1/leads/metrics/summary',
      name: 'leads'
    },
    {
      serviceKey: 'NOTIFICATION_SERVICE',
      endpoint: '/api/v1/notifications/metrics/summary',
      name: 'notifications'
    },
    {
      serviceKey: 'GEOLOCATION_SERVICE',
      endpoint: '/api/v1/geolocation/metrics/summary',
      name: 'geolocation'
    }
  ]

  const results = await aggregateFromMultipleServices(requests)
  const aggregatedMetrics = processMetricsResults(results)

  await setCache('dashboard:aggregated-metrics', aggregatedMetrics, 300)

  broadcastRealTimeUpdate({
    type: 'metrics-update',
    data: aggregatedMetrics,
    timestamp: new Date().toISOString()
  })

  logger.info('Metrics aggregation completed successfully')
}

function processMetricsResults(results) {
  const aggregated = {
    timestamp: new Date().toISOString(),
    services: {},
    summary: {
      totalRequests: results.length,
      successfulRequests: results.filter(r => r.success).length,
      failedRequests: results.filter(r => !r.success).length
    }
  }

  results.forEach(result => {
    if (result.success && result.data) {
      aggregated.services[result.name] = {
        status: 'healthy',
        metrics: result.data,
        lastUpdated: new Date().toISOString()
      }
    } else {
      aggregated.services[result.name] = {
        status: 'unhealthy',
        error: result.error,
        lastUpdated: new Date().toISOString()
      }
    }
  })

  return aggregated
}

function scheduleSystemHealthCheck() {
  const jobName = 'system-health-check'

  if (scheduledJobs.has(jobName)) {
    logger.warn(`Job ${jobName} already scheduled`)
    return
  }

  const job = cron.schedule('*/2 * * * *', async () => {
    logger.debug('Running system health check...')
    try {
      await performSystemHealthCheck()
    } catch (error) {
      logger.error('Error in system health check job:', error)
    }
  }, {
    scheduled: true,
    timezone: 'UTC'
  })

  scheduledJobs.set(jobName, job)
  logger.info('Scheduled system health check job (every 2 minutes)')
}

async function performSystemHealthCheck() {
  const systemHealth = {
    timestamp: new Date().toISOString(),
    services: await require('./serviceRegistry').getAllServicesHealth(),
    dashboard: {
      status: 'healthy',
      uptime: process.uptime(),
      memory: process.memoryUsage(),
      cpu: process.cpuUsage()
    }
  }

  await setCache('dashboard:system-health', systemHealth, 120)

  const unhealthyCount = Object.values(systemHealth.services)
    .filter(s => s.status !== 'healthy').length

  if (unhealthyCount > 0) {
    broadcastRealTimeUpdate({
      type: 'health-alert',
      data: {
        message: `${unhealthyCount} services are unhealthy`,
        services: systemHealth.services
      },
      timestamp: new Date().toISOString()
    })
  }
}

function scheduleCacheCleanup() {
  const jobName = 'cache-cleanup'

  if (scheduledJobs.has(jobName)) {
    logger.warn(`Job ${jobName} already scheduled`)
    return
  }

  const job = cron.schedule('0 2 * * *', async () => {
    logger.info('Running cache cleanup job...')
    try {
      await performCacheCleanup()
    } catch (error) {
      logger.error('Error in cache cleanup job:', error)
    }
  }, {
    scheduled: true,
    timezone: 'UTC'
  })

  scheduledJobs.set(jobName, job)
  logger.info('Scheduled cache cleanup job (daily at 2 AM UTC)')
}

async function performCacheCleanup() {
  const { getRedisClient, invalidatePattern } = require('./cache/redis')
  const redisClient = getRedisClient()

  if (!redisClient) {
    logger.warn('Redis not available for cache cleanup')
    return
  }

  const patterns = [
    'dashboard:*',
    'service:*',
    'temp:*'
  ]

  let totalDeleted = 0

  for (const pattern of patterns) {
    try {
      const deleted = await invalidatePattern(`${pattern}:*`)
      totalDeleted += deleted
      logger.debug(`Deleted ${deleted} keys matching pattern: ${pattern}`)
    } catch (error) {
      logger.error(`Error cleaning up pattern ${pattern}:`, error)
    }
  }

  logger.info(`Cache cleanup completed. Deleted ${totalDeleted} keys`)
}

function scheduleAnalyticsRefresh() {
  const jobName = 'analytics-refresh'

  if (scheduledJobs.has(jobName)) {
    logger.warn(`Job ${jobName} already scheduled`)
    return
  }

  const job = cron.schedule('0 */6 * * *', async () => {
    logger.info('Running analytics refresh job...')
    try {
      await performAnalyticsRefresh()
    } catch (error) {
      logger.error('Error in analytics refresh job:', error)
    }
  }, {
    scheduled: true,
    timezone: 'UTC'
  })

  scheduledJobs.set(jobName, job)
  logger.info('Scheduled analytics refresh job (every 6 hours)')
}

async function performAnalyticsRefresh() {
  const requests = [
    {
      serviceKey: 'BILLING_SERVICE',
      endpoint: '/api/v1/billing/analytics/daily',
      name: 'billing-analytics'
    },
    {
      serviceKey: 'AI_LEADS_SERVICE',
      endpoint: '/api/v1/leads/analytics/summary',
      name: 'leads-analytics'
    }
  ]

  const results = await aggregateFromMultipleServices(requests)
  const analyticsData = processAnalyticsResults(results)

  await setCache('dashboard:analytics', analyticsData, 21600)

  broadcastRealTimeUpdate({
    type: 'analytics-update',
    data: analyticsData,
    timestamp: new Date().toISOString()
  })

  logger.info('Analytics refresh completed successfully')
}

function processAnalyticsResults(results) {
  const analytics = {
    timestamp: new Date().toISOString(),
    data: {},
    summary: {
      totalServices: results.length,
      successfulRefreshes: results.filter(r => r.success).length
    }
  }

  results.forEach(result => {
    if (result.success && result.data) {
      analytics.data[result.name] = result.data
    }
  })

  return analytics
}

function scheduleCustomJob(name, cronExpression, callback, options = {}) {
  if (scheduledJobs.has(name)) {
    logger.warn(`Job ${name} already scheduled`)
    return false
  }

  const job = cron.schedule(cronExpression, callback, {
    scheduled: options.scheduled !== false,
    timezone: options.timezone || 'UTC'
  })

  scheduledJobs.set(name, job)
  logger.info(`Scheduled custom job: ${name} (${cronExpression})`)

  return true
}

function stopJob(name) {
  const job = scheduledJobs.get(name)
  if (job) {
    job.stop()
    scheduledJobs.delete(name)
    logger.info(`Stopped job: ${name}`)
    return true
  }
  return false
}

function startJob(name) {
  const job = scheduledJobs.get(name)
  if (job) {
    job.start()
    logger.info(`Started job: ${name}`)
    return true
  }
  return false
}

function getScheduledJobs() {
  return Array.from(scheduledJobs.keys())
}

function broadcastRealTimeUpdate(data) {
  const wss = global.wss
  if (wss && wss.clients) {
    const message = JSON.stringify(data)

    wss.clients.forEach((client) => {
      if (client.readyState === 1) { // WebSocket.OPEN = 1
        client.send(message)
      }
    })

    logger.debug(`Broadcasted real-time update to ${wss.clients.size} WebSocket clients`)
  }
}

module.exports = {
  initializeScheduler,
  scheduleMetricsAggregation,
  performMetricsAggregation,
  scheduleSystemHealthCheck,
  performSystemHealthCheck,
  scheduleCacheCleanup,
  performCacheCleanup,
  scheduleAnalyticsRefresh,
  performAnalyticsRefresh,
  scheduleCustomJob,
  stopJob,
  startJob,
  getScheduledJobs,
  broadcastRealTimeUpdate
}