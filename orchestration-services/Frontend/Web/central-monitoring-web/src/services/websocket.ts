import { Client, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// WebSocket Configuration
const WS_BASE_URL = import.meta.env.VITE_WS_BASE_URL || 'http://localhost:8080/ws';

class WebSocketService {
  private client: Client | null = null;
  private connected: boolean = false;
  private subscriptions: Map<string, StompSubscription> = new Map();

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.connected) {
        resolve();
        return;
      }

      this.client = new Client({
        webSocketFactory: () => new SockJS(WS_BASE_URL),
        connectHeaders: {
          Authorization: `Bearer ${localStorage.getItem('auth_token') || ''}`,
        },
        debug: (str) => console.log('[WebSocket]', str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,

        onConnect: () => {
          console.log('WebSocket connected');
          this.connected = true;
          resolve();
        },

        onDisconnect: () => {
          console.log('WebSocket disconnected');
          this.connected = false;
        },

        onStompError: (frame) => {
          console.error('WebSocket error:', frame.headers['message']);
          reject(new Error(frame.headers['message']));
        },
      });

      this.client.activate();
    });
  }

  disconnect(): void {
    if (this.client) {
      this.subscriptions.forEach((sub) => sub.unsubscribe());
      this.subscriptions.clear();
      this.client.deactivate();
      this.connected = false;
    }
  }

  subscribe(destination: string, callback: (message: any) => void): StompSubscription {
    if (!this.client || !this.connected) {
      console.warn('WebSocket not connected. Auto-connecting...');
      this.connect().then(() => {
        return this.subscribe(destination, callback);
      });
      // Return a dummy subscription
      return {} as StompSubscription;
    }

    const subscription = this.client.subscribe(destination, (message) => {
      callback(message);
    });

    this.subscriptions.set(destination, subscription);
    return subscription;
  }

  unsubscribe(destination: string): void {
    const subscription = this.subscriptions.get(destination);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(destination);
    }
  }

  publish(destination: string, body: any): void {
    if (!this.client || !this.connected) {
      console.error('Cannot publish: WebSocket not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    });
  }

  isConnected(): boolean {
    return this.connected;
  }

  // Singleton pattern
  private static instance: WebSocketService;

  static getInstance(): WebSocketService {
    if (!WebSocketService.instance) {
      WebSocketService.instance = new WebSocketService();
    }
    return WebSocketService.instance;
  }
}

export const websocketService = WebSocketService.getInstance();

// Auto-connect on module load
websocketService.connect().catch((error) => {
  console.error('Failed to connect WebSocket:', error);
});

export default websocketService;
