import axios from 'axios';
import Constants from 'expo-constants';

const API_BASE_URL = Constants.expoConfig?.extra?.apiUrl || 'http://localhost:3000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

let token = null;

export const setAuthToken = (newToken) => {
  token = newToken;
};

api.interceptors.request.use(
  (config) => {
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      token = null;
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  logout: () => api.post('/auth/logout'),
  me: () => api.get('/auth/me'),
};

export const dashboardAPI = {
  getAll: () => api.get('/dashboards'),
  getById: (id) => api.get(`/dashboards/${id}`),
};

export const metricsAPI = {
  getMetrics: (params) => api.get('/metrics', { params }),
};

export const servicesAPI = {
  getAll: () => api.get('/services'),
  getStatus: (serviceId) => api.get(`/services/${serviceId}/status`),
};

export const alertsAPI = {
  getAll: () => api.get('/alerts'),
  acknowledge: (alertId) => api.post(`/alerts/${alertId}/acknowledge`),
};

export default api;
