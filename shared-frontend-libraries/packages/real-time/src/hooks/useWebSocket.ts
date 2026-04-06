import { useState, useEffect, useCallback, useRef } from 'react';

export interface WebSocketConfig {
  url: string;
  reconnectInterval?: number;
  heartbeatInterval?: number;
  maxReconnectAttempts?: number;
}

export type WebSocketStatus = 'connecting' | 'connected' | 'disconnected' | 'reconnecting' | 'error';

export interface WebSocketState {
  status: WebSocketStatus;
  error?: string;
  lastMessage?: any;
  connectionCount: number;
}

export type WebSocketMessage = {
  type: string;
  payload: any;
  timestamp?: number;
}

export interface UseWebSocketOptions {
  config: WebSocketConfig;
  onMessage?: (message: WebSocketMessage) => void;
  onStatusChange?: (status: WebSocketState) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
}

export const useWebSocket = (options: UseWebSocketOptions) => {
  const { config } = options;
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const heartbeatTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const heartbeatIntervalRef = useRef<NodeJS.Timeout | null>(null);

  const [state, setState] = useState<WebSocketState>({
    status: 'disconnected',
    connectionCount: 0,
  });

  const updateStatus = useCallback((status: WebSocketStatus, error?: string) => {
    setState((prev) => ({
      ...prev,
      status,
      error,
    }));
    options.onStatusChange?.({
      status,
      error,
      connectionCount: state.connectionCount + 1,
    });
  }, [state.connectionCount, options]);

  const connect = useCallback(() => {
    try {
      updateStatus('connecting');
      const ws = new WebSocket(config.url);
      wsRef.current = ws;

      ws.onopen = () => {
        updateStatus('connected');
        options.onConnect?.();
      };

      ws.onmessage = (event) => {
        const message: WebSocketMessage = JSON.parse(event.data);
        options.onMessage?.(message);
        setState((prev) => ({
          ...prev,
          lastMessage: message,
        }));
      };

      ws.onerror = () => {
        updateStatus('error', 'Connection error');
        scheduleReconnect();
      };

      ws.onclose = () => {
        updateStatus('disconnected');
        options.onDisconnect?.();
        scheduleReconnect();
      };

      startHeartbeat(ws);
    } catch (error) {
      updateStatus('error', error instanceof Error ? error.message : 'Failed to connect');
      scheduleReconnect();
    }
  }, [config.url, updateStatus, scheduleReconnect, startHeartbeat, options]);

  const disconnect = useCallback(() => {
    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
      updateStatus('disconnected');
    }
  }, [updateStatus]);

  const send = useCallback((message: any) => {
    if (wsRef.current?.readyState === WebSocket.OPEN) {
      wsRef.current.send(JSON.stringify(message));
    }
  }, []);

  const scheduleReconnect = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }

    const reconnect = () => {
      const maxAttempts = config.maxReconnectAttempts || 5;
      if (state.connectionCount >= maxAttempts) {
        return;
      }

      updateStatus('reconnecting');
      reconnectTimeoutRef.current = setTimeout(() => {
        connect();
      }, config.reconnectInterval || 3000);
    };

    reconnectTimeoutRef.current = setTimeout(reconnect, 1000);
  }, [config, state.connectionCount, updateStatus, connect]);

  const startHeartbeat = useCallback((ws: WebSocket) => {
    if (config.heartbeatInterval) {
      const heartbeat = () => {
        if (ws.readyState === WebSocket.OPEN) {
          try {
            ws.send(JSON.stringify({ type: 'ping' }));
          } catch (e) {
            // Ignore heartbeat errors
          }
        }
      };

      heartbeatIntervalRef.current = setInterval(heartbeat, config.heartbeatInterval);
    }
  }, [config.heartbeatInterval]);

  const stopHeartbeat = useCallback(() => {
    if (heartbeatTimeoutRef.current) {
      clearInterval(heartbeatTimeoutRef.current);
    }
    if (heartbeatIntervalRef.current) {
      clearInterval(heartbeatIntervalRef.current);
    }
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      disconnect();
      stopHeartbeat();
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
    };
  }, [disconnect, stopHeartbeat]);

  return {
    connect,
    disconnect,
    send,
    status: state.status,
    error: state.error,
    lastMessage: state.lastMessage,
  };
};
