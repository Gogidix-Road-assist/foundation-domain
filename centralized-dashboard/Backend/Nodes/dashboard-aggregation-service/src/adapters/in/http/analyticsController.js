const { query, body, validationResult } = require('express-validator')
const { logger } = require('../../bootstrap/server')
const { getCache, setCache } = require('../../infrastructure/cache/redis')
const { aggregateFromMultipleServices } = require('../../infrastructure/serviceRegistry')

async function getOverview(req, res) {
  try {
    const { tenantId, timeRange = '7d', segment = 'all' } = req.query
    const cacheKey = `analytics:overview:${tenantId || 'default'}:${timeRange}:${segment}`

    const cachedOverview = await getCache(cacheKey)
    if (cachedOverview) {
      return res.json(cachedOverview)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/analytics/overview?timeRange=${timeRange}&segment=${segment}`,
        name: 'billing'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/analytics/overview?timeRange=${timeRange}&segment=${segment}`,
        name: 'leads'
      },
      {
        serviceKey: 'NOTIFICATION_SERVICE',
        endpoint: `/api/v1/analytics/overview?timeRange=${timeRange}&segment=${segment}`,
        name: 'notifications'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const overview = {
      tenantId: tenantId || 'default',
      timeRange,
      segment,
      timestamp: new Date().toISOString(),
      kpis: calculateKPIs(results),
      trends: generateTrends(results),
      distribution: generateDistribution(results),
      insights: generateInsights(results)
    }

    await setCache(cacheKey, overview, 900)

    res.json(overview)
  } catch (error) {
    logger.error('Error getting analytics overview:', error)
    res.status(500).json({
      error: 'Failed to retrieve analytics overview',
      message: error.message
    })
  }
}

async function getConversionAnalytics(req, res) {
  try {
    const { tenantId, timeRange = '30d', funnel = 'default' } = req.query
    const cacheKey = `analytics:conversion:${tenantId || 'default'}:${timeRange}:${funnel}`

    const cachedAnalytics = await getCache(cacheKey)
    if (cachedAnalytics) {
      return res.json(cachedAnalytics)
    }

    const requests = [
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/analytics/conversion?timeRange=${timeRange}&funnel=${funnel}`,
        name: 'leads-funnel'
      },
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/analytics/conversion?timeRange=${timeRange}`,
        name: 'billing-conversion'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const conversionAnalytics = {
      tenantId: tenantId || 'default',
      timeRange,
      funnel,
      timestamp: new Date().toISOString(),
      funnelAnalysis: analyzeConversionFunnel(results),
      conversionRates: calculateConversionRates(results),
      dropOffAnalysis: analyzeDropOffs(results),
      recommendations: generateConversionRecommendations(results)
    }

    await setCache(cacheKey, conversionAnalytics, 900)

    res.json(conversionAnalytics)
  } catch (error) {
    logger.error('Error getting conversion analytics:', error)
    res.status(500).json({
      error: 'Failed to retrieve conversion analytics',
      message: error.message
    })
  }
}

async function getUsageAnalytics(req, res) {
  try {
    const { tenantId, timeRange = '7d', metric = 'all' } = req.query
    const cacheKey = `analytics:usage:${tenantId || 'default'}:${timeRange}:${metric}`

    const cachedAnalytics = await getCache(cacheKey)
    if (cachedAnalytics) {
      return res.json(cachedAnalytics)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/analytics/usage?timeRange=${timeRange}&metric=${metric}`,
        name: 'billing-usage'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/analytics/usage?timeRange=${timeRange}&metric=${metric}`,
        name: 'leads-usage'
      },
      {
        serviceKey: 'NOTIFICATION_SERVICE',
        endpoint: `/api/v1/analytics/usage?timeRange=${timeRange}&metric=${metric}`,
        name: 'notifications-usage'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const usageAnalytics = {
      tenantId: tenantId || 'default',
      timeRange,
      metric,
      timestamp: new Date().toISOString(),
      usagePatterns: analyzeUsagePatterns(results),
      peakUsageTimes: identifyPeakUsage(results),
      userSegments: segmentUsersByUsage(results),
      featureAdoption: calculateFeatureAdoption(results)
    }

    await setCache(cacheKey, usageAnalytics, 900)

    res.json(usageAnalytics)
  } catch (error) {
    logger.error('Error getting usage analytics:', error)
    res.status(500).json({
      error: 'Failed to retrieve usage analytics',
      message: error.message
    })
  }
}

async function getPerformanceAnalytics(req, res) {
  try {
    const { tenantId, timeRange = '24h', dimension = 'service' } = req.query
    const cacheKey = `analytics:performance:${tenantId || 'default'}:${timeRange}:${dimension}`

    const cachedAnalytics = await getCache(cacheKey)
    if (cachedAnalytics) {
      return res.json(cachedAnalytics)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/analytics/performance?timeRange=${timeRange}`,
        name: 'billing'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/analytics/performance?timeRange=${timeRange}`,
        name: 'leads'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const performanceAnalytics = {
      tenantId: tenantId || 'default',
      timeRange,
      dimension,
      timestamp: new Date().toISOString(),
      performanceMetrics: calculatePerformanceMetrics(results),
      bottlenecks: identifyBottlenecks(results),
      benchmarks: generateBenchmarks(results),
      optimizationSuggestions: generateOptimizationSuggestions(results)
    }

    await setCache(cacheKey, performanceAnalytics, 900)

    res.json(performanceAnalytics)
  } catch (error) {
    logger.error('Error getting performance analytics:', error)
    res.status(500).json({
      error: 'Failed to retrieve performance analytics',
      message: error.message
    })
  }
}

async function getCustomAnalytics(req, res) {
  try {
    const { tenantId, analyticsId } = req.query
    const cacheKey = `analytics:custom:${tenantId || 'default'}:${analyticsId}`

    const cachedAnalytics = await getCache(cacheKey)
    if (cachedAnalytics) {
      return res.json(cachedAnalytics)
    }

    const customAnalytics = await getCustomAnalyticsData(analyticsId, tenantId)

    await setCache(cacheKey, customAnalytics, 600)

    res.json(customAnalytics)
  } catch (error) {
    logger.error('Error getting custom analytics:', error)
    res.status(500).json({
      error: 'Failed to retrieve custom analytics',
      message: error.message
    })
  }
}

async function createCustomAnalytics(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { tenantId, name, description, config, schedule } = req.body

    const customAnalytics = {
      id: generateAnalyticsId(),
      tenantId: tenantId || 'default',
      name,
      description,
      config,
      schedule,
      status: 'active',
      createdBy: req.user?.id || 'system',
      createdAt: new Date().toISOString(),
      lastRun: null,
      nextRun: calculateNextRun(schedule)
    }

    await saveCustomAnalytics(customAnalytics)

    res.status(201).json(customAnalytics)
  } catch (error) {
    logger.error('Error creating custom analytics:', error)
    res.status(500).json({
      error: 'Failed to create custom analytics',
      message: error.message
    })
  }
}

function calculateKPIs(results) {
  const kpis = {
    revenue: 0,
    leads: 0,
    conversions: 0,
    engagement: 0,
    retention: 0
  }

  results.forEach(result => {
    if (result.success && result.data) {
      switch (result.name) {
        case 'billing':
          kpis.revenue = result.data.revenue || 0
          kpis.retention = result.data.retentionRate || 0
          break
        case 'leads':
          kpis.leads = result.data.totalLeads || 0
          kpis.conversions = result.data.conversions || 0
          break
        case 'notifications':
          kpis.engagement = result.data.engagementRate || 0
          break
      }
    }
  })

  return kpis
}

function generateTrends(results) {
  const trends = {}

  results.forEach(result => {
    if (result.success && result.data && result.data.trends) {
      trends[result.name] = result.data.trends
    }
  })

  return trends
}

function generateDistribution(results) {
  const distribution = {
    byService: {},
    byTime: {},
    bySegment: {}
  }

  results.forEach(result => {
    if (result.success && result.data) {
      distribution.byService[result.name] = result.data.distribution || {}
    }
  })

  return distribution
}

function generateInsights(results) {
  const insights = []

  if (results.billing?.success && results.billing.data?.revenueGrowth > 0) {
    insights.push({
      type: 'positive',
      title: 'Revenue Growth',
      description: `Revenue increased by ${results.billing.data.revenueGrowth}%`,
      impact: 'high'
    })
  }

  if (results.leads?.success && results.leads.data?.conversionRate < 5) {
    insights.push({
      type: 'warning',
      title: 'Low Conversion Rate',
      description: 'Lead conversion rate is below 5%',
      impact: 'medium'
    })
  }

  return insights
}

function analyzeConversionFunnel(results) {
  const funnel = {
    stages: [],
    conversionRates: []
  }

  const leadsData = results.find(r => r.name === 'leads-funnel')
  if (leadsData?.success && leadsData.data?.funnel) {
    funnel.stages = leadsData.data.funnel.stages
    funnel.conversionRates = leadsData.data.funnel.conversionRates
  }

  return funnel
}

function calculateConversionRates(results) {
  const rates = {}

  results.forEach(result => {
    if (result.success && result.data && result.data.conversionRate) {
      rates[result.name] = result.data.conversionRate
    }
  })

  return rates
}

function analyzeDropOffs(results) {
  const dropOffs = []

  results.forEach(result => {
    if (result.success && result.data && result.data.dropOffs) {
      dropOffs.push({
        service: result.name,
        stages: result.data.dropOffs
      })
    }
  })

  return dropOffs
}

function generateConversionRecommendations(results) {
  const recommendations = []

  if (results.leads?.success && results.leads.data?.conversionRate < 10) {
    recommendations.push({
      category: 'optimization',
      title: 'Improve Lead Nurturing',
      description: 'Implement automated follow-up sequences to improve conversion rates'
    })
  }

  return recommendations
}

function analyzeUsagePatterns(results) {
  const patterns = {}

  results.forEach(result => {
    if (result.success && result.data && result.data.patterns) {
      patterns[result.name] = result.data.patterns
    }
  })

  return patterns
}

function identifyPeakUsage(results) {
  const peakTimes = []

  results.forEach(result => {
    if (result.success && result.data && result.data.peakTimes) {
      peakTimes.push({
        service: result.name,
        times: result.data.peakTimes
      })
    }
  })

  return peakTimes
}

function segmentUsersByUsage(results) {
  const segments = {
    high: 0,
    medium: 0,
    low: 0
  }

  results.forEach(result => {
    if (result.success && result.data && result.data.userSegments) {
      Object.keys(segments).forEach(segment => {
        segments[segment] += result.data.userSegments[segment] || 0
      })
    }
  })

  return segments
}

function calculateFeatureAdoption(results) {
  const adoption = {}

  results.forEach(result => {
    if (result.success && result.data && result.data.featureAdoption) {
      adoption[result.name] = result.data.featureAdoption
    }
  })

  return adoption
}

function calculatePerformanceMetrics(results) {
  const metrics = {
    averageResponseTime: 0,
    throughput: 0,
    availability: 100,
    errorRate: 0
  }

  let responseTimes = []
  let throughputs = []
  let availabilities = []
  let errorRates = []

  results.forEach(result => {
    if (result.success && result.data) {
      if (result.data.averageResponseTime) responseTimes.push(result.data.averageResponseTime)
      if (result.data.throughput) throughputs.push(result.data.throughput)
      if (result.data.availability) availabilities.push(result.data.availability)
      if (result.data.errorRate !== undefined) errorRates.push(result.data.errorRate)
    }
  })

  if (responseTimes.length > 0) {
    metrics.averageResponseTime = responseTimes.reduce((a, b) => a + b, 0) / responseTimes.length
  }
  if (throughputs.length > 0) {
    metrics.throughput = throughputs.reduce((a, b) => a + b, 0)
  }
  if (availabilities.length > 0) {
    metrics.availability = availabilities.reduce((a, b) => a + b, 0) / availabilities.length
  }
  if (errorRates.length > 0) {
    metrics.errorRate = errorRates.reduce((a, b) => a + b, 0) / errorRates.length
  }

  return metrics
}

function identifyBottlenecks(results) {
  const bottlenecks = []

  results.forEach(result => {
    if (result.success && result.data && result.data.bottlenecks) {
      bottlenecks.push({
        service: result.name,
        issues: result.data.bottlenecks
      })
    }
  })

  return bottlenecks
}

function generateBenchmarks(results) {
  const benchmarks = {
    industry: {},
    internal: {}
  }

  results.forEach(result => {
    if (result.success && result.data && result.data.benchmarks) {
      benchmarks.internal[result.name] = result.data.benchmarks.internal
      benchmarks.industry[result.name] = result.data.benchmarks.industry
    }
  })

  return benchmarks
}

function generateOptimizationSuggestions(results) {
  const suggestions = []

  results.forEach(result => {
    if (result.success && result.data && result.data.suggestions) {
      suggestions.push({
        service: result.name,
        suggestions: result.data.suggestions
      })
    }
  })

  return suggestions
}

async function getCustomAnalyticsData(analyticsId, tenantId) {
  const cacheKey = `custom-analytics:${analyticsId}:${tenantId || 'default'}`
  return await getCache(cacheKey) || {
    id: analyticsId,
    data: {},
    error: 'Analytics not found'
  }
}

async function saveCustomAnalytics(analytics) {
  const cacheKey = `custom-analytics:${analytics.id}:${analytics.tenantId}`
  await setCache(cacheKey, analytics, 86400)
}

function generateAnalyticsId() {
  return `analytics_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

function calculateNextRun(schedule) {
  if (!schedule || !schedule.cron) return null

  return new Date(Date.now() + 3600000).toISOString()
}

module.exports = {
  getOverview,
  getConversionAnalytics,
  getUsageAnalytics,
  getPerformanceAnalytics,
  getCustomAnalytics,
  createCustomAnalytics
}