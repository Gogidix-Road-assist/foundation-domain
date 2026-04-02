import { useCallback } from 'react';
import { RealTimeContext } from '../contexts/RealTimeContext';

export interface RealTimeData {
  requests?: any[];
  notifications?: any[];
  alerts?: any[];
  users?: Map<string, { status: string; lastSeen: number }>;
}

export interface UseRealTimeDataOptions {
  onDataUpdate?: (data: RealTimeData) => void;
  refreshInterval?: number;
}

export const useRealTimeData = (options: UseRealTimeDataOptions = {}) => {
  const context = useContext(RealTimeContext);

  const fetchData = useCallback(async () => {
    try {
      const response = await fetch('/api/realtime/data');
      const data: RealTimeData = await response.json();
      options.onDataUpdate?.(data);
    } catch (error) {
      console.error('Failed to fetch real-time data:', error);
    }
  }, [options.refreshInterval, options.onDataUpdate]);

  return {
    data: context.data,
    refresh: fetchData,
  };
};
