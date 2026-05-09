import React from 'react'
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  Platform
} from 'react-native'
import * as Sentry from '@sentry/react-native'

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null
    }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true }
  }

  componentDidCatch(error, errorInfo) {
    // Log error to console
    console.error('ErrorBoundary caught an error:', error, errorInfo)

    // Log to crash reporting service (Sentry)
    if (Sentry && Sentry.captureException) {
      Sentry.captureException(error, {
        contexts: {
          react: {
            componentStack: errorInfo.componentStack
          }
        }
      })
    }

    // Store error details in state
    this.setState({
      error,
      errorInfo
    })

    // Log error details to API for monitoring
    this.logErrorToService(error, errorInfo)
  }

  logErrorToService(error, errorInfo) {
    const errorDetails = {
      message: error.message,
      stack: error.stack,
      componentStack: errorInfo.componentStack,
      timestamp: new Date().toISOString(),
      platform: Platform.OS,
      appVersion: '1.0.0'
    }

    // Send to error logging endpoint
    if (global.fetch) {
      fetch('/api/v1/errors/mobile', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(errorDetails)
      }).catch(err => console.error('Failed to log error:', err))
    }

    // In development, also log to console with full details
    if (__DEV__) {
      console.error('Error Details:', errorDetails)
    }
  }

  handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null
    })

    // Optionally reload the app
    if (this.props.onReset) {
      this.props.onReset()
    } else {
      // Reset by reloading
      Updates.reloadAsync()
    }
  }

  handleRestart = () => {
    // Complete app restart
    Updates.reloadAsync()
  }

  render() {
    if (this.state.hasError) {
      return (
        <View style={styles.container}>
          <View style={styles.contentContainer}>
            <View style={styles.iconContainer}>
              <Text style={styles.icon}>⚠️</Text>
            </View>

            <Text style={styles.title}>Oops!</Text>
            <Text style={styles.subtitle}>Something went wrong</Text>

            <Text style={styles.message}>
              We're sorry for the inconvenience. An unexpected error has occurred.
            </Text>

            {__DEV__ && this.state.error && (
              <ScrollView style={styles.errorDetailsContainer}>
                <Text style={styles.errorDetailsTitle}>Error Details (Development)</Text>
                <Text style={styles.errorText}>
                  {this.state.error.toString()}
                  {'\n\n'}
                  {this.state.errorInfo && this.state.errorInfo.componentStack}
                </Text>
              </ScrollView>
            )}

            <View style={styles.buttonContainer}>
              <TouchableOpacity
                style={[styles.button, styles.primaryButton]}
                onPress={this.handleReset}
              >
                <Text style={styles.primaryButtonText}>Try Again</Text>
              </TouchableOpacity>

              <TouchableOpacity
                style={[styles.button, styles.secondaryButton]}
                onPress={this.handleRestart}
              >
                <Text style={styles.secondaryButtonText}>Restart App</Text>
              </TouchableOpacity>
            </View>

            <Text style={styles.supportText}>
              If the problem persists, please contact:{'\n'}
              <Text style={styles.supportEmail}>support@gogidix.com</Text>
            </Text>
          </View>
        </View>
      )
    }

    return this.props.children
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20
  },
  contentContainer: {
    width: '100%',
    maxWidth: 400,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    padding: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 4
  },
  iconContainer: {
    alignItems: 'center',
    marginBottom: 16
  },
  icon: {
    fontSize: 64
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#dc3545',
    textAlign: 'center',
    marginBottom: 8
  },
  subtitle: {
    fontSize: 20,
    fontWeight: '600',
    color: '#212529',
    textAlign: 'center',
    marginBottom: 16
  },
  message: {
    fontSize: 16,
    color: '#6c757d',
    textAlign: 'center',
    marginBottom: 24,
    lineHeight: 24
  },
  errorDetailsContainer: {
    maxHeight: 200,
    backgroundColor: '#f8f9fa',
    borderRadius: 8,
    padding: 12,
    marginBottom: 24
  },
  errorDetailsTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#495057',
    marginBottom: 8
  },
  errorText: {
    fontSize: 12,
    color: '#6c757d',
    fontFamily: Platform.OS === 'ios' ? 'Courier' : 'monospace'
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 24,
    gap: 12
  },
  button: {
    flex: 1,
    paddingVertical: 14,
    paddingHorizontal: 20,
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center'
  },
  primaryButton: {
    backgroundColor: '#007bff'
  },
  primaryButtonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '600'
  },
  secondaryButton: {
    backgroundColor: '#6c757d'
  },
  secondaryButtonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '600'
  },
  supportText: {
    fontSize: 14,
    color: '#6c757d',
    textAlign: 'center',
    lineHeight: 20
  },
  supportEmail: {
    color: '#007bff',
    fontWeight: '600'
  }
})

export default ErrorBoundary
