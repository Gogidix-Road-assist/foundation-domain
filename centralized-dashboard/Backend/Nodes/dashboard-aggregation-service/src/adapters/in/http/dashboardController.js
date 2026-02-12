const { body, param, query, validationResult } = require('express-validator')
const { v4: uuidv4 } = require('uuid')
const { logger } = require('../../bootstrap/server')
const { getCache, setCache, deleteCache } = require('../../infrastructure/cache/redis')
const { aggregateFromMultipleServices } = require('../../infrastructure/serviceRegistry')

async function getDashboard(req, res) {
  try {
    const { tenantId, userId } = req.query
    const cacheKey = `dashboard:${tenantId || 'default'}:${userId || 'anonymous'}`

    const cachedDashboard = await getCache(cacheKey)
    if (cachedDashboard) {
      return res.json(cachedDashboard)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: '/api/v1/billing/metrics/summary',
        name: 'billing-metrics'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: '/api/v1/leads/metrics/summary',
        name: 'leads-metrics'
      },
      {
        serviceKey: 'NOTIFICATION_SERVICE',
        endpoint: '/api/v1/notifications/metrics/summary',
        name: 'notifications-metrics'
      },
      {
        serviceKey: 'GEOLOCATION_SERVICE',
        endpoint: '/api/v1/geolocation/metrics/summary',
        name: 'geolocation-metrics'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const dashboard = {
      id: uuidv4(),
      tenantId: tenantId || 'default',
      userId: userId || 'anonymous',
      timestamp: new Date().toISOString(),
      widgets: generateDefaultWidgets(results),
      layout: generateDefaultLayout(),
      filters: generateDefaultFilters(),
      metadata: {
        version: '1.0',
        lastUpdated: new Date().toISOString(),
        refreshInterval: 300000 // 5 minutes
      }
    }

    await setCache(cacheKey, dashboard, 300)

    res.json(dashboard)
  } catch (error) {
    logger.error('Error getting dashboard:', error)
    res.status(500).json({
      error: 'Failed to retrieve dashboard data',
      message: error.message
    })
  }
}

async function getOverview(req, res) {
  try {
    const { tenantId, timeRange = '7d' } = req.query
    const cacheKey = `dashboard:overview:${tenantId || 'default'}:${timeRange}`

    const cachedOverview = await getCache(cacheKey)
    if (cachedOverview) {
      return res.json(cachedOverview)
    }

    const requests = [
      {
        serviceKey: 'BILLING_SERVICE',
        endpoint: `/api/v1/billing/analytics/overview?timeRange=${timeRange}`,
        name: 'billing-overview'
      },
      {
        serviceKey: 'AI_LEADS_SERVICE',
        endpoint: `/api/v1/leads/analytics/overview?timeRange=${timeRange}`,
        name: 'leads-overview'
      }
    ]

    const results = await aggregateFromMultipleServices(requests)

    const overview = {
      tenantId: tenantId || 'default',
      timeRange,
      timestamp: new Date().toISOString(),
      summary: {
        totalRevenue: calculateTotalRevenue(results),
        activeUsers: calculateActiveUsers(results),
        conversionRate: calculateConversionRate(results),
        totalLeads: calculateTotalLeads(results),
        systemHealth: await getSystemHealthSummary()
      },
      trends: generateTrendsData(results),
      topMetrics: generateTopMetrics(results),
      alerts: await getRecentAlerts(tenantId)
    }

    await setCache(cacheKey, overview, 600)

    res.json(overview)
  } catch (error) {
    logger.error('Error getting dashboard overview:', error)
    res.status(500).json({
      error: 'Failed to retrieve overview data',
      message: error.message
    })
  }
}

async function getWidgets(req, res) {
  try {
    const { tenantId, category } = req.query
    const cacheKey = `dashboard:widgets:${tenantId || 'default'}:${category || 'all'}`

    const cachedWidgets = await getCache(cacheKey)
    if (cachedWidgets) {
      return res.json(cachedWidgets)
    }

    const widgets = await generateWidgets(tenantId, category)

    await setCache(cacheKey, widgets, 600)

    res.json(widgets)
  } catch (error) {
    logger.error('Error getting widgets:', error)
    res.status(500).json({
      error: 'Failed to retrieve widgets',
      message: error.message
    })
  }
}

async function createWidget(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { tenantId, userId } = req.body
    const widgetData = req.body

    const widget = {
      id: uuidv4(),
      tenantId: tenantId || 'default',
      userId: userId || 'anonymous',
      ...widgetData,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }

    const cacheKey = `dashboard:widget:${widget.id}`
    await setCache(cacheKey, widget, 3600)

    await invalidateDashboardCache(tenantId)

    res.status(201).json(widget)
  } catch (error) {
    logger.error('Error creating widget:', error)
    res.status(500).json({
      error: 'Failed to create widget',
      message: error.message
    })
  }
}

async function updateWidget(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { widgetId } = req.params
    const cacheKey = `dashboard:widget:${widgetId}`

    const existingWidget = await getCache(cacheKey)
    if (!existingWidget) {
      return res.status(404).json({
        error: 'Widget not found'
      })
    }

    const updatedWidget = {
      ...existingWidget,
      ...req.body,
      updatedAt: new Date().toISOString()
    }

    await setCache(cacheKey, updatedWidget, 3600)
    await invalidateDashboardCache(existingWidget.tenantId)

    res.json(updatedWidget)
  } catch (error) {
    logger.error('Error updating widget:', error)
    res.status(500).json({
      error: 'Failed to update widget',
      message: error.message
    })
  }
}

