import { ConnectionManager, useConnection } from './ConnectionManager';
import { RealTimeContext } from '../contexts/RealTimeContext';

export type EventType = 'user_joined' | 'user_left' | 'message_sent' | 'alert_triggered';

export interface EventData {
  type: EventType;
  payload?: any;
  timestamp?: number;
  userId?: string;
}

export interface PublishOptions {
  broadcast?: boolean;
  targetUserIds?: string[];
}

export class EventPublisher {
  private manager: ConnectionManager = useConnection();

  public publish(event: EventData, options: PublishOptions = {}): void {
    const connection = this.manager.getCurrentState();

    if (connection.isConnected) {
      const eventData: EventData = {
        ...event,
        timestamp: Date.now(),
      };

      this.manager.notifyListeners({ ...eventData });
    }
  }

  public subscribe(listener: (data: EventData) => void): () => void {
    this.manager.subscribe((state) => {
      if (state.isConnected) {
        listener(data);
      }
    });
  }

  public unsubscribe(listener: (data: EventData) => void): () => void {
    this.manager.unsubscribe(listener);
  }
}
