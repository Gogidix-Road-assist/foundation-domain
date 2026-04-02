const { body, param, query, validationResult } = require('express-validator')
const { v4: uuidv4 } = require('uuid')
const { logger } = require('../../bootstrap/server')
const { getCache, setCache } = require('../../infrastructure/cache/redis')
const { aggregateFromMultipleServices } = require('../../infrastructure/serviceRegistry')

async function getReports(req, res) {
  try {
    const { tenantId, type, status, page = 1, limit = 20 } = req.query
    const cacheKey = `reports:${tenantId || 'default'}:${type || 'all'}:${status || 'all'}:${page}:${limit}`

    const cachedReports = await getCache(cacheKey)
    if (cachedReports) {
      return res.json(cachedReports)
    }

    const reports = await getReportsFromStorage(tenantId, type, status, page, limit)

    const response = {
      reports: reports.data,
      pagination: {
        page: parseInt(page),
        limit: parseInt(limit),
        total: reports.total,
        pages: Math.ceil(reports.total / limit)
      },
      summary: {
        total: reports.total,
        scheduled: reports.scheduled,
        completed: reports.completed,
        failed: reports.failed
      }
    }

    await setCache(cacheKey, response, 300)

    res.json(response)
  } catch (error) {
    logger.error('Error getting reports:', error)
    res.status(500).json({
      error: 'Failed to retrieve reports',
      message: error.message
    })
  }
}

async function getReport(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { reportId } = req.params
    const cacheKey = `report:${reportId}`

    const cachedReport = await getCache(cacheKey)
    if (cachedReport) {
      return res.json(cachedReport)
    }

    const report = await getReportFromStorage(reportId)

    if (!report) {
      return res.status(404).json({
        error: 'Report not found',
        reportId
      })
    }

    await setCache(cacheKey, report, 600)

    res.json(report)
  } catch (error) {
    logger.error('Error getting report:', error)
    res.status(500).json({
      error: 'Failed to retrieve report',
      message: error.message
    })
  }
}

async function createReport(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const reportData = req.body
    const report = {
      id: uuidv4(),
      ...reportData,
      status: 'pending',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      generatedAt: null,
      generatedBy: null,
      expiresAt: calculateExpirationDate(reportData.retention)
    }

    await saveReportToStorage(report)

    const cacheKey = `report:${report.id}`
    await setCache(cacheKey, report, 3600)

    res.status(201).json(report)
  } catch (error) {
    logger.error('Error creating report:', error)
    res.status(500).json({
      error: 'Failed to create report',
      message: error.message
    })
  }
}

async function getReportData(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { reportId } = req.params
    const { format = 'json', filters } = req.query

    const report = await getReportFromStorage(reportId)
    if (!report) {
      return res.status(404).json({
        error: 'Report not found',
        reportId
      })
    }

    if (report.status !== 'completed') {
      return res.status(400).json({
        error: 'Report not ready',
        status: report.status
      })
    }

    const reportData = await generateReportData(report, format, filters)

    res.json({
      reportId,
      format,
      data: reportData,
      generatedAt: new Date().toISOString()
    })
  } catch (error) {
    logger.error('Error getting report data:', error)
    res.status(500).json({
      error: 'Failed to retrieve report data',
      message: error.message
    })
  }
}

async function exportReport(req, res) {
  try {
    const errors = validationResult(req)
    if (!errors.isEmpty()) {
      return res.status(400).json({
        error: 'Validation failed',
        details: errors.array()
      })
    }

    const { reportId } = req.params
    const { format = 'csv', email } = req.body

    const report = await getReportFromStorage(reportId)
    if (!report) {
      return res.status(404).json({
        error: 'Report not found',
        reportId
      })
    }

    if (report.status !== 'completed') {
      return res.status(400).json({
        error: 'Report not ready for export',
        status: report.status
      })
    }

    const exportData = await generateReportExport(report, format)

    if (email) {
      await sendReportByEmail(report, exportData, email)
    }

    res.json({
      reportId,
      format,
      exportedAt: new Date().toISOString(),
      emailSent: email || false,
      downloadUrl: `/api/v1/reports/${reportId}/download?format=${format}`
    })
  } catch (error) {
    logger.error('Error exporting report:', error)
    res.status(500).json({
      error: 'Failed to export report',
      message: error.message
    })
  }
}

async function getReportsFromStorage(tenantId, type, status, page, limit) {
  const cacheKey = `reports:storage:${tenantId || 'default'}:${type || 'all'}:${status || 'all'}`
  const cached = await getCache(cacheKey)

  if (cached) {
    return paginateReports(cached, page, limit)
  }

  const mockReports = generateMockReports(tenantId, type, status)
  await setCache(cacheKey, mockReports, 600)

  return paginateReports(mockReports, page, limit)
}

