import { io } from 'socket.io-client';
import { useStore } from '../store/useStore';

const WS_URL = import.meta.env.VITE_WS_URL || 'http://localhost:3000';

class WebSocketService {
  constructor() {
    this.socket = null;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
  }

  connect(token) {
    if (this.socket?.connected) {
      return;
    }

    this.socket = io(WS_URL, {
      auth: { token },
      reconnection: true,
      reconnectionDelay: 1000,
      reconnectionAttempts: this.maxReconnectAttempts,
    });

    this.socket.on('connect', () => {
      console.log('WebSocket connected');
      useStore.getState().setConnected(true);
      this.reconnectAttempts = 0;
    });

    this.socket.on('disconnect', () => {
      console.log('WebSocket disconnected');
      useStore.getState().setConnected(false);
    });

    this.socket.on('connect_error', (error) => {
      console.error('WebSocket connection error:', error);
      this.reconnectAttempts++;
      if (this.reconnectAttempts >= this.maxReconnectAttempts) {
        console.error('Max reconnection attempts reached');
      }
    });

    // Service status updates
    this.socket.on('service:status:update', (data) => {
      useStore.getState().updateService(data.serviceId, data.status);
    });

    // Real-time metrics
    this.socket.on('metrics:update', (data) => {
      useStore.getState().updateMetric(data.key, data.value);
    });

    // Alerts
    this.socket.on('alert:new', (alert) => {
      useStore.getState().addAlert(alert);
    });

    // Dashboard updates
    this.socket.on('dashboard:update', (dashboard) => {
      useStore.getState().updateDashboard(dashboard.id, dashboard);
    });

    // Widget data updates
    this.socket.on('widget:data:update', (data) => {
      useStore.getState().updateWidget(data.widgetId, { data: data.data });
    });
  }

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
      useStore.getState().setConnected(false);
    }
  }

  joinDashboard(dashboardId) {
    if (this.socket?.connected) {
      this.socket.emit('dashboard:join', { dashboardId });
    }
  }

  leaveDashboard(dashboardId) {
    if (this.socket?.connected) {
      this.socket.emit('dashboard:leave', { dashboardId });
    }
  }

  subscribeToMetrics(serviceId) {
    if (this.socket?.connected) {
      this.socket.emit('metrics:subscribe', { serviceId });
    }
  }

  unsubscribeFromMetrics(serviceId) {
    if (this.socket?.connected) {
      this.socket.emit('metrics:unsubscribe', { serviceId });
    }
  }

  emit(event, data) {
    if (this.socket?.connected) {
      this.socket.emit(event, data);
    }
  }

  on(event, callback) {
    if (this.socket) {
      this.socket.on(event, callback);
    }
  }

  off(event, callback) {
    if (this.socket) {
      this.socket.off(event, callback);
    }
  }

  isConnected() {
    return this.socket?.connected || false;
  }
}

export const wsService = new WebSocketService();
