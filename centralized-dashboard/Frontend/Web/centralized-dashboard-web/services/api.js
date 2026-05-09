import axios from 'axios';

const api = axios.create({
  baseURL: process.env.VITE_API_URL || 'http://localhost:3000/api',
  timeout: 10000,
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
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear token and redirect to login
      localStorage.removeItem('auth_token');
      localStorage.removeItem('auth_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },
  logout: async () => {
    const response = await api.post('/auth/logout');
    return response.data;
  },
  verify: async () => {
    const response = await api.get('/auth/verify');
    return response.data;
  },
};

// Dashboard API
export const dashboardAPI = {
  getDashboards: async () => {
    const response = await api.get('/dashboards');
    return response.data;
  },
  getDashboard: async (id) => {
    const response = await api.get(`/dashboards/${id}`);
    return response.data;
  },
  createDashboard: async (data) => {
    const response = await api.post('/dashboards', data);
    return response.data;
  },
  updateDashboard: async (id, data) => {
    const response = await api.put(`/dashboards/${id}`, data);
    return response.data;
  },
  deleteDashboard: async (id) => {
    const response = await api.delete(`/dashboards/${id}`);
    return response.data;
  },
};

// Metrics API
export const metricsAPI = {
  getMetrics: async (params) => {
    const response = await api.get('/metrics', { params });
    return response.data;
  },
  getMetricHistory: async (metricId, params) => {
    const response = await api.get(`/metrics/${metricId}/history`, { params });
    return response.data;
  },
};

// Services API
export const servicesAPI = {
  getServices: async () => {
    const response = await api.get('/services');
    return response.data;
  },
  getService: async (serviceId) => {
    const response = await api.get(`/services/${serviceId}`);
    return response.data;
  },
  getServiceHealth: async (serviceId) => {
    const response = await api.get(`/services/${serviceId}/health`);
    return response.data;
  },
};

// Alerts API
export const alertsAPI = {
  getAlerts: async (params) => {
    const response = await api.get('/alerts', { params });
    return response.data;
  },
  acknowledgeAlert: async (alertId) => {
    const response = await api.post(`/alerts/${alertId}/acknowledge`);
    return response.data;
  },
};

// Reports API
export const reportsAPI = {
  getReports: async () => {
    const response = await api.get('/reports');
    return response.data;
  },
  generateReport: async (reportType, params) => {
    const response = await api.post(`/reports/${reportType}/generate`, params);
    return response.data;
  },
  downloadReport: async (reportId) => {
    const response = await api.get(`/reports/${reportId}/download`, {
      responseType: 'blob',
    });
    return response.data;
  },
};

export default api;
