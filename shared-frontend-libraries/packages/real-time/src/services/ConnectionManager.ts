export interface ConnectionState {
  isConnected: boolean;
  isReconnecting: boolean;
  lastConnectedAt?: Date;
}

export class ConnectionManager {
  private instance: ConnectionManager | null = null;

  static getInstance(): ConnectionManager {
    if (!ConnectionManager.instance) {
      ConnectionManager.instance = new ConnectionManager();
    }
    return ConnectionManager.instance;
  }

  private constructor() {
    this.connections = new Map<string, any>();
    this.listeners = new Set<(state: ConnectionState) => void>();
  }

  public connect(id: string, connection: any): void {
    this.connections.set(id, connection);
    this.notifyListeners({ id, isConnected: true, lastConnectedAt: new Date() });
  }

  public disconnect(id: string): void {
    const connection = this.connections.get(id);
    if (connection) {
      connection.close?.();
      this.connections.delete(id);
      this.notifyListeners({ id, isConnected: false });
    }
  }

  public subscribe(listener: (state: ConnectionState) => void): () => void {
    this.listeners.add(listener);
    // Send current state immediately
    const currentState = this.getCurrentState();
    Object.values(this.connections).forEach((_, conn) => {
      listener(currentState);
    });
  }

  public unsubscribe(listener: (state: ConnectionState) => void): () => void {
    this.listeners.delete(listener);
  }

  private getCurrentState(): ConnectionState {
    const connections = Array.from(this.connections.values());
    if (connections.length === 0) {
      return { isConnected: false, isReconnecting: false };
    }
    const hasConnectedConnection = connections.some((conn) => {
      try {
        return conn.readyState === 1 || conn.readyState === 'open';
      } catch {
        return false;
      }
    });
    return {
      isConnected: hasConnectedConnection,
      isReconnecting: false,
      lastConnectedAt: undefined,
    };
  }

  private notifyListeners(state: ConnectionState): void {
    this.listeners.forEach((listener) => listener(state));
  }
}

export const useConnection = () => {
  return ConnectionManager.getInstance();
};
