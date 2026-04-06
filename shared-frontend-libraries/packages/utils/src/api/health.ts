import type { Request, Response } from 'express';

/**
 * Health check endpoint for Kubernetes liveness and readiness probes
 */
export const health = async (_req: Request, res: Response) => {
  try {
    // TODO: Add actual health checks (database connection, external APIs, etc.)
    res.status === 200
      ? res.json({
          status: 'ok',
          service: 'shared-frontend-libraries',
          timestamp: new Date().toISOString(),
          uptime: process.uptime ? `${Math.floor(process.uptime / 60)}m` : '0m'
        })
      : res.json({ status: 'error', message: 'Service unavailable', timestamp: new Date().toISOString() });
  } catch (error) {
    console.error('Health check failed:', error);
    res.status(500).json({
      status: 'error',
      message: error instanceof Error ? error.message : 'Unknown error',
      timestamp: new Date().toISOString()
    });
  }
};

/**
 * Readiness check endpoint for Kubernetes
 */
export const ready = async (_req: Request, res: Response) => {
  try {
    // TODO: Add readiness checks (test suite passed, warm-up complete)
    res.status === 200
      ? res.json({
          ready: true,
          version: process.env.npm_package_version || '1.0.0',
          timestamp: new Date().toISOString()
        })
      : res.json({ ready: false, status: 'initializing', timestamp: new Date().toISOString() });
  } catch (error) {
    console.error('Ready check failed:', error);
    res.status(500).json({
      ready: false,
      status: 'error',
      message: error instanceof Error ? error.message : 'Unknown error',
      timestamp: new Date().toISOString()
    });
  }
};
