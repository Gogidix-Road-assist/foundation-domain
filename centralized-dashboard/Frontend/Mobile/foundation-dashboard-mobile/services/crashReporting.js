import { Platform } from 'react-native'
import * as Sentry from '@sentry/react-native'

/**
 * Crash Reporting Service
 * Integrates with Sentry for error tracking and crash reporting
 */

class CrashReportingService {
  constructor() {
    this.initialized = false
    this.enabled = false
  }

  /**
   * Initialize crash reporting
   * @param {Object} config - Configuration object
   */
  initialize(config = {}) {
    try {
      if (!config.dsn) {
        console.warn('CrashReporting: No DSN provided, crash reporting disabled')
        return
      }

      const SentryConfig = {
        dsn: config.dsn,
        enableAutoSessionTracking: config.enableAutoSessionTracking !== false,
        sessionTrackingIntervalMillis: config.sessionTrackingIntervalMillis || 30000,
        enableInExpoDevelopment: config.enableInExpoDevelopment || true,
        debug: config.debug || __DEV__,
        environment: config.environment || __DEV__ ? 'development' : 'production',
        tracesSampleRate: config.tracesSampleRate || 0.2,
        beforeSend: this.beforeSend.bind(this),
        attachStacktrace: true,
        attachScreenshot: config.attachScreenshot || false,
        attachViewHierarchy: config.attachViewHierarchy || false
      }

      // Initialize Sentry
      Sentry.init(SentryConfig)

      this.initialized = true
      this.enabled = true

      console.log('CrashReporting: Initialized successfully')
    } catch (error) {
      console.error('CrashReporting: Failed to initialize:', error)
    }
  }

  /**
   * Before send hook to filter or modify events
   */
  beforeSend(event, hint) {
    // Don't send events in development unless explicitly enabled
    if (__DEV__ && !this.enableInDev) {
      return null
    }

    // Add custom context
    event.contexts = {
      ...event.contexts,
      app: {
        ...event.contexts?.app,
        name: 'Foundation Dashboard',
        version: '1.0.0'
      },
      device: {
        ...event.contexts?.device,
        platform: Platform.OS,
        version: Platform.Version
      }
    }

    // Filter out certain errors
    if (event.exception) {
      const errorMessage = event.exception.values?.[0]?.value

      // Ignore network errors that are expected
      if (errorMessage && this.shouldIgnoreError(errorMessage)) {
        return null
      }
    }

    return event
  }

  /**
   * Determine if an error should be ignored
   */
  shouldIgnoreError(errorMessage) {
    const ignoredErrors = [
      'Network request failed',
      'Timeout',
      'AbortError'
    ]

    return ignoredErrors.some(pattern =>
      errorMessage.toLowerCase().includes(pattern.toLowerCase())
    )
  }

  /**
   * Capture an exception
   * @param {Error} error - Error object
   * @param {Object} extra - Extra data to attach
   */
  captureException(error, extra = {}) {
    if (!this.enabled || !this.initialized) {
      console.error('CrashReporting (not sent):', error, extra)
      return
    }

    Sentry.captureException(error, {
      extra: {
        ...extra,
        timestamp: new Date().toISOString()
      }
    })
  }

  /**
   * Capture a message
   * @param {string} message - Message to capture
   * @param {string} level - Log level (info, warning, error)
   * @param {Object} extra - Extra data to attach
   */
  captureMessage(message, level = 'info', extra = {}) {
    if (!this.enabled || !this.initialized) {
      console.log(`CrashReporting [${level}]:`, message, extra)
      return
    }

    Sentry.captureMessage(message, {
      level,
      extra: {
        ...extra,
        timestamp: new Date().toISOString()
      }
    })
  }

  /**
   * Set user context
   * @param {Object} user - User object
   */
  setUser(user) {
    if (!this.initialized) return

    Sentry.setUser({
      id: user.id,
      email: user.email,
      username: user.name || user.email,
      ...user
    })
  }

  /**
   * Clear user context
   */
  clearUser() {
    if (!this.initialized) return

    Sentry.setUser(null)
  }

  /**
   * Add breadcrumb
   * @param {Object} breadcrumb - Breadcrumb object
   */
  addBreadcrumb(breadcrumb) {
    if (!this.initialized) return

    Sentry.addBreadcrumb({
      timestamp: new Date() / 1000,
      ...breadcrumb
    })
  }

  /**
   * Set tag
   * @param {string} key - Tag key
   * @param {string} value - Tag value
   */
  setTag(key, value) {
    if (!this.initialized) return

    Sentry.setTag(key, value)
  }

  /**
   * Set context
   * @param {string} key - Context key
   * @param {Object} value - Context value
   */
  setContext(key, value) {
    if (!this.initialized) return

    Sentry.setContext(key, value)
  }

  /**
   * Start a performance transaction
   * @param {string} name - Transaction name
   * @param {string} operation - Operation type
   */
  startTransaction(name, operation = 'navigation') {
    if (!this.initialized) return null

    return Sentry.startTransaction({ name, operation })
  }

  /**
   * Capture a handled error
   * @param {Error} error - Error object
   * @param {Object} context - Additional context
   */
  captureError(error, context = {}) {
    if (!this.enabled || !this.initialized) {
      console.error('CrashReporting (not sent):', error, context)
      return
    }

    Sentry.withScope((scope) => {
      // Add context to scope
      if (context.user) {
        scope.setUser(context.user)
      }
      if (context.tags) {
        Object.entries(context.tags).forEach(([key, value]) => {
          scope.setTag(key, value)
        })
      }
      if (context.extra) {
        scope.setExtras(context.extra)
      }

      Sentry.captureException(error)
    })
  }

  /**
   * Record a custom event
   * @param {string} name - Event name
   * @param {Object} data - Event data
   */
  recordEvent(name, data = {}) {
    this.addBreadcrumb({
      category: 'custom',
      message: name,
      data,
      level: 'info'
    })
  }

  /**
   * Enable/disable crash reporting
   * @param {boolean} enabled - Enable or disable
   */
  setEnabled(enabled) {
    this.enabled = enabled
  }

  /**
   * Check if crash reporting is enabled
   */
  isEnabled() {
    return this.enabled && this.initialized
  }
}

// Export singleton instance
const crashReporting = new CrashReportingService()

export default crashReporting
