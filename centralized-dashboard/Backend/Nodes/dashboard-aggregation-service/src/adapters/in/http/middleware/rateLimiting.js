const { logger } = require('../../bootstrap/server')
const { getCache, setCache, incrementCache } = require('../../infrastructure/cache/redis')

class RateLimiter {
  constructor(options = {}) {
    this.windowMs = options.windowMs || 60000 // 1 minute default
    this.maxRequests = options.maxRequests || 100
    this.keyGenerator = options.keyGenerator || this.defaultKeyGenerator
    this.skipSuccessfulRequests = options.skipSuccessfulRequests || false
    this.skipFailedRequests = options.skipFailedRequests || false
  }

  defaultKeyGenerator(req) {
    // Priority: API key > User ID > IP address
    if (req.apiKey) {
      return `ratelimit:api:${req.apiKey}`
    }
    if (req.user?.id) {
      return `ratelimit:user:${req.user.id}`
    }
    return `ratelimit:ip:${req.ip}`
  }

  async middleware(req, res, next) {
    const key = this.keyGenerator(req)

    try {
      const current = await incrementCache(key, 1, this.windowMs / 1000)
      const requests = current || 1
      const remaining = Math.max(0, this.maxRequests - requests)
      const resetTime = Date.now() + this.windowMs

      // Set rate limit headers
      res.set({
        'X-RateLimit-Limit': this.maxRequests,
        'X-RateLimit-Remaining': remaining,
        'X-RateLimit-Reset': new Date(resetTime).toISOString()
      })

      if (requests > this.maxRequests) {
        logger.warn('Rate limit exceeded', {
          key,
          requests,
          limit: this.maxRequests,
          url: req.originalUrl,
          method: req.method,
          ip: req.ip
        })

        return res.status(429).json({
          error: 'Too many requests',
          message: `Rate limit exceeded. Try again in ${Math.ceil(this.windowMs / 1000)} seconds.`,
          retryAfter: Math.ceil(this.windowMs / 1000),
          limit: this.maxRequests,
          windowMs: this.windowMs
        })
      }

      // Skip counting based on configuration
      res.on('finish', async () => {
        if (
          (this.skipSuccessfulRequests && res.statusCode >= 200 && res.statusCode < 300) ||
          (this.skipFailedRequests && res.statusCode >= 400)
        ) {
          // Decrement the counter
          await incrementCache(key, -1, this.windowMs / 1000)
        }
      })

      next()
    } catch (error) {
      logger.error('Rate limiting error', error)
      // Fail open - allow request if rate limiting fails
      next()
    }
  }
}

// Predefined rate limiters for different use cases
const apiLimiter = new RateLimiter({
  windowMs: 60000, // 1 minute
  maxRequests: 100,
  message: 'Too many API requests, please try again later'
})

const dashboardLimiter = new RateLimiter({
  windowMs: 60000, // 1 minute
  maxRequests: 30,
  message: 'Too many dashboard requests, please try again later'
})

const reportLimiter = new RateLimiter({
  windowMs: 300000, // 5 minutes
  maxRequests: 10,
  message: 'Too many report generation requests, please try again later'
})

const exportLimiter = new RateLimiter({
  windowMs: 600000, // 10 minutes
  maxRequests: 5,
  message: 'Too many export requests, please try again later'
})

const webhookLimiter = new RateLimiter({
  windowMs: 60000, // 1 minute
  maxRequests: 20,
  keyGenerator: (req) => `ratelimit:webhook:${req.body.webhookId || req.ip}`,
  message: 'Too many webhook requests, please try again later'
})

// Advanced rate limiting with sliding window
class SlidingWindowRateLimiter {
  constructor(options = {}) {
    this.windowMs = options.windowMs || 60000
    this.maxRequests = options.maxRequests || 100
    this.granularity = options.granularity || 1000 // 1 second buckets
  }

  async middleware(req, res, next) {
    const key = `sliding:${req.ip}:${Math.floor(Date.now() / this.granularity)}`

    try {
      // Get current window data
      const currentBucket = Math.floor(Date.now() / this.granularity)
      const buckets = []

      // Check last N buckets based on window size
      const bucketCount = Math.ceil(this.windowMs / this.granularity)
      for (let i = 0; i < bucketCount; i++) {
        const bucketKey = `sliding:${req.ip}:${currentBucket - i}`
        const count = await getCache(bucketKey)
        buckets.push(count || 0)
      }

      const totalRequests = buckets.reduce((sum, count) => sum + count, 0)

      if (totalRequests >= this.maxRequests) {
        return res.status(429).json({
          error: 'Rate limit exceeded',
          message: 'Too many requests in the sliding window',
          retryAfter: Math.ceil(this.granularity / 1000)
        })
      }

      // Increment current bucket
      await setCache(key, 1, this.windowMs / 1000)

      res.set({
        'X-RateLimit-Limit': this.maxRequests,
        'X-RateLimit-Remaining': Math.max(0, this.maxRequests - totalRequests - 1)
      })

      next()
    } catch (error) {
      logger.error('Sliding window rate limiting error', error)
      next()
    }
  }
}

// Rate limiting for specific endpoints based on cost
class CostBasedRateLimiter {
  constructor(options = {}) {
    this.maxCost = options.maxCost || 1000 // Cost units per window
    this.windowMs = options.windowMs || 60000
    this.costs = options.costs || {
      'GET': 1,
      'POST': 5,
      'PUT': 5,
      'DELETE': 10,
      'REPORT': 20,
      'EXPORT': 50
    }
  }

  async middleware(req, res, next) {
    const key = `cost:${req.ip}`
    const cost = this.costs[req.method] || 1

    try {
      const current = await incrementCache(key, cost, this.windowMs / 1000)
      const remaining = Math.max(0, this.maxCost - (current || 0))

      res.set({
        'X-CostLimit-Limit': this.maxCost,
        'X-CostLimit-Remaining': remaining,
        'X-CostLimit-Used': current || cost,
        'X-CostLimit-Cost': cost
      })

      if ((current || cost) > this.maxCost) {
        return res.status(429).json({
          error: 'Cost limit exceeded',
          message: `Operation cost (${cost}) exceeds remaining limit (${remaining})`,
          retryAfter: Math.ceil(this.windowMs / 1000)
        })
      }

      next()
    } catch (error) {
      logger.error('Cost-based rate limiting error', error)
      next()
    }
  }
}

// Create rate limiters for different tiers
const createTieredLimiter = (tiers) => {
  return async (req, res, next) => {
    const userTier = req.user?.tier || 'free'
    const config = tiers[userTier] || tiers.free

    const limiter = new RateLimiter(config)
    return limiter.middleware(req, res, next)
  }
}

const tieredLimiters = createTieredLimiter({
  free: {
    windowMs: 60000,
    maxRequests: 50
  },
  pro: {
    windowMs: 60000,
    maxRequests: 200
  },
  enterprise: {
    windowMs: 60000,
    maxRequests: 1000
  }
})

// Adaptive rate limiting based on system load
class AdaptiveRateLimiter {
  constructor(options = {}) {
    this.baseLimit = options.baseLimit || 100
    this.windowMs = options.windowMs || 60000
    this.loadThreshold = options.loadThreshold || 0.8
  }

  async getSystemLoad() {
    // Get current system metrics
    const memUsage = process.memoryUsage()
    const cpuUsage = process.cpuUsage()

    // Simple load calculation - in production, use more sophisticated metrics
    const memoryLoad = memUsage.heapUsed / memUsage.heapTotal
    const avgCpu = (cpuUsage.user + cpuUsage.system) / 2 / 1000000

    return Math.max(memoryLoad, avgCpu / 100)
  }

  async middleware(req, res, next) {
    try {
      const systemLoad = await this.getSystemLoad()
      const adaptiveLimit = Math.floor(this.baseLimit * (1 - systemLoad))
      const effectiveLimit = Math.max(10, adaptiveLimit) // Minimum 10 requests

      const limiter = new RateLimiter({
        windowMs: this.windowMs,
        maxRequests: effectiveLimit
      })

      logger.debug('Adaptive rate limiting', {
        systemLoad,
        baseLimit: this.baseLimit,
        effectiveLimit,
        ip: req.ip
      })

      return limiter.middleware(req, res, next)
    } catch (error) {
      logger.error('Adaptive rate limiting error', error)
      next()
    }
  }
}

const adaptiveLimiter = new AdaptiveRateLimiter()

module.exports = {
  RateLimiter,
  SlidingWindowRateLimiter,
  CostBasedRateLimiter,
  AdaptiveRateLimiter,
  apiLimiter,
  dashboardLimiter,
  reportLimiter,
  exportLimiter,
  webhookLimiter,
  tieredLimiters,
  createTieredLimiter
}