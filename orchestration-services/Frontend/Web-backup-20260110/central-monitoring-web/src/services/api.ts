/**
 * API Service for Central Monitoring Dashboard
 */

import axios, { AxiosInstance } from 'axios';
import type {
  LocationUpdate,
  PartnerLocation,
  ServiceRequest,
  DashboardStats,
  Alert,
  BroadcastMessage,
  EntityType,
  EntityStatus,
  WithinRadiusRequest
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8083';

class ApiService {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: `${API_BASE_URL}/api/central`,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // Request interceptor
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

  // ========== LOCATION API ==========

  /**
   * Submit a location update
   */
  async submitLocationUpdate(data: {
    entityType: EntityType;
    entityId: string;
    latitude: number;
    longitude: number;
    address?: string;
    status: EntityStatus;
    currentJobId?: string;
    speed?: number;
    heading?: number;
    batteryLevel?: number;
  }): Promise<LocationUpdate> {
    const response = await this.client.post('/location/update', data);
    return response.data;
  }

  /**
   * Get all partner locations
   */
  async getPartnerLocations(): Promise<PartnerLocation[]> {
    const response = await this.client.get('/location/partners');
    return response.data;
  }

  /**
   * Get all user locations
   */
  async getUserLocations(): Promise<LocationUpdate[]> {
    const response = await this.client.get('/location/users');
    return response.data;
  }

  /**
   * Find entities within a radius
   */
  async findWithinRadius(request: WithinRadiusRequest): Promise<PartnerLocation[]> {
    const response = await this.client.post('/location/within-radius', request);
    return response.data;
  }

  /**
   * Get latest location for an entity
   */
  async getLatestLocation(
    entityType: EntityType,
    entityId: string
  ): Promise<LocationUpdate | null> {
    try {
      const response = await this.client.get(`/location/${entityType}/${entityId}`);
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  }

  /**
   * Get location history for an entity
   */
  async getLocationHistory(
    entityType: EntityType,
    entityId: string
  ): Promise<LocationUpdate[]> {
    const response = await this.client.get(`/location/${entityType}/${entityId}/history`);
    return response.data;
  }

  /**
   * Mark entity as offline
   */
  async markAsOffline(entityType: EntityType, entityId: string): Promise<void> {
    await this.client.delete(`/location/${entityType}/${entityId}`);
  }

  // ========== DASHBOARD API ==========

  /**
   * Get dashboard statistics
   */
  async getDashboardStats(): Promise<DashboardStats> {
    const response = await this.client.get('/location/dashboard/stats');
    return response.data;
  }

  // ========== MONITORING API ==========

  /**
   * Get all active service requests
   */
  async getActiveRequests(): Promise<ServiceRequest[]> {
    const response = await this.client.get('/monitoring/active-requests');
    return response.data;
  }

  /**
   * Get monitoring performance metrics
   */
  async getPerformanceMetrics(timeRange: string = '24h'): Promise<any> {
    const response = await this.client.get(`/monitoring/performance?range=${timeRange}`);
    return response.data;
  }

  // ========== ALERTS API ==========

  /**
   * Get all active alerts
   */
  async getAlerts(): Promise<Alert[]> {
    const response = await this.client.get('/alerts');
    return response.data;
  }

  /**
   * Get alert history
   */
  async getAlertHistory(limit: number = 100): Promise<Alert[]> {
    const response = await this.client.get(`/alerts/history?limit=${limit}`);
    return response.data;
  }

  /**
   * Acknowledge an alert
   */
  async acknowledgeAlert(alertId: string): Promise<void> {
    await this.client.post(`/alerts/${alertId}/acknowledge`);
  }

  // ========== DISPATCH API ==========

  /**
   * Assign partner to request
   */
  async assignPartner(data: {
    requestId: string;
    partnerId: string;
    driverId: string;
    method: 'auto' | 'manual';
    assignedBy: string;
  }): Promise<any> {
    const response = await this.client.post('/coordination/assign', data);
    return response.data;
  }

  /**
   * Get dispatch queue
   */
  async getDispatchQueue(): Promise<ServiceRequest[]> {
    const response = await this.client.get('/coordination/queue');
    return response.data;
  }

  /**
   * Send broadcast message
   */
  async sendBroadcast(data: {
    recipientType: 'all' | 'towing' | 'mechanics' | 'specific-partner';
    recipientId?: string;
    subject: string;
    message: string;
    priority: 'information' | 'advisory' | 'urgent' | 'emergency';
  }): Promise<BroadcastMessage> {
    const response = await this.client.post('/coordination/broadcast', data);
    return response.data;
  }
}

// Export singleton instance
export const apiService = new ApiService();
export default apiService;
