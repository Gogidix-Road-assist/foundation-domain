import axios from 'axios'

// API Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Service Interfaces
export interface ServiceMetrics {
  uptime: number
  requestCount: number
  errorRate: number
  avgResponseTime: number
  cpuUsage: number
  memoryUsage: number
}

export interface ServiceHealth {
  id: string
  name: string
  category: string
  description: string
  status: 'healthy' | 'degraded' | 'down'
  port: number
  metrics: ServiceMetrics
  lastHealthCheck: string
}

// API Functions
export const dashboardApi = {
  // Get all services health status
  async getAllServices(): Promise<ServiceHealth[]> {
    const response = await api.get('/api/services/health')
    return response.data
  },

  // Get specific service health
  async getServiceHealth(serviceId: string): Promise<ServiceHealth> {
    const response = await api.get(`/api/services/${serviceId}/health`)
    return response.data
  },

  // Get service metrics
  async getServiceMetrics(serviceId: string): Promise<ServiceMetrics> {
    const response = await api.get(`/api/services/${serviceId}/metrics`)
    return response.data
  },

  // Get overall dashboard stats
  async getDashboardStats(): Promise<{
    total: number
    healthy: number
    degraded: number
    down: number
    totalRequests: number
    avgResponseTime: number
  }> {
    const response = await api.get('/api/dashboard/stats')
    return response.data
  },

  // Get time series metrics
  async getTimeSeriesMetrics(
    metric: 'requests' | 'errors' | 'responseTime',
    hours: number = 24
  ): Promise<Array<{ time: string; value: number }>> {
    const response = await api.get(`/api/metrics/timeseries?metric=${metric}&hours=${hours}`)
    return response.data
  },

  // Get alerts
  async getAlerts(): Promise<Array<{
    id: string
    type: 'critical' | 'warning' | 'info'
    service: string
    message: string
    timestamp: string
  }>> {
    const response = await api.get('/api/alerts')
    return response.data
  },

  // Acknowledge alert
  async acknowledgeAlert(alertId: string): Promise<void> {
    await api.post(`/api/alerts/${alertId}/acknowledge`)
  },

  // Resolve alert
  async resolveAlert(alertId: string): Promise<void> {
    await api.post(`/api/alerts/${alertId}/resolve`)
  },

  // Refresh all services
  async refreshServices(): Promise<void> {
    await api.post('/api/services/refresh')
  },
}

// WebSocket connection for real-time updates
export class ServicesWebSocket {
  private ws: WebSocket | null = null
  private reconnectTimeout: NodeJS.Timeout | null = null
  private messageHandlers: ((data: any) => void)[] = []

  connect() {
    try {
      this.ws = new WebSocket(WS_URL)

      this.ws.onopen = () => {
        console.log('WebSocket connected')
      }

      this.ws.onmessage = (event) => {
        const data = JSON.parse(event.data)
        this.messageHandlers.forEach(handler => handler(data))
      }

      this.ws.onclose = () => {
        console.log('WebSocket disconnected, reconnecting...')
        this.reconnectTimeout = setTimeout(() => this.connect(), 5000)
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error)
      }
    } catch (error) {
      console.error('Failed to connect WebSocket:', error)
      this.reconnectTimeout = setTimeout(() => this.connect(), 5000)
    }
  }

  disconnect() {
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout)
    }
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  onMessage(handler: (data: any) => void) {
    this.messageHandlers.push(handler)
  }

  offMessage(handler: (data: any) => void) {
    this.messageHandlers = this.messageHandlers.filter(h => h !== handler)
  }
}

export const wsClient = new ServicesWebSocket()
