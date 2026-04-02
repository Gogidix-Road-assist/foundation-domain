import axios, { AxiosInstance, AxiosResponse } from 'axios';

// API Base Configuration
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8080/api';

// Create axios instances for different services
const apiClient: AxiosInstance = axios.create({
  baseURL: API_GATEWAY_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Central-Monitoring API Services
export const monitoringApi = {
  // Location Service
  location: {
    submitUpdate: (data: any) => apiClient.post('/monitoring/location/update', data),
    getActivePartners: () => apiClient.get('/monitoring/location/active-partners'),
    getPartnerLocation: (partnerId: string) => apiClient.get(`/monitoring/location/partner/${partnerId}`),
  },

  // Matching Service
  matching: {
    findPartners: (request: any) => apiClient.post('/monitoring/matching/find', request),
    getActiveRequests: () => apiClient.get('/monitoring/matching/active-requests'),
    getMatchingStatus: (requestId: string) => apiClient.get(`/monitoring/matching/status/${requestId}`),
  },

  // Dispatching Service
  dispatching: {
    assignPartner: (data: any) => apiClient.post('/monitoring/dispatching/assign', data),
    getPending: () => apiClient.get('/monitoring/dispatching/pending'),
    broadcastMessage: (data: any) => apiClient.post('/monitoring/dispatching/broadcast', data),
    getAssignment: (assignmentId: string) => apiClient.get(`/monitoring/dispatching/assignment/${assignmentId}`),
  },

  // Alerting Service
  alerting: {
    getAlerts: () => apiClient.get('/monitoring/alerting/alerts'),
    createAlert: (data: any) => apiClient.post('/monitoring/alerting/alerts', data),
    acknowledgeAlert: (alertId: string) => apiClient.patch(`/monitoring/alerting/alerts/${alertId}/acknowledge`),
    resolveAlert: (alertId: string) => apiClient.patch(`/monitoring/alerting/alerts/${alertId}/resolve`),
  },

  // Monitoring Service
  monitoring: {
    getStats: () => apiClient.get('/monitoring/monitoring/stats'),
    getPartnerStats: (partnerId: string) => apiClient.get(`/monitoring/monitoring/partner/${partnerId}`),
    getSystemHealth: () => apiClient.get('/monitoring/monitoring/health'),
  },

  // Reporting Service
  reporting: {
    getMetrics: (period: string) => apiClient.get(`/monitoring/reporting/metrics?period=${period}`),
    getTimeSeries: (period: string) => apiClient.get(`/monitoring/reporting/timeseries?period=${period}`),
    getPartnerPerformance: () => apiClient.get('/monitoring/reporting/partner-performance'),
    generateReport: (reportConfig: any) => apiClient.post('/monitoring/reporting/generate', reportConfig),
  },
};

// Vendors-Ecommerce API Services
export const ecommerceApi = {
  // Catalog Service
  catalog: {
    getProduct: (id: string) => apiClient.get(`/vendor/catalog/products/${id}`),
    getProducts: (params?: any) => apiClient.get('/vendor/catalog/products', { params }),
    getVendorProducts: (vendorId: string, page: number, size: number) =>
      apiClient.get(`/vendor/catalog/vendors/${vendorId}/products?page=${page}&size=${size}`),
    getCategoryProducts: (category: string, page: number = 0, size: number = 20) =>
      apiClient.get(`/vendor/catalog/categories/${category}/products?page=${page}&size=${size}`),
    getFeaturedProducts: () => apiClient.get('/vendor/catalog/products/featured'),
    getLowStockProducts: (vendorId: string) => apiClient.get(`/vendor/catalog/vendors/${vendorId}/products/low-stock`),
    getCategories: () => apiClient.get('/vendor/catalog/categories'),
    getRootCategories: () => apiClient.get('/vendor/catalog/categories/root'),
    getSubCategories: (parentId: number) => apiClient.get(`/vendor/catalog/categories/${parentId}/subcategories`),
  },

  // Search Service
  search: {
    searchProducts: (query: string, filters?: any) => apiClient.post('/vendor/search/products', { query, ...filters }),
    searchProductsGet: (params: any) => apiClient.get('/vendor/search/products', { params }),
    getCompatibleProducts: (vehicle: string, page: number = 0, size: number = 20) =>
      apiClient.get(`/vendor/search/products/compatible/${vehicle}?page=${page}&size=${size}`),
    getSuggestions: (query: string) => apiClient.get(`/vendor/search/suggestions?q=${query}`),
  },

  // Order Service
  orders: {
    getOrder: (id: string) => apiClient.get(`/vendor/orders/${id}`),
    getOrderByNumber: (orderNumber: string) => apiClient.get(`/vendor/orders/number/${orderNumber}`),
    getCustomerOrders: (customerId: string, page: number = 0, size: number = 20) =>
      apiClient.get(`/vendor/orders/customers/${customerId}?page=${page}&size=${size}`),
    getVendorOrders: (vendorId: string, page: number = 0, size: number = 20) =>
      apiClient.get(`/vendor/orders/vendors/${vendorId}?page=${page}&size=${size}`),
    getVendorPendingOrders: (vendorId: string) => apiClient.get(`/vendor/orders/vendors/${vendorId}/pending`),
    createOrder: (orderData: any) => apiClient.post('/vendor/orders', orderData),
    updateOrderStatus: (orderId: string, status: string, notes?: string) =>
      apiClient.patch(`/vendor/orders/${orderId}/status`, { status, notes }),
    updatePaymentStatus: (orderId: string, paymentStatus: string, paymentReference: string) =>
      apiClient.patch(`/vendor/orders/${orderId}/payment`, { paymentStatus, paymentReference }),
    addTrackingInfo: (orderId: string, trackingNumber: string) =>
      apiClient.patch(`/vendor/orders/${orderId}/tracking`, { trackingNumber }),
    cancelOrder: (orderId: string, reason: string) =>
      apiClient.post(`/vendor/orders/${orderId}/cancel`, { reason }),
    getVendorMetrics: (vendorId: string) => apiClient.get(`/vendor/orders/vendors/${vendorId}/metrics`),
  },

  // Cart Service
  cart: {
    getCustomerCart: (customerId: string) => apiClient.get(`/vendor/cart/customers/${customerId}`),
    getSessionCart: (sessionId: string) => apiClient.get(`/vendor/cart/sessions/${sessionId}`),
    createCart: (data: any) => apiClient.post('/vendor/cart', data),
    addItem: (cartId: string, itemData: any) => apiClient.post(`/vendor/cart/${cartId}/items`, itemData),
    updateItemQuantity: (cartId: string, itemId: string, quantity: number) =>
      apiClient.patch(`/vendor/cart/${cartId}/items/${itemId}`, { quantity }),
    removeItem: (cartId: string, itemId: string) => apiClient.delete(`/vendor/cart/${cartId}/items/${itemId}`),
    clearCart: (cartId: string) => apiClient.delete(`/vendor/cart/${cartId}/items`),
    applyCoupon: (cartId: string, couponCode: string) =>
      apiClient.post(`/vendor/cart/${cartId}/coupon`, { couponCode }),
    mergeCarts: (sessionCartId: string, customerId: string) =>
      apiClient.post('/vendor/cart/merge', { sessionCartId, customerId }),
  },

  // Delivery Optimizer Service
  delivery: {
    getOptions: (request: any) => apiClient.post('/vendor/delivery/options', request),
    getBestOption: (request: any) => apiClient.post('/vendor/delivery/options/best', request),
    bookDelivery: (request: any, channel: string) =>
      apiClient.post(`/vendor/delivery/book?channel=${channel}`, request),
  },

  // Vendor Service
  vendors: {
    getVendor: (id: string) => apiClient.get(`/vendor/vendors/${id}`),
    getVendorByEmail: (email: string) => apiClient.get(`/vendor/vendors/email/${email}`),
    getVendorsByStatus: (status: string, page: number = 0, size: number = 20) =>
      apiClient.get(`/vendor/vendors/status/${status}?page=${page}&size=${size}`),
    registerVendor: (vendorData: any) => apiClient.post('/vendor/vendors/register', vendorData),
    updateVendor: (id: string, vendorData: any) => apiClient.put(`/vendor/vendors/${id}`, vendorData),
    approveVendor: (id: string) => apiClient.patch(`/vendor/vendors/${id}/approve`),
    suspendVendor: (id: string, reason: string) =>
      apiClient.patch(`/vendor/vendors/${id}/suspend`, { reason }),
    updateMetrics: (id: string, metrics: any) => apiClient.patch(`/vendor/vendors/${id}/metrics`, metrics),
    getAnalytics: (id: string) => apiClient.get(`/vendor/vendors/${id}/analytics`),
  },
};

// Export the default API client for direct use
export default apiClient;

// Helper function for direct axios calls
export const api = {
  get: <T = any>(url: string, config?: any) => apiClient.get<T, AxiosResponse<T>>(url, config),
  post: <T = any>(url: string, data?: any, config?: any) => apiClient.post<T, AxiosResponse<T>>(url, data, config),
  put: <T = any>(url: string, data?: any, config?: any) => apiClient.put<T, AxiosResponse<T>>(url, data, config),
  patch: <T = any>(url: string, data?: any, config?: any) => apiClient.patch<T, AxiosResponse<T>>(url, data, config),
  delete: <T = any>(url: string, config?: any) => apiClient.delete<T, AxiosResponse<T>>(url, config),
};
