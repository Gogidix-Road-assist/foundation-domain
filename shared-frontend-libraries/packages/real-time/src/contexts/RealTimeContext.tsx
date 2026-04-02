import React, { createContext, Context, useContext } from 'react';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface RealTimeContextType {
  ws: ReturnType<typeof import('./hooks/useWebSocket').useWebSocket> | null;
  onlineUsers: Map<string, { status: string; lastSeen: number }>;
  data: {
    notifications?: any[];
    alerts?: any[];
    users?: Map<string, { status: string; lastSeen: number }>;
  };
}

export const RealTimeContext = createContext<RealTimeContextType | null>({
  ws: null,
  onlineUsers: new Map(),
  data: {
    notifications: [],
    alerts: [],
    users: new Map(),
  },
});