function generateMockReports(tenantId, type, status) {
  const reports = []
  const count = Math.floor(Math.random() * 30) + 5

  for (let i = 0; i < count; i++) {
    const types = ['analytics', 'financial', 'usage', 'performance', 'health', 'custom']
    const statuses = ['pending', 'generating', 'completed', 'failed']
    const reportType = types[Math.floor(Math.random() * types.length)]
    const reportStatus = statuses[Math.floor(Math.random() * statuses.length)]

    if (type && type !== reportType) continue
    if (status && status !== reportStatus) continue

    reports.push({
      id: `report_${i + 1}`,
      tenantId: tenantId || 'default',
      name: `${reportType.charAt(0).toUpperCase() + reportType.slice(1)} Report ${i + 1}`,
      description: `Generated ${reportType} report`,
      type: reportType,
      status: reportStatus,
      format: ['pdf', 'csv', 'excel'][Math.floor(Math.random() * 3)],
      schedule: generateSchedule(),
      parameters: generateReportParameters(reportType),
      createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      updatedAt: new Date().toISOString(),
      generatedAt: reportStatus === 'completed' ? new Date().toISOString() : null,
      size: reportStatus === 'completed' ? Math.floor(Math.random() * 10000000) + 100000 : null,
      retention: 90
    })
  }

  return reports
}

function generateSchedule() {
  const schedules = [
    null,
    { frequency: 'daily', time: '09:00' },
    { frequency: 'weekly', day: 'monday', time: '09:00' },
    { frequency: 'monthly', day: 1, time: '09:00' }
  ]

  return schedules[Math.floor(Math.random() * schedules.length)]
}

function generateReportParameters(type) {
  const parameters = {
    analytics: {
      timeRange: '30d',
      metrics: ['revenue', 'users', 'conversions'],
      dimensions: ['region', 'segment']
    },
    financial: {
      timeRange: '30d',
      includeInvoices: true,
      includePayments: true
    },
    usage: {
      timeRange: '7d',
      breakdown: 'feature',
      includeInactive: false
    },
    performance: {
      timeRange: '24h',
      granularity: '5m',
      services: ['all']
    },
    health: {
      timeRange: '7d',
      includeHistory: true
    },
    custom: {}
  }

  return parameters[type] || parameters.custom
}

function paginateReports(reports, page, limit) {
  const start = (page - 1) * limit
  const end = start + limit
  const paginatedReports = reports.slice(start, end)

  return {
    data: paginatedReports,
    total: reports.length,
    scheduled: reports.filter(r => r.schedule).length,
    completed: reports.filter(r => r.status === 'completed').length,
    failed: reports.filter(r => r.status === 'failed').length
  }
}

async function getReportFromStorage(reportId) {
  const cacheKey = `report:${reportId}`
  const cached = await getCache(cacheKey)

  if (cached) {
    return cached
  }

  const mockReport = {
    id: reportId,
    tenantId: 'default',
    name: 'Sample Report',
    type: 'analytics',
    status: 'completed',
    createdAt: new Date().toISOString(),
    generatedAt: new Date().toISOString()
  }

  await setCache(cacheKey, mockReport, 300)
  return mockReport
}

async function saveReportToStorage(report) {
  const cacheKey = `report:${report.id}`
  await setCache(cacheKey, report, 3600)
}

async function generateReportData(report, format, filters) {
  const requests = [
    {
      serviceKey: 'BILLING_SERVICE',
      endpoint: `/api/v1/analytics/report?format=${format}`,
      name: 'billing-data'
    },
    {
      serviceKey: 'AI_LEADS_SERVICE',
      endpoint: `/api/v1/analytics/report?format=${format}`,
      name: 'leads-data'
    }
  ]

  const results = await aggregateFromMultipleServices(requests)

  return {
    reportId: report.id,
    format,
    generatedAt: new Date().toISOString(),
    data: results,
    summary: {
      totalRecords: results.reduce((sum, r) => sum + (r.success ? (r.data?.records || 0) : 0), 0),
      successCount: results.filter(r => r.success).length,
      errorCount: results.filter(r => !r.success).length
    }
  }
}

async function generateReportExport(report, format) {
  const exportData = {
    reportId: report.id,
    format,
    content: generateMockReportContent(format),
    size: Math.floor(Math.random() * 1000000) + 100000,
    checksum: generateChecksum()
  }

  return exportData
}

function generateMockReportContent(format) {
  const content = {
    csv: 'id,name,value\n1,Sample,100\n2,Data,200',
    json: { data: [{ id: 1, name: 'Sample', value: 100 }] },
    pdf: 'PDF content base64 encoded...',
    excel: 'Excel content base64 encoded...'
  }

  return content[format] || content.json
}

function generateChecksum() {
  return Math.random().toString(36).substring(2)
}

async function sendReportByEmail(report, exportData, email) {
  logger.info(`Sending report ${report.id} to ${email}`)
  return true
}

function calculateExpirationDate(retention) {
  if (!retention) return null

  const expirationDate = new Date()
  expirationDate.setDate(expirationDate.getDate() + retention)

  return expirationDate.toISOString()
}

module.exports = {
  getReports,
  getReport,
  createReport,
  getReportData,
  exportReport
}