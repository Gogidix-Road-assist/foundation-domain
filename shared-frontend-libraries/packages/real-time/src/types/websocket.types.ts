export interface WebSocketMessage {
  type: string;
  payload?: any;
  timestamp?: number;
}

export type WebSocketStatus = 'connecting' | 'connected' | 'disconnected' | 'error';
export type ReadyState = number;

export interface WebSocketConfig {
  url: string;
  protocols?: string[];
}

export interface WebSocketClient {
  url: string;
  status: WebSocketStatus;
  connect: () => void;
  disconnect: () => void;
  send: (message: WebSocketMessage) => void;
  onMessage: (message: WebSocketMessage) => void;
  onClose: () => void;
  onError: (error: Error) => void;
}
