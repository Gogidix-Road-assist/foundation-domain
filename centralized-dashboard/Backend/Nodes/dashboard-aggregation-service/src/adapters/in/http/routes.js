const { statusController } = require('./statusController');
const { dashboardController } = require('./dashboardController');
const { metricsController } = require('./metricsController');
const { analyticsController } = require('./analyticsController');
const { serviceHealthController } = require('./serviceHealthController');
const { alertsController } = require('./alertsController');
const { reportsController } = require('./reportsController');
const { jwtAuth } = require('./middleware/auth');
const { dashboardLimiter, reportLimiter, exportLimiter, apiLimiter } = require('./middleware/rateLimiting');
const { validateBody, validateQuery } = require('./middleware/validation');
const { schemas } = require('./middleware/validation');

function registerRoutes(app) {
  // Health and Status (Public - No authentication required)
  app.get('/status', statusController);
  app.get('/api/v1/health', (req, res) => {
    res.json({
      status: 'healthy',
      timestamp: new Date().toISOString(),
      service: 'dashboard-aggregation-service',
      version: '1.0.0'
    });
  });

  // Dashboard API Routes (Authenticated)
  app.get('/api/v1/dashboard', jwtAuth.authenticate(), apiLimiter.middleware.bind(apiLimiter), dashboardController.getDashboard);
  app.get('/api/v1/dashboard/overview', jwtAuth.authenticate(), apiLimiter.middleware.bind(apiLimiter), dashboardController.getOverview);
  app.get('/api/v1/dashboard/widgets', jwtAuth.authenticate(), validateQuery(schemas.dashboard.query), dashboardController.getWidgets);
  app.post('/api/v1/dashboard/widgets', jwtAuth.authenticate(), validateBody(schemas.widget.create), dashboardController.createWidget);
  app.put('/api/v1/dashboard/widgets/:widgetId', jwtAuth.authenticate(), validateBody(schemas.widget.update), dashboardController.updateWidget);
  app.delete('/api/v1/dashboard/widgets/:widgetId', jwtAuth.authenticate(), dashboardController.deleteWidget);

  // Metrics API Routes (Authenticated)
  app.get('/api/v1/metrics/summary', jwtAuth.authenticate(), validateQuery(schemas.metrics.query), metricsController.getMetricsSummary);
  app.get('/api/v1/metrics/services', jwtAuth.authenticate(), validateQuery(schemas.metrics.query), metricsController.getServiceMetrics);
  app.get('/api/v1/metrics/performance', jwtAuth.authenticate(), validateQuery(schemas.metrics.query), metricsController.getPerformanceMetrics);
  app.get('/api/v1/metrics/trends', jwtAuth.authenticate(), validateQuery(schemas.metrics.query), metricsController.getTrends);
  app.get('/api/v1/metrics/realtime', jwtAuth.authenticate(), validateQuery(schemas.metrics.query), metricsController.getRealTimeMetrics);

  // Analytics API Routes (Authenticated)
  app.get('/api/v1/analytics/overview', jwtAuth.authenticate(), validateQuery(schemas.analytics.query), analyticsController.getOverview);
  app.get('/api/v1/analytics/conversion', jwtAuth.authenticate(), validateQuery(schemas.analytics.query), analyticsController.getConversionAnalytics);
  app.get('/api/v1/analytics/usage', jwtAuth.authenticate(), validateQuery(schemas.analytics.query), analyticsController.getUsageAnalytics);
  app.get('/api/v1/analytics/performance', jwtAuth.authenticate(), validateQuery(schemas.analytics.query), analyticsController.getPerformanceAnalytics);
  app.get('/api/v1/analytics/custom', jwtAuth.authenticate(), validateQuery(schemas.analytics.query), analyticsController.getCustomAnalytics);
  app.post('/api/v1/analytics/custom', jwtAuth.authenticate(), validateBody(schemas.analytics.custom), analyticsController.createCustomAnalytics);

  // Service Health API Routes (Authenticated)
  app.get('/api/v1/services/health', jwtAuth.authenticate(), serviceHealthController.getAllServicesHealth);
  app.get('/api/v1/services/health/:serviceId', jwtAuth.authenticate(), serviceHealthController.getServiceHealth);
  app.post('/api/v1/services/health/check', jwtAuth.authenticate(), serviceHealthController.triggerHealthCheck);

  // Alerts API Routes (Authenticated)
  app.get('/api/v1/alerts', jwtAuth.authenticate(), validateQuery(schemas.alerts.query), alertsController.getAlerts);
  app.get('/api/v1/alerts/:alertId', jwtAuth.authenticate(), alertsController.getAlert);
  app.post('/api/v1/alerts', jwtAuth.authenticate(), validateBody(schemas.alerts.create), alertsController.createAlert);
  app.put('/api/v1/alerts/:alertId/acknowledge', jwtAuth.authenticate(), validateBody(schemas.alerts.acknowledge), alertsController.acknowledgeAlert);
  app.put('/api/v1/alerts/:alertId/resolve', jwtAuth.authenticate(), validateBody(schemas.alerts.resolve), alertsController.resolveAlert);

  // Reports API Routes (Authenticated with stricter rate limiting)
  app.get('/api/v1/reports', jwtAuth.authenticate(), validateQuery(schemas.reports.query), reportsController.getReports);
  app.get('/api/v1/reports/:reportId', jwtAuth.authenticate(), reportsController.getReport);
  app.post('/api/v1/reports', jwtAuth.authenticate(), reportLimiter.middleware.bind(reportLimiter), validateBody(schemas.reports.create), reportsController.createReport);
  app.get('/api/v1/reports/:reportId/data', jwtAuth.authenticate(), validateQuery(schemas.reports.data), reportsController.getReportData);
  app.post('/api/v1/reports/:reportId/export', jwtAuth.authenticate(), exportLimiter.middleware.bind(exportLimiter), validateBody(schemas.reports.export), reportsController.exportReport);

  // Error handling for 404
  app.use('*', (req, res) => {
    res.status(404).json({
      error: 'Endpoint not found',
      path: req.originalUrl,
      method: req.method
    });
  });
}

module.exports = { registerRoutes };
