import { create } from 'zustand';

const useDashboardStore = create((set) => ({
  dashboards: [],
  activeDashboardId: null,
  widgets: [],
  isLoading: false,
  error: null,

  setActiveDashboard: (dashboardId) => set({ activeDashboardId: dashboardId }),

  addWidget: (widget) => set((state) => ({
    widgets: [...state.widgets, widget],
  })),

  removeWidget: (widgetId) => set((state) => ({
    widgets: state.widgets.filter((w) => w.id !== widgetId),
  })),

  updateWidget: (widgetId, updates) => set((state) => ({
    widgets: state.widgets.map((w) =>
      w.id === widgetId ? { ...w, ...updates } : w
    ),
  })),

  setLoading: (loading) => set({ isLoading: loading }),

  setError: (error) => set({ error }),

  // Sample data for development
  initializeSampleData: () => set({
    dashboards: [
      {
        id: '1',
        name: 'Main Dashboard',
        description: 'Platform overview',
        layout: 'GRID',
        widgets: ['metric-1', 'metric-2', 'chart-1', 'table-1'],
      },
      {
        id: '2',
        name: 'Service Health',
        description: 'System health monitoring',
        layout: 'GRID',
        widgets: ['health-1', 'health-2', 'alerts-1'],
      },
    ],
    activeDashboardId: '1',
    widgets: [
      {
        id: 'metric-1',
        type: 'METRIC',
        title: 'Total Requests',
        value: '125,432',
        change: '+12.5%',
        positive: true,
      },
      {
        id: 'metric-2',
        type: 'METRIC',
        title: 'Success Rate',
        value: '99.8%',
        change: '+0.3%',
        positive: true,
      },
      {
        id: 'chart-1',
        type: 'LINE_CHART',
        title: 'Request Volume Over Time',
        data: [
          { time: '00:00', value: 1200 },
          { time: '04:00', value: 800 },
          { time: '08:00', value: 2100 },
          { time: '12:00', value: 3500 },
          { time: '16:00', value: 2800 },
          { time: '20:00', value: 1900 },
        ],
      },
      {
        id: 'table-1',
        type: 'SERVICE_TABLE',
        title: 'Service Status',
        data: [
          { name: 'API Gateway', status: 'UP', requests: 45000, errors: 12 },
          { name: 'Auth Service', status: 'UP', requests: 32000, errors: 5 },
          { name: 'Config Service', status: 'UP', requests: 18000, errors: 2 },
          { name: 'Metrics Service', status: 'UP', requests: 28000, errors: 8 },
          { name: 'Notification Service', status: 'WARNING', requests: 15000, errors: 45 },
        ],
      },
      {
        id: 'health-1',
        type: 'DONUT_CHART',
        title: 'Service Health Distribution',
        data: [
          { name: 'UP', value: 38, color: '#52c41a' },
          { name: 'WARNING', value: 2, color: '#faad14' },
          { name: 'DOWN', value: 0, color: '#ff4d4f' },
        ],
      },
      {
        id: 'health-2',
        type: 'BAR_CHART',
        title: 'Response Times by Service',
        data: [
          { name: 'API Gateway', value: 45 },
          { name: 'Auth Service', value: 32 },
          { name: 'Config Service', value: 28 },
          { name: 'Metrics Service', value: 55 },
          { name: 'Notification Service', value: 78 },
        ],
      },
      {
        id: 'alerts-1',
        type: 'ALERT_LIST',
        title: 'Recent Alerts',
        data: [
          {
            id: '1',
            severity: 'ERROR',
            service: 'Notification Service',
            message: 'High error rate detected',
            timestamp: new Date(Date.now() - 300000).toISOString(),
          },
          {
            id: '2',
            severity: 'WARNING',
            service: 'Config Service',
            message: 'Cache hit rate below threshold',
            timestamp: new Date(Date.now() - 900000).toISOString(),
          },
          {
            id: '3',
            severity: 'INFO',
            service: 'API Gateway',
            message: 'Scheduled maintenance completed',
            timestamp: new Date(Date.now() - 3600000).toISOString(),
          },
        ],
      },
    ],
  })),
}));

export default useDashboardStore;
