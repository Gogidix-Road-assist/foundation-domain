import { create } from 'zustand';

const useDashboardStore = create((set) => ({
  metrics: {},
  services: [],
  alerts: [],
  isConnected: false,

  setMetrics: (metrics) => set({ metrics }),
  updateMetric: (key, value) => set((state) => ({
    metrics: { ...state.metrics, [key]: value }
  })),

  setServices: (services) => set({ services }),
  updateService: (serviceId, status) => set((state) => ({
    services: state.services.map(s =>
      s.id === serviceId ? { ...s, status, lastUpdated: new Date().toISOString() } : s
    )
  })),

  setAlerts: (alerts) => set({ alerts }),
  addAlert: (alert) => set((state) => ({
    alerts: [alert, ...state.alerts].slice(0, 50)
  })),

  setConnected: (isConnected) => set({ isConnected }),

  initializeSampleData: () => set({
    metrics: {
      totalRequests: 125432,
      successRate: 99.8,
      avgResponseTime: 45,
      activeServices: 83,
    },
    services: [
      { id: '1', name: 'API Gateway', status: 'HEALTHY', uptime: '99.9%', requests: 45231 },
      { id: '2', name: 'Auth Service', status: 'HEALTHY', uptime: '99.8%', requests: 12543 },
      { id: '3', name: 'Config Service', status: 'HEALTHY', uptime: '100%', requests: 3245 },
      { id: '4', name: 'Discovery Service', status: 'WARNING', uptime: '98.5%', requests: 8765 },
      { id: '5', name: 'Metrics Service', status: 'HEALTHY', uptime: '99.7%', requests: 23456 },
    ],
    alerts: [
      { id: '1', type: 'WARNING', message: 'High response time detected', time: '2 min ago' },
      { id: '2', type: 'INFO', message: 'Service deployment completed', time: '15 min ago' },
      { id: '3', type: 'SUCCESS', message: 'Health check passed', time: '1 hour ago' },
    ],
  }),
}));

export default useDashboardStore;
