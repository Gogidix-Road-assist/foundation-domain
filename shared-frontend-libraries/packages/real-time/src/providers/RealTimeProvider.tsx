import React, { useState, useCallback, useMemo, useEffect } from 'react';
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
  useMutation,
} from '@tanstack/react-query';
import { WebSocketProvider } from './WebSocketProvider';
import { useWebSocket } from '../hooks/useWebSocket';

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

  const { ws } = useWebSocket({
    onMessage: useCallback((message) => {
      if (message.type === 'users:update') {
        const users = message.payload as any;
        setState((prev) => ({
          ...prev,
          onlineUsers: new Map(
            Object.entries(users).map(([id, data]: [string, any]) => [
              id,
              {
                ...data,
                status: data.status || prev.onlineUsers.get(id)?.status || 'offline',
                lastSeen: Date.now(),
              } as any,
            ],
          ])
        ));
      }
    },
  });

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
      {children}
    </QueryClientProvider>
  );
};
