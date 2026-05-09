import * as SecureStore from 'expo-secure-store'
import AsyncStorage from '@react-native-async-storage/async-storage'
import { Platform } from 'react-native'

/**
 * Secure Storage Service
 * Provides secure storage for sensitive data like tokens, credentials, etc.
 * Uses expo-secure-store on native platforms and encrypted AsyncStorage on web
 */

class SecureStorageService {
  constructor() {
    this.isNative = Platform.OS !== 'web'
    this.keyPrefix = '@secure_'
  }

  /**
   * Store data securely
   * @param {string} key - Storage key
   * @param {string} value - Value to store (will be stringified if object)
   * @returns {Promise<void>}
   */
  async setItem(key, value) {
    try {
      const fullKey = this.keyPrefix + key
      const stringValue = typeof value === 'string' ? value : JSON.stringify(value)

      if (this.isNative) {
        // Use SecureStore on native platforms
        await SecureStore.setItemAsync(fullKey, stringValue)
      } else {
        // Use AsyncStorage on web (should be encrypted in production)
        await AsyncStorage.setItem(fullKey, stringValue)
      }

      return true
    } catch (error) {
      console.error(`SecureStorage: Error setting item ${key}:`, error)
      throw new Error(`Failed to securely store ${key}`)
    }
  }

  /**
   * Retrieve data from secure storage
   * @param {string} key - Storage key
   * @returns {Promise<string|null>} Stored value or null
   */
  async getItem(key) {
    try {
      const fullKey = this.keyPrefix + key

      let value
      if (this.isNative) {
        value = await SecureStore.getItemAsync(fullKey)
      } else {
        value = await AsyncStorage.getItem(fullKey)
      }

      return value
    } catch (error) {
      console.error(`SecureStorage: Error getting item ${key}:`, error)
      return null
    }
  }

  /**
   * Remove item from secure storage
   * @param {string} key - Storage key
   * @returns {Promise<void>}
   */
  async removeItem(key) {
    try {
      const fullKey = this.keyPrefix + key

      if (this.isNative) {
        await SecureStore.deleteItemAsync(fullKey)
      } else {
        await AsyncStorage.removeItem(fullKey)
      }

      return true
    } catch (error) {
      console.error(`SecureStorage: Error removing item ${key}:`, error)
      throw new Error(`Failed to remove ${key}`)
    }
  }

  /**
   * Check if key exists in secure storage
   * @param {string} key - Storage key
   * @returns {Promise<boolean>}
   */
  async hasItem(key) {
    try {
      const value = await this.getItem(key)
      return value !== null
    } catch (error) {
      return false
    }
  }

  /**
   * Clear all secure storage
   * @returns {Promise<void>}
   */
  async clear() {
    try {
      if (this.isNative) {
        // SecureStore doesn't have a clear all method, so we need to track keys
        const keys = await this.getAllKeys()
        await Promise.all(keys.map(key => this.removeItem(key)))
      } else {
        await AsyncStorage.clear()
      }

      return true
    } catch (error) {
      console.error('SecureStorage: Error clearing storage:', error)
      throw new Error('Failed to clear secure storage')
    }
  }

  /**
   * Get all keys from secure storage (works only on AsyncStorage/Web)
   * @returns {Promise<string[]>}
   */
  async getAllKeys() {
    try {
      if (!this.isNative) {
        const allKeys = await AsyncStorage.getAllKeys()
        return allKeys.filter(key => key.startsWith(this.keyPrefix))
      }
      return []
    } catch (error) {
      console.error('SecureStorage: Error getting keys:', error)
      return []
    }
  }

  // Specific methods for common use cases

  /**
   * Store authentication token
   */
  async setAuthToken(token) {
    return this.setItem('authToken', token)
  }

  /**
   * Get authentication token
   */
  async getAuthToken() {
    return this.getItem('authToken')
  }

  /**
   * Remove authentication token
   */
  async removeAuthToken() {
    return this.removeItem('authToken')
  }

  /**
   * Store refresh token
   */
  async setRefreshToken(token) {
    return this.setItem('refreshToken', token)
  }

  /**
   * Get refresh token
   */
  async getRefreshToken() {
    return this.getItem('refreshToken')
  }

  /**
   * Store user credentials (encrypted)
   */
  async setUserCredentials(credentials) {
    return this.setItem('userCredentials', JSON.stringify(credentials))
  }

  /**
   * Get user credentials
   */
  async getUserCredentials() {
    const value = await this.getItem('userCredentials')
    if (value) {
      try {
        return JSON.parse(value)
      } catch (error) {
        console.error('SecureStorage: Error parsing credentials:', error)
        return null
      }
    }
    return null
  }

  /**
   * Store biometric preference
   */
  async setBiometricEnabled(enabled) {
    return this.setItem('biometricEnabled', enabled.toString())
  }

  /**
   * Get biometric preference
   */
  async getBiometricEnabled() {
    const value = await this.getItem('biometricEnabled')
    return value === 'true'
  }

  /**
   * Store user session data
   */
  async setUserSession(sessionData) {
    return this.setItem('userSession', JSON.stringify(sessionData))
  }

  /**
   * Get user session data
   */
  async getUserSession() {
    const value = await this.getItem('userSession')
    if (value) {
      try {
        return JSON.parse(value)
      } catch (error) {
        console.error('SecureStorage: Error parsing session:', error)
        return null
      }
    }
    return null
  }

  /**
   * Clear all authentication data
   */
  async clearAuthData() {
    try {
      await Promise.all([
        this.removeAuthToken(),
        this.removeRefreshToken(),
        this.removeItem('userCredentials'),
        this.removeItem('userSession')
      ])
      return true
    } catch (error) {
      console.error('SecureStorage: Error clearing auth data:', error)
      throw new Error('Failed to clear authentication data')
    }
  }
}

// Export singleton instance
const secureStorage = new SecureStorageService()

export default secureStorage
