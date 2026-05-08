/**
 * WebSocket Service for real-time location updates
 * Uses SockJS and STOMP over WebSocket
 */

import SockJS from 'sockjs-client';
import { Stomp, Frame, StompSubscription } from '@stomp/stompjs';
import type { LocationUpdate, DashboardStats, PartnerLocation, Alert } from '../types';

const WS_BASE_URL = import.meta.env.VITE_WS_BASE_URL || 'http://localhost:8083';

type MessageCallback<T> = (message: T) => void;
type ConnectionState = 'connecting' | 'connected' | 'disconnected' | 'error';

class WebSocketService {
  private client: any = null;
  private connectionState: ConnectionState = 'disconnected';
  private subscriptions: Map<string, StompSubscription> = new Map();
  private reconnectTimer: NodeJS.Timeout | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;

  // Callback registries
  private onLocationUpdateCallbacks: Set<MessageCallback<LocationUpdate>> = new Set();
  private onPartnerUpdateCallbacks: Set<MessageCallback<PartnerLocation>> = new Set();
  private onUserUpdateCallbacks: Set<MessageCallback<LocationUpdate>> = new Set();
  private onDashboardStatsCallbacks: Set<MessageCallback<DashboardStats>> = new Set();
  private onAlertCallbacks: Set<MessageCallback<Alert>> = new Set();
  private onConnectionChangeCallbacks: Set<MessageCallback<ConnectionState>> = new Set();

  /**
   * Connect to WebSocket server
   */
  connect(): void {
    if (this.connectionState === 'connected' || this.connectionState === 'connecting') {
      return;
    }

    this.setConnectionState('connecting');

    this.client = Stomp.over(() => {
      return new SockJS(`${WS_BASE_URL}/ws/location`);
    });

    // Disable debug logging in production
    this.client.debug = () => {};

    this.client.connect(
      {},
      (frame: Frame) => {
        console.log('WebSocket connected:', frame);
        this.setConnectionState('connected');
        this.reconnectAttempts = 0;
        this.subscribeToTopics();
      },
      (error: any) => {
        console.error('WebSocket connection error:', error);
        this.setConnectionState('error');
        this.scheduleReconnect();
      }
    );
  }

  /**
   * Disconnect from WebSocket server
   */
  disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }

    if (this.client) {
      this.client.disconnect(() => {
        console.log('WebSocket disconnected');
        this.setConnectionState('disconnected');
      });
    }

    this.subscriptions.clear();
  }

  /**
   * Subscribe to all default topics
   */
  private subscribeToTopics(): void {
    // Location updates
    this.subscribe('/topic/location/updates', (message: LocationUpdate) => {
      this.onLocationUpdateCallbacks.forEach(cb => cb(message));
    });

    // Partner updates
    this.subscribe('/topic/location/partners', (message: any) => {
      this.onPartnerUpdateCallbacks.forEach(cb => cb(message));
    });

    // User updates
    this.subscribe('/topic/location/users', (message: LocationUpdate) => {
      this.onUserUpdateCallbacks.forEach(cb => cb(message));
    });

    // Dashboard stats
    this.subscribe('/topic/location/dashboard/stats', (message: DashboardStats) => {
      this.onDashboardStatsCallbacks.forEach(cb => cb(message));
    });

    // All partners (broadcast every 2 seconds)
    this.subscribe('/topic/location/partners/all', (message: PartnerLocation[]) => {
      this.onPartnerUpdateCallbacks.forEach(cb => {
        message.forEach(p => cb(p));
      });
    });
  }

  /**
   * Subscribe to a specific topic
   */
  subscribe<T>(topic: string, callback: MessageCallback<T>): void {
    if (this.client && this.connectionState === 'connected') {
      const subscription = this.client.subscribe(topic, (message: any) => {
        try {
          const parsedBody = JSON.parse(message.body);
          callback(parsedBody);
        } catch (e) {
          callback(message.body as unknown as T);
        }
      });
      this.subscriptions.set(topic, subscription);
    }
  }

  /**
   * Subscribe to updates for a specific entity
   */
  subscribeToEntity(entityType: string, entityId: string, callback: MessageCallback<LocationUpdate>): void {
    const topic = `/topic/location/entity/${entityType}/${entityId}`;
    this.subscribe(topic, callback);
  }

  /**
   * Subscribe to nearby entities
   */
  subscribeToNearby(lat: number, lon: number, radius: number, callback: MessageCallback<PartnerLocation[]>): void {
    const topic = `/topic/location/nearby/${lat}/${lon}/${radius}`;
    this.subscribe(topic, callback);
  }

  /**
   * Send location update via WebSocket
   */
  sendLocationUpdate(locationUpdate: any): void {
    if (this.client && this.connectionState === 'connected') {
      this.client.send('/app/location/update', {}, JSON.stringify(locationUpdate));
    }
  }

  // ========== Event Listeners ==========

  onLocationUpdate(callback: MessageCallback<LocationUpdate>): () => void {
    this.onLocationUpdateCallbacks.add(callback);
    return () => this.onLocationUpdateCallbacks.delete(callback);
  }

  onPartnerUpdate(callback: MessageCallback<PartnerLocation>): () => void {
    this.onPartnerUpdateCallbacks.add(callback);
    return () => this.onPartnerUpdateCallbacks.delete(callback);
  }

  onUserUpdate(callback: MessageCallback<LocationUpdate>): () => void {
    this.onUserUpdateCallbacks.add(callback);
    return () => this.onUserUpdateCallbacks.delete(callback);
  }

  onDashboardStats(callback: MessageCallback<DashboardStats>): () => void {
    this.onDashboardStatsCallbacks.add(callback);
    return () => this.onDashboardStatsCallbacks.delete(callback);
  }

  onAlert(callback: MessageCallback<Alert>): () => void {
    this.onAlertCallbacks.add(callback);
    return () => this.onAlertCallbacks.delete(callback);
  }

  onConnectionChange(callback: MessageCallback<ConnectionState>): () => void {
    this.onConnectionChangeCallbacks.add(callback);
    // Immediately call with current state
    callback(this.connectionState);
    return () => this.onConnectionChangeCallbacks.delete(callback);
  }

  // ========== Helper Methods ==========

  private setConnectionState(state: ConnectionState): void {
    if (this.connectionState !== state) {
      this.connectionState = state;
      this.onConnectionChangeCallbacks.forEach(cb => cb(state));
    }
  }

  private scheduleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnect attempts reached');
      this.setConnectionState('error');
      return;
    }

    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
    this.reconnectAttempts++;

    console.log(`Scheduling reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);

    this.reconnectTimer = setTimeout(() => {
      this.connect();
    }, delay);
  }

  getConnectionState(): ConnectionState {
    return this.connectionState;
  }

  isConnected(): boolean {
    return this.connectionState === 'connected';
  }
}

// Export singleton instance
export const wsService = new WebSocketService();
export default wsService;
