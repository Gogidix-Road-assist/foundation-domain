import { create } from 'zustand';

export const useStore = create((set, get) => ({
  // Auth state
  isAuthenticated: false,
  user: null,
  token: null,

  // Dashboard state
  currentDashboard: null,
  dashboards: [],
  widgets: [],
  isDarkMode: false,

  // Real-time state
  isConnected: false,
  services: [],
  alerts: [],
  metrics: {},

  // Auth actions
  login: (user, token) => set({ isAuthenticated: true, user, token }),
  logout: () => set({ isAuthenticated: false, user: null, token: null, currentDashboard: null }),

  // Dashboard actions
  setCurrentDashboard: (dashboard) => set({ currentDashboard: dashboard }),
  setDashboards: (dashboards) => set({ dashboards }),
  addDashboard: (dashboard) => set((state) => ({ dashboards: [...state.dashboards, dashboard] })),
  updateDashboard: (id, updates) => set((state) => ({
    dashboards: state.dashboards.map(d => d.id === id ? { ...d, ...updates } : d)
  })),
  deleteDashboard: (id) => set((state) => ({
    dashboards: state.dashboards.filter(d => d.id !== id)
  })),

  // Widget actions
  setWidgets: (widgets) => set({ widgets }),
  addWidget: (widget) => set((state) => ({ widgets: [...state.widgets, widget] })),
  updateWidget: (id, updates) => set((state) => ({
    widgets: state.widgets.map(w => w.id === id ? { ...w, ...updates } : w)
  })),
  removeWidget: (id) => set((state) => ({
    widgets: state.widgets.filter(w => w.id !== id)
  })),

  // Theme actions
  toggleDarkMode: () => set((state) => ({ isDarkMode: !state.isDarkMode })),
  setDarkMode: (isDark) => set({ isDarkMode: isDark }),

  // Real-time actions
  setConnected: (isConnected) => set({ isConnected }),
  setServices: (services) => set({ services }),
  updateService: (serviceId, status) => set((state) => ({
    services: state.services.map(s =>
      s.id === serviceId ? { ...s, status, lastUpdated: new Date().toISOString() } : s
    )
  })),
  setAlerts: (alerts) => set({ alerts }),
  addAlert: (alert) => set((state) => ({ alerts: [alert, ...state.alerts].slice(0, 50) })),
  removeAlert: (alertId) => set((state) => ({
    alerts: state.alerts.filter(a => a.id !== alertId)
  })),
  setMetrics: (metrics) => set({ metrics }),
  updateMetric: (key, value) => set((state) => ({
    metrics: { ...state.metrics, [key]: value }
  })),
}));
