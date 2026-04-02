import React, { createContext, useContext, useMemo } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';

export interface WebSocketProviderState {
  config?: {
    url: string;
    reconnectInterval?: number;
  };
}

export interface WebSocketContextType extends WebSocketProviderState {
  ws: ReturnType<typeof useWebSocket> | null;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const config = useMemo(
    () => ({
      url: process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080',
      reconnectInterval: 5000,
      maxReconnectAttempts: 5,
    }),
    []
  );

  return (
    <WebSocketContext.Provider value={useWebSocket(config)}>
      {children}
    </WebSocketContext.Provider>
  );
};
