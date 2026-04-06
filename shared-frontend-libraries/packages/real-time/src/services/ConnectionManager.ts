import { WebSocketStatus } from '../types/websocket.types';

export interface ConnectionState {
  isConnected: boolean;
  isReconnecting: boolean;
  connectionId?: string;
  lastConnectedAt?: Date;
}

export type ConnectionStateListener = (state: ConnectionState) => void;

export class ConnectionManager {
  private static instance: ConnectionManager | null = null;
  private connections: Map<string, WebSocket> = new Map();
  private listeners: Set<ConnectionStateListener> = new Set();

  static getInstance(): ConnectionManager {
    if (!ConnectionManager.instance) {
      ConnectionManager.instance = new ConnectionManager();
    }
    return ConnectionManager.instance;
  }

  private constructor() {}

  public connect(id: string, connection: WebSocket): void {
    this.connections.set(id, connection);
    this.notifyListeners({ id, isConnected: true, lastConnectedAt: new Date() });
  }

  public disconnect(id: string): void {
    const connection = this.connections.get(id);
    if (connection) {
      connection.close();
      this.connections.delete(id);
      this.notifyListeners({ id, isConnected: false });
    }
  }

  public subscribe(listener: ConnectionStateListener): () => void {
    this.listeners.add(listener);
    this.notifyListeners(this.getCurrentState());
    return () => this.unsubscribe(listener);
  }

  public unsubscribe(listener: ConnectionStateListener): void {
    this.listeners.delete(listener);
  }

  private getCurrentState(): ConnectionState {
    const connections = Array.from(this.connections.values());
    if (connections.length === 0) {
      return { isConnected: false, isReconnecting: false };
    }

    const hasConnectedConnection = connections.some((conn) => {
      try {
        return conn.readyState === WebSocket.OPEN || conn.readyState === 'open';
      } catch {
        return false;
      }
    });

    const isConnecting = connections.some((conn) => {
      try {
        return conn.readyState === WebSocket.CONNECTING || conn.readyState === 'connecting';
      } catch {
        return false;
      }
    });

    const latestConnection = connections.find((conn) => {
      try {
        return conn.readyState === WebSocket.OPEN || conn.readyState === 'open';
      } catch {
        return false;
      }
    });

    return {
      isConnected: hasConnectedConnection,
      isReconnecting: isConnecting,
      lastConnectedAt: (latestConnection as any)?.lastConnectedAt,
      connectionId: (latestConnection as any)?.url,
    };
  }

  public getConnectedCount(): number {
    return Array.from(this.connections.values()).filter((conn) => {
      const status = conn.readyState as WebSocketStatus;
      return status === WebSocket.OPEN || status === 'open';
    }).length;
  }

  private notifyListeners(state: ConnectionState): void {
    this.listeners.forEach((listener) => {
      try {
        listener(state);
      } catch (error) {
        console.error('Error notifying listener:', error);
      }
    });
  }

  public getConnectedIds(): string[] {
    return Array.from(this.connections.keys()).filter((id) => {
      const connection = this.connections.get(id);
      return connection && (connection.readyState === WebSocket.OPEN || connection.readyState === 'open');
    });
  }
}

export const useConnection = () => {
  return ConnectionManager.getInstance();
};
