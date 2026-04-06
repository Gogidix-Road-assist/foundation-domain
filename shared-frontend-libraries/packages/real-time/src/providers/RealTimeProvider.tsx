import React, { useState, useCallback, useMemo } from 'react';
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from '@tanstack/react-query';
import { WebSocketProvider, useWebSocketContext } from './WebSocketProvider';

export interface RealTimeState {
  isConnected: boolean;
  lastUpdate: number;
  onlineUsers: Map<string, { status: string; lastSeen: number }>;
}

const queryClient = new QueryClient({
  defaultOptions: {
    refetchOnWindowFocus: false,
    retry: 1,
    staleTime: 30000,
  },
});

export const RealTimeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<RealTimeState>({
    isConnected: false,
    lastUpdate: Date.now(),
    onlineUsers: new Map(),
  });

  const onMessage = useCallback((message: any) => {
    if (message.type === 'users:update') {
      const users = message.payload;
      setState((prev) => ({
        ...prev,
        onlineUsers: new Map(
          Object.entries(users).map(([id, data]: [string, any]) => [
            id,
            {
              ...data,
              status: data.status || prev.onlineUsers.get(id)?.status || 'offline',
              lastSeen: Date.now(),
            },
          ]),
        ),
      }));
    }
  }, []);

  const { data: notifications } = useQuery({
    queryKey: ['notifications'],
    queryFn: async () => {
      const response = await fetch('/api/notifications');
      return response.json();
    },
    enabled: state.isConnected,
    refetchInterval: 10000,
  });

  return (
    <QueryClientProvider client={queryClient}>
      <WebSocketProvider onMessage={onMessage}>
        {children}
      </WebSocketProvider>
    </QueryClientProvider>
  );
};

export const useRealTime = () => {
  const { isConnected } = useWebSocketContext();
  return { isConnected };
};
