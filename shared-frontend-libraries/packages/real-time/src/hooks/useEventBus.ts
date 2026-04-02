import { useCallback, useContext } from 'react';
import { RealTimeContext } from '../contexts/RealTimeContext';

export interface UseEventBusOptions {
  eventType?: string;
}

export const useEventBus = (options: UseEventBusOptions = {}) => {
  const { eventType } = options;
  const context = useContext(RealTimeContext);

  const publish = useCallback((data: any) => {
    context.publish?.({ type: eventType || 'custom', data });
  }, [context.publish, eventType]);

  const subscribe = useCallback((handler: (data: any) => {
    return context.subscribe?.({ type: eventType || 'custom', handler });
  }, [context.subscribe, eventType]);

  const unsubscribe = useCallback((handler: (data: any) => {
    return context.unsubscribe?.({ type: eventType || 'custom', handler });
  }, [context.unsubscribe, eventType]);

  return {
    publish,
    subscribe,
    unsubscribe,
  };
};
