export interface ReconnectionStrategy {
  reconnect(): Promise<boolean>;
}

export class ExponentialBackoffStrategy implements ReconnectionStrategy {
  private attempt = 0;
  private maxAttempts = 5;
  private baseDelay = 1000;
  private maxDelay = 30000;

  async reconnect(): Promise<boolean> {
    if (this.attempt >= this.maxAttempts) {
      return false;
    }

    const delay = Math.min(this.baseDelay * Math.pow(2, this.attempt), this.maxDelay);

    await new Promise((resolve) => {
      setTimeout(() => {
        this.attempt++;
        resolve(true);
      }, delay);
    });

    return this.attempt < this.maxAttempts;
  }

  reset(): void {
    this.attempt = 0;
  }
}

export class FixedDelayStrategy implements ReconnectionStrategy {
  private delay: number;

  constructor(delay: number = 3000) {
    this.delay = delay;
  }

  async reconnect(): Promise<boolean> {
    await new Promise((resolve) => {
      setTimeout(() => {
        resolve(true);
      }, this.delay);
    });

    return true;
  }
}

export const useReconnectionStrategy = (type: 'exponential' | 'fixed' = 'exponential') => {
  return type === 'exponential'
    ? new ExponentialBackoffStrategy()
    : new FixedDelayStrategy();
};
