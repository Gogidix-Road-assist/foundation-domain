const Redis = require('ioredis')
const { logger } = require('../../bootstrap/server')

let redisClient = null
let pubClient = null
let subClient = null

async function connectRedis() {
  try {
    if (redisClient) {
      logger.info('Redis already connected')
      return redisClient
    }

    const redisConfig = {
      host: process.env.REDIS_HOST || 'localhost',
      port: parseInt(process.env.REDIS_PORT) || 6379,
      password: process.env.REDIS_PASSWORD || undefined,
      db: parseInt(process.env.REDIS_DB) || 0,
      retryDelayOnFailover: 100,
      maxRetriesPerRequest: 3,
      lazyConnect: true,
      keepAlive: 30000,
      connectTimeout: 10000,
      commandTimeout: 5000
    }

    redisClient = new Redis(redisConfig)

    redisClient.on('connect', () => {
      logger.info('Redis connected')
    })

    redisClient.on('ready', () => {
      logger.info('Redis ready')
    })

    redisClient.on('error', (error) => {
      logger.error('Redis connection error:', error)
    })

    redisClient.on('close', () => {
      logger.warn('Redis connection closed')
    })

    redisClient.on('reconnecting', (ms) => {
      logger.info(`Redis reconnecting in ${ms}ms`)
    })

    await redisClient.connect()

    pubClient = new Redis(redisConfig)
    subClient = new Redis(redisConfig)

    logger.info('Redis clients initialized')
    return redisClient
  } catch (error) {
    logger.error('Failed to connect to Redis:', error)
    throw error
  }
}

async function disconnectRedis() {
  try {
    if (redisClient) {
      await redisClient.quit()
      redisClient = null
    }

    if (pubClient) {
      await pubClient.quit()
      pubClient = null
    }

    if (subClient) {
      await subClient.quit()
      subClient = null
    }

    logger.info('Disconnected from Redis')
  } catch (error) {
    logger.error('Error disconnecting from Redis:', error)
    throw error
  }
}

async function setCache(key, value, ttl = 300) {
  try {
    if (!redisClient) {
      throw new Error('Redis not connected')
    }

    const serializedValue = JSON.stringify(value)
    if (ttl > 0) {
      await redisClient.setex(key, ttl, serializedValue)
    } else {
      await redisClient.set(key, serializedValue)
    }

    logger.debug(`Cache set for key: ${key}, TTL: ${ttl}s`)
  } catch (error) {
    logger.error('Error setting cache:', error)
    throw error
  }
}

async function getCache(key) {
  try {
    if (!redisClient) {
      throw new Error('Redis not connected')
    }

    const value = await redisClient.get(key)
    if (value === null) {
      return null
    }

    const parsedValue = JSON.parse(value)
    logger.debug(`Cache hit for key: ${key}`)
    return parsedValue
  } catch (error) {
    logger.error('Error getting cache:', error)
    return null
  }
}

async function deleteCache(key) {
  try {
    if (!redisClient) {
      throw new Error('Redis not connected')
    }

    const result = await redisClient.del(key)
    logger.debug(`Cache deleted for key: ${key}, result: ${result}`)
    return result
  } catch (error) {
    logger.error('Error deleting cache:', error)
    throw error
  }
}

async function invalidatePattern(pattern) {
  try {
    if (!redisClient) {
      throw new Error('Redis not connected')
    }

    const keys = await redisClient.keys(pattern)
    if (keys.length > 0) {
      const result = await redisClient.del(...keys)
      logger.debug(`Invalidated ${result} keys matching pattern: ${pattern}`)
      return result
    }
    return 0
  } catch (error) {
    logger.error('Error invalidating cache pattern:', error)
    throw error
  }
}

async function publish(channel, message) {
  try {
    if (!pubClient) {
      throw new Error('Redis pub client not connected')
    }

    const result = await pubClient.publish(channel, JSON.stringify(message))
    logger.debug(`Published message to channel ${channel}, subscribers: ${result}`)
    return result
  } catch (error) {
    logger.error('Error publishing message:', error)
    throw error
  }
}

async function subscribe(channel, callback) {
  try {
    if (!subClient) {
      throw new Error('Redis sub client not connected')
    }

    await subClient.subscribe(channel)
    subClient.on('message', (receivedChannel, message) => {
      if (receivedChannel === channel) {
        try {
          const parsedMessage = JSON.parse(message)
          callback(parsedMessage)
        } catch (error) {
          logger.error('Error parsing subscribed message:', error)
        }
      }
    })

    logger.info(`Subscribed to channel: ${channel}`)
  } catch (error) {
    logger.error('Error subscribing to channel:', error)
    throw error
  }
}

function getRedisClient() {
  return redisClient
}

function getConnectionInfo() {
  if (!redisClient) {
    return { status: 'disconnected' }
  }

  return {
    status: redisClient.status,
    host: redisClient.options.host,
    port: redisClient.options.port,
    db: redisClient.options.db
  }
}

module.exports = {
  connectRedis,
  disconnectRedis,
  setCache,
  getCache,
  deleteCache,
  invalidatePattern,
  publish,
  subscribe,
  getRedisClient,
  getConnectionInfo
}