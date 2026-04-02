import { useState, useCallback, useEffect } from 'react';

export interface PollingConfig {
  interval?: number;
  enabled?: boolean;
}

export interface PollingState {
  isPolling: boolean;
  lastPollTime: number;
  error?: string;
}

export interface UsePollingOptions {
  config: PollingConfig;
  onPoll: () => Promise<any>;
  onDataChange?: (data: any) => void;
  onError?: (error: string) => void;
}

export const usePolling = (options: UsePollingOptions) => {
  const { config = options } = options;
  const [state, setState] = useState<PollingState>({
    isPolling: false,
    lastPollTime: 0,
  });

  const poll = useCallback(async () => {
    if (!config.enabled) {
      return;
    }

    try {
      setState((prev) => ({ ...prev, isPolling: true }));

      const data = await options.onPoll?.();
      options.onDataChange?.(data);
    } catch (error) {
      options.onError?.(error instanceof Error ? error.message : 'Polling failed');
    } finally {
      setState((prev) => ({ ...prev, isPolling: false, lastPollTime: Date.now() }));
    }
  }, [config.enabled, config.interval, options.onPoll, options.onDataChange, options.onError]);

  const start = useCallback(() => {
    if (config.interval) {
      poll();
    }
  }, [poll, config.interval]);

  return {
    isPolling: state.isPolling,
    lastPollTime: state.lastPollTime,
    error: state.error,
    start,
  };
};
