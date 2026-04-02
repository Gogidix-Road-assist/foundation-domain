require('dotenv').config()
const express = require('express')
const cors = require('cors')
const helmet = require('helmet')
const compression = require('compression')
const rateLimit = require('express-rate-limit')
const pino = require('pino')
const pinoHttp = require('pino-http')
const WebSocket = require('ws')
const http = require('http')
const swaggerUi = require('swagger-ui-express')
const { logger } = require('./logger')

const { registerRoutes } = require('../adapters/in/http/routes')
const { connectDatabase } = require('../infrastructure/database/mongodb')
const { connectRedis } = require('../infrastructure/cache/redis')
const { initializeScheduler } = require('../infrastructure/scheduler')
const { initializeServiceRegistry } = require('../infrastructure/serviceRegistry')
const swaggerConfig = require('../config/swagger')

// Remove logger definition as it's now imported from logger.js

function configureMiddleware(app) {
  app.use(helmet({
    crossOriginResourcePolicy: { policy: "cross-origin" }
  }))

  app.use(cors({
    origin: process.env.CORS_ORIGIN || '*',
    credentials: true
  }))

  app.use(compression())

  app.use(express.json({ limit: '10mb' }))
  app.use(express.urlencoded({ extended: true, limit: '10mb' }))

  if (process.env.NODE_ENV === 'production') {
    const limiter = rateLimit({
      windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 15 * 60 * 1000,
      max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100,
      message: {
        error: 'Too many requests from this IP, please try again later.'
      }
    })
    app.use('/api', limiter)
  }

  app.use(pinoHttp({
    logger
  }))
}

function createApp() {
  const app = express()

  configureMiddleware(app)

  // Serve API documentation
  app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerConfig.definition, {
    explorer: true,
    customCss: '.swagger-ui .topbar { display: none }',
    customSiteTitle: 'Dashboard Aggregation Service API Documentation',
    swaggerOptions: {
      persistAuthorization: true,
      displayRequestDuration: true,
      docExpansion: 'none',
      filter: true,
      showRequestHeaders: true
    }
  }))

  // API spec endpoint
  app.get('/api-docs.json', (req, res) => {
    res.setHeader('Content-Type', 'application/json')
    res.send(swaggerConfig.definition)
  })

  registerRoutes(app)

  app.use((err, req, res, next) => {
    logger.error(err)
    res.status(err.status || 500).json({
      error: process.env.NODE_ENV === 'production' ? 'Internal Server Error' : err.message
    })
  })

  return app
}

async function initializeWebSocket(server) {
  if (process.env.WEBSOCKET_ENABLED === 'true') {
    const wss = new WebSocket.Server({ server })

    wss.on('connection', (ws, req) => {
      logger.info('WebSocket client connected')

      ws.on('message', (message) => {
        try {
          const data = JSON.parse(message)
          logger.info('WebSocket message received', data)

          ws.send(JSON.stringify({
            type: 'response',
            data: { received: true, timestamp: new Date().toISOString() }
          }))
        } catch (error) {
          logger.error('Error processing WebSocket message', error)
        }
      })

      ws.on('close', () => {
        logger.info('WebSocket client disconnected')
      })
    })

    global.wss = wss
    logger.info('WebSocket server initialized')
  }
}

async function main() {
  try {
    logger.info('Starting Dashboard Aggregation Service...')

    await connectDatabase()
    logger.info('Database connected successfully')

    await connectRedis()
    logger.info('Redis connected successfully')

    await initializeServiceRegistry()
    logger.info('Service registry initialized')

    await initializeScheduler()
    logger.info('Scheduler initialized')

    const app = createApp()
    const server = http.createServer(app)

    await initializeWebSocket(server)

    const port = process.env.PORT ? Number(process.env.PORT) : 3000
    const host = process.env.HOST || 'localhost'

    server.listen(port, host, () => {
      logger.info(`Dashboard Aggregation Service listening on http://${host}:${port}`)
      logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`)
      logger.info(`WebSocket enabled: ${process.env.WEBSOCKET_ENABLED === 'true'}`)
    })

    const gracefulShutdown = (signal) => {
      logger.info(`Received ${signal}, shutting down gracefully...`)
      server.close(() => {
        logger.info('HTTP server closed')
        process.exit(0)
      })
    }

    process.on('SIGTERM', () => gracefulShutdown('SIGTERM'))
    process.on('SIGINT', () => gracefulShutdown('SIGINT'))

  } catch (error) {
    logger.error('Failed to start server:', error)
    process.exit(1)
  }
}

if (require.main === module) {
  main()
}

module.exports = { createApp, logger }
