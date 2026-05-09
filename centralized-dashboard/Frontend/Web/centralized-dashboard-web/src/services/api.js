import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  logout: () => api.post('/auth/logout'),
  me: () => api.get('/auth/me'),
};

// Dashboard API
export const dashboardAPI = {
  getAll: () => api.get('/dashboards'),
  getById: (id) => api.get(`/dashboards/${id}`),
  create: (data) => api.post('/dashboards', data),
  update: (id, data) => api.put(`/dashboards/${id}`, data),
  delete: (id) => api.delete(`/dashboards/${id}`),
  clone: (id) => api.post(`/dashboards/${id}/clone`),
};

// Widget API
export const widgetAPI = {
  getAll: (dashboardId) => api.get(`/dashboards/${dashboardId}/widgets`),
  create: (dashboardId, data) => api.post(`/dashboards/${dashboardId}/widgets`, data),
  update: (dashboardId, widgetId, data) => api.put(`/dashboards/${dashboardId}/widgets/${widgetId}`, data),
  delete: (dashboardId, widgetId) => api.delete(`/dashboards/${dashboardId}/widgets/${widgetId}`),
};

// Analytics API
export const analyticsAPI = {
  getDashboardStats: (dashboardId) => api.get(`/analytics/dashboard/${dashboardId}`),
  getUsageReport: (dashboardId, period) => api.get(`/analytics/dashboard/${dashboardId}/usage?period=${period}`),
  getPerformanceMetrics: (dashboardId) => api.get(`/analytics/dashboard/${dashboardId}/performance`),
  trackEvent: (event) => api.post('/analytics/events', event),
};

// Reports API
export const reportsAPI = {
  getAll: () => api.get('/reports'),
  getById: (id) => api.get(`/reports/${id}`),
  create: (data) => api.post('/reports', data),
  update: (id, data) => api.put(`/reports/${id}`, data),
  delete: (id) => api.delete(`/reports/${id}`),
  execute: (id) => api.post(`/reports/${id}/execute`),
  download: (id, format) => api.get(`/reports/${id}/download?format=${format}`, { responseType: 'blob' }),
};

// Services API
export const servicesAPI = {
  getAll: () => api.get('/services'),
  getStatus: (serviceId) => api.get(`/services/${serviceId}/status`),
  getMetrics: (serviceId) => api.get(`/services/${serviceId}/metrics`),
};

// Alerts API
export const alertsAPI = {
  getAll: () => api.get('/alerts'),
  acknowledge: (alertId) => api.post(`/alerts/${alertId}/acknowledge`),
  dismiss: (alertId) => api.delete(`/alerts/${alertId}`),
};

export default api;
