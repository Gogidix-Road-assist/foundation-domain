const mongoose = require('mongoose')
const { logger } = require('../../bootstrap/server')

let connection = null

async function connectDatabase() {
  try {
    if (connection) {
      logger.info('Database already connected')
      return connection
    }

    const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017/dashboard_aggregation'
    const options = {
      maxPoolSize: 10,
      serverSelectionTimeoutMS: 5000,
      socketTimeoutMS: 45000,
      bufferCommands: false,
      bufferMaxEntries: 0
    }

    connection = await mongoose.connect(mongoUri, options)

    mongoose.connection.on('error', (error) => {
      logger.error('MongoDB connection error:', error)
    })

    mongoose.connection.on('disconnected', () => {
      logger.warn('MongoDB disconnected')
    })

    mongoose.connection.on('reconnected', () => {
      logger.info('MongoDB reconnected')
    })

    logger.info('Connected to MongoDB')
    return connection
  } catch (error) {
    logger.error('Failed to connect to MongoDB:', error)
    throw error
  }
}

async function disconnectDatabase() {
  try {
    if (connection) {
      await mongoose.disconnect()
      connection = null
      logger.info('Disconnected from MongoDB')
    }
  } catch (error) {
    logger.error('Error disconnecting from MongoDB:', error)
    throw error
  }
}

function getConnection() {
  return connection
}

function getConnectionState() {
  const states = {
    0: 'disconnected',
    1: 'connected',
    2: 'connecting',
    3: 'disconnecting'
  }

  return {
    state: states[mongoose.connection.readyState],
    host: mongoose.connection.host,
    port: mongoose.connection.port,
    name: mongoose.connection.name
  }
}

module.exports = {
  connectDatabase,
  disconnectDatabase,
  getConnection,
  getConnectionState
}