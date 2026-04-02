const jwt = require('jsonwebtoken')
const { AuthenticationError, AuthorizationError } = require('./errorHandler')
const { logger } = require('../../bootstrap/server')

/**
 * JWT Authentication Middleware
 * Validates JWT tokens and sets user context in request
 */
class JWTAuth {
  constructor() {
    this.secret = process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-in-production'
    this.algorithm = 'HS256'
  }

  /**
   * Generate JWT token for a user
   * @param {Object} payload - User data to encode in token
   * @param {String} expiresIn - Token expiration time (default: 24h)
   * @returns {String} JWT token
   */
  generateToken(payload, expiresIn = '24h') {
    return jwt.sign(payload, this.secret, {
      algorithm: this.algorithm,
      expiresIn
    })
  }

  /**
   * Verify and decode JWT token
   * @param {String} token - JWT token to verify
   * @returns {Object} Decoded token payload
   */
  verifyToken(token) {
    try {
      return jwt.verify(token, this.secret, {
        algorithms: [this.algorithm]
      })
    } catch (error) {
      if (error.name === 'JsonWebTokenError') {
        throw new AuthenticationError('Invalid authentication token')
      } else if (error.name === 'TokenExpiredError') {
        throw new AuthenticationError('Authentication token has expired')
      }
      throw new AuthenticationError('Authentication failed')
    }
  }

  /**
   * Extract token from request
   * Priority: Authorization header > Cookie > Query param
   * @param {Object} req - Express request object
   * @returns {String|null} Extracted token or null
   */
  extractToken(req) {
    // Check Authorization header (Bearer token)
    const authHeader = req.get('Authorization')
    if (authHeader && authHeader.startsWith('Bearer ')) {
      return authHeader.substring(7)
    }

    // Check cookie
    if (req.cookies && req.cookies.token) {
      return req.cookies.token
    }

    // Check query parameter (for WebSocket connections)
    if (req.query.token) {
      return req.query.token
    }

    return null
  }

  /**
   * Authentication middleware
   * Validates JWT token and sets req.user
   */
  authenticate() {
    return (req, res, next) => {
      try {
        const token = this.extractToken(req)

        if (!token) {
          throw new AuthenticationError('Authentication token is required')
        }

        const decoded = this.verifyToken(token)

        // Set user context in request
        req.user = {
          id: decoded.sub || decoded.userId,
          email: decoded.email,
          name: decoded.name,
          role: decoded.role,
          permissions: decoded.permissions || [],
          tenants: decoded.tenants || ['default'],
          tier: decoded.tier || 'free',
          iat: decoded.iat,
          exp: decoded.exp
        }

        logger.debug('User authenticated', {
          userId: req.user.id,
          email: req.user.email,
          role: req.user.role,
          ip: req.ip
        })

        next()
      } catch (error) {
        logger.warn('Authentication failed', {
          error: error.message,
          ip: req.ip,
          userAgent: req.get('User-Agent'),
          url: req.originalUrl
        })

        if (error instanceof AuthenticationError) {
          return res.status(error.statusCode).json({
            error: error.name,
            message: error.message,
            type: 'authentication',
            timestamp: new Date().toISOString()
          })
        }

        return res.status(401).json({
          error: 'Authentication failed',
          message: 'Could not authenticate user',
          type: 'authentication',
          timestamp: new Date().toISOString()
        })
      }
    }
  }

  /**
   * Optional authentication middleware
   * Attaches user if token present, but doesn't require it
   */
  optionalAuthenticate() {
    return (req, res, next) => {
      try {
        const token = this.extractToken(req)

        if (token) {
          const decoded = this.verifyToken(token)
          req.user = {
            id: decoded.sub || decoded.userId,
            email: decoded.email,
            name: decoded.name,
            role: decoded.role,
            permissions: decoded.permissions || [],
            tenants: decoded.tenants || ['default'],
            tier: decoded.tier || 'free'
          }
        }

        next()
      } catch (error) {
        // Ignore authentication errors for optional auth
        next()
      }
    }
  }

  /**
   * Authorization middleware - check user permissions
   * @param {String|Array<String>} requiredPermissions - Required permission(s)
   */
  requirePermissions(requiredPermissions) {
    const permissions = Array.isArray(requiredPermissions)
      ? requiredPermissions
      : [requiredPermissions]

    return (req, res, next) => {
      if (!req.user) {
        return res.status(401).json({
          error: 'Authentication required',
          message: 'You must be authenticated to access this resource',
          type: 'authentication',
          timestamp: new Date().toISOString()
        })
      }

      const userPermissions = req.user.permissions || []

      // Check if user has all required permissions
      const hasAllPermissions = permissions.every(perm =>
        userPermissions.includes(perm) || userPermissions.includes('*')
      )

      if (!hasAllPermissions) {
        logger.warn('Authorization failed - insufficient permissions', {
          userId: req.user.id,
          requiredPermissions: permissions,
          userPermissions,
          url: req.originalUrl
        })

        return res.status(403).json({
          error: 'Access denied',
          message: `You do not have the required permissions: ${permissions.join(', ')}`,
          type: 'authorization',
          requiredPermissions: permissions,
          timestamp: new Date().toISOString()
        })
      }

      next()
    }
  }

  /**
   * Authorization middleware - check user roles
   * @param {String|Array<String>} allowedRoles - Allowed role(s)
   */
  requireRoles(allowedRoles) {
    const roles = Array.isArray(allowedRoles) ? allowedRoles : [allowedRoles]

    return (req, res, next) => {
      if (!req.user) {
        return res.status(401).json({
          error: 'Authentication required',
          message: 'You must be authenticated to access this resource',
          type: 'authentication',
          timestamp: new Date().toISOString()
        })
      }

      if (!roles.includes(req.user.role) && !roles.includes('*')) {
        logger.warn('Authorization failed - invalid role', {
          userId: req.user.id,
          userRole: req.user.role,
          allowedRoles: roles,
          url: req.originalUrl
        })

        return res.status(403).json({
          error: 'Access denied',
          message: `Your role (${req.user.role}) is not authorized for this resource`,
          type: 'authorization',
          allowedRoles: roles,
          timestamp: new Date().toISOString()
        })
      }

      next()
    }
  }

  /**
   * Refresh token middleware
   * Generates a new token with extended expiration
   */
  refreshToken() {
    return (req, res, next) => {
      try {
        const token = this.extractToken(req)

        if (!token) {
          throw new AuthenticationError('Token is required for refresh')
        }

        const decoded = this.verifyToken(token)

        // Generate new token with same payload
        const newToken = this.generateToken({
          sub: decoded.sub || decoded.userId,
          email: decoded.email,
          name: decoded.name,
          role: decoded.role,
          permissions: decoded.permissions,
          tenants: decoded.tenants,
          tier: decoded.tier
        }, process.env.JWT_EXPIRES_IN || '24h')

        res.json({
          success: true,
          token: newToken,
          expiresIn: process.env.JWT_EXPIRES_IN || '24h'
        })
      } catch (error) {
        logger.warn('Token refresh failed', {
          error: error.message,
          ip: req.ip
        })

        return res.status(401).json({
          error: 'Token refresh failed',
          message: error.message,
          type: 'authentication',
          timestamp: new Date().toISOString()
        })
      }
    }
  }
}

// Create singleton instance
const jwtAuth = new JWTAuth()

// Helper function to generate tokens for testing
const generateTestToken = (userOverrides = {}) => {
  const defaultUser = {
    sub: 'test-user-123',
    userId: 'test-user-123',
    email: 'test@example.com',
    name: 'Test User',
    role: 'user',
    permissions: ['read:dashboard', 'read:analytics'],
    tenants: ['default'],
    tier: 'free'
  }

  const payload = { ...defaultUser, ...userOverrides }
  return jwtAuth.generateToken(payload)
}

module.exports = {
  JWTAuth,
  jwtAuth,
  generateTestToken
}
