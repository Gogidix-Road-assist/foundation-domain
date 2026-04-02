const { statusController } = require('./statusController');

function registerRoutes(app) {
  app.get('/status', statusController);
}

module.exports = { registerRoutes };