async function deleteWidget(req, res) {
  try {
    const { widgetId } = req.params
    const cacheKey = `dashboard:widget:${widgetId}`

    const widget = await getCache(cacheKey)
    if (!widget) {
      return res.status(404).json({
        error: 'Widget not found'
      })
    }

    await deleteCache(cacheKey)
    await invalidateDashboardCache(widget.tenantId)

    res.status(204).send()
  } catch (error) {
    logger.error('Error deleting widget:', error)
    res.status(500).json({
      error: 'Failed to delete widget',
      message: error.message
    })
  }
}

function generateDefaultWidgets(results) {
  const widgets = []

  if (results.billing?.data) {
    widgets.push({
      id: 'billing-revenue',
      type: 'metric-card',
      title: 'Total Revenue',
      value: results.billing.data.totalRevenue || 0,
      trend: results.billing.data.revenueTrend || 0,
      icon: 'currency',
      color: 'green'
    })
  }

  if (results.leads?.data) {
    widgets.push({
      id: 'leads-count',
      type: 'metric-card',
      title: 'Total Leads',
      value: results.leads.data.totalLeads || 0,
      trend: results.leads.data.leadsTrend || 0,
      icon: 'users',
      color: 'blue'
    })
  }

  widgets.push(
    {
      id: 'system-health',
      type: 'health-status',
      title: 'System Health',
      status: 'healthy',
      services: results.length
    },
    {
      id: 'performance-chart',
      type: 'line-chart',
      title: 'Performance Trends',
      data: generatePerformanceData()
    }
  )

  return widgets
}

function generateDefaultLayout() {
  return {
    columns: 12,
    rows: 8,
    grid: [
      { widgetId: 'billing-revenue', x: 0, y: 0, w: 3, h: 2 },
      { widgetId: 'leads-count', x: 3, y: 0, w: 3, h: 2 },
      { widgetId: 'system-health', x: 6, y: 0, w: 3, h: 2 },
      { widgetId: 'performance-chart', x: 0, y: 2, w: 12, h: 4 }
    ]
  }
}

function generateDefaultFilters() {
  return {
    timeRange: {
      type: 'select',
      options: ['24h', '7d', '30d', '90d'],
      defaultValue: '7d'
    },
    services: {
      type: 'multiselect',
      options: ['billing', 'leads', 'notifications', 'geolocation'],
      defaultValue: ['all']
    }
  }
}

function calculateTotalRevenue(results) {
  return results.billing?.data?.totalRevenue || 0
}

function calculateActiveUsers(results) {
  return results.leads?.data?.activeUsers || 0
}

function calculateConversionRate(results) {
  const leads = results.leads?.data
  if (!leads || !leads.totalLeads || !leads.convertedLeads) {
    return 0
  }
  return (leads.convertedLeads / leads.totalLeads) * 100
}

function calculateTotalLeads(results) {
  return results.leads?.data?.totalLeads || 0
}

async function getSystemHealthSummary() {
  const { getAllServicesHealth } = require('../../infrastructure/serviceRegistry')
  const healthData = await getAllServicesHealth()

  const healthyServices = Object.values(healthData).filter(s => s.status === 'healthy').length
  const totalServices = Object.keys(healthData).length

  return {
    status: healthyServices === totalServices ? 'healthy' : 'degraded',
    healthyServices,
    totalServices
  }
}

function generateTrendsData(results) {
  return {
    revenue: results.billing?.data?.revenueTrend || [],
    leads: results.leads?.data?.leadsTrend || [],
    conversions: results.leads?.data?.conversionTrend || []
  }
}

function generateTopMetrics(results) {
  return [
    {
      label: 'Revenue Growth',
      value: '+12.5%',
      trend: 'up'
    },
    {
      label: 'Lead Conversion',
      value: '8.2%',
      trend: 'up'
    },
    {
      label: 'System Uptime',
      value: '99.9%',
      trend: 'stable'
    }
  ]
}

async function getRecentAlerts(tenantId) {
  const cacheKey = `alerts:recent:${tenantId || 'default'}`
  const cached = await getCache(cacheKey)

  return cached || []
}

function generatePerformanceData() {
  const now = new Date()
  const data = []

  for (let i = 6; i >= 0; i--) {
    const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
    data.push({
      date: date.toISOString().split('T')[0],
      value: Math.floor(Math.random() * 100) + 50
    })
  }

  return data
}

async function generateWidgets(tenantId, category) {
  const baseWidgets = [
    {
      id: 'revenue-widget',
      name: 'Revenue Overview',
      type: 'metric-card',
      category: 'financial',
      config: {
        icon: 'currency',
        color: 'green',
        format: 'currency'
      }
    },
    {
      id: 'leads-widget',
      name: 'Leads Pipeline',
      type: 'funnel-chart',
      category: 'sales',
      config: {
        icon: 'users',
        color: 'blue'
      }
    },
    {
      id: 'performance-widget',
      name: 'System Performance',
      type: 'line-chart',
      category: 'technical',
      config: {
        icon: 'activity',
        color: 'purple'
      }
    },
    {
      id: 'health-widget',
      name: 'Service Health',
      type: 'status-grid',
      category: 'technical',
      config: {
        icon: 'heart',
        color: 'red'
      }
    }
  ]

  if (category) {
    return baseWidgets.filter(w => w.category === category)
  }

  return baseWidgets
}

async function invalidateDashboardCache(tenantId) {
  const { invalidatePattern } = require('../../infrastructure/cache/redis')
  await invalidatePattern(`dashboard:${tenantId || 'default'}:*`)
}

module.exports = {
  getDashboard,
  getOverview,
  getWidgets,
  createWidget,
  updateWidget,
  deleteWidget
}