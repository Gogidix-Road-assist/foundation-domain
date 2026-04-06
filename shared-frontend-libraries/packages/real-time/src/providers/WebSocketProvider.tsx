import React, { createContext, useContext, useMemo } from 'react';
import { useWebSocket, type WebSocketMessage } from '../hooks/useWebSocket';

export interface WebSocketProviderProps {
  children: React.ReactNode;
  wsUrl?: string;
  onMessage?: (message: WebSocketMessage) => void;
  onConnect?: () => void;
  onDisconnect?: () => void;
}

export interface WebSocketContextType {
  ws: ReturnType<typeof useWebSocket>;
  isConnected: boolean;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider: React.FC<WebSocketProviderProps> = ({
  children,
  wsUrl,
  onMessage,
  onConnect,
  onDisconnect,
}) => {
  const config = useMemo(
    () => ({
      url: wsUrl || process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080',
      reconnectInterval: 5000,
      maxReconnectAttempts: 5,
    }),
    [wsUrl]
  );

  const wsValue = useWebSocket({
    config,
    onMessage,
    onConnect,
    onDisconnect,
  });

  return (
    <WebSocketContext.Provider value={{ ws: wsValue, isConnected: wsValue.status === 'connected' }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocketContext = () => {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error('useWebSocketContext must be used within WebSocketProvider');
  }
  return context;
};
