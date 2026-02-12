import { create } from 'zustand';
import { io } from 'socket.io-client';

const useWebSocketStore = create((set, get) => ({
  socket: null,
  connected: false,
  metrics: {},
  alerts: [],
  serviceHealth: {},

  connect: () => {
    const token = get().token || localStorage.getItem('auth_token');
    if (!token) return;

    const socket = io('http://localhost:3000', {
      auth: { token },
      transports: ['websocket'],
    });

    socket.on('connect', () => {
      console.log('WebSocket connected');
      set({ connected: true, socket });
    });

    socket.on('disconnect', () => {
      console.log('WebSocket disconnected');
      set({ connected: false });
    });

    socket.on('metrics:update', (data) => {
      set((state) => ({
        metrics: { ...state.metrics, ...data },
      }));
    });

    socket.on('alert:new', (alert) => {
      set((state) => ({
        alerts: [alert, ...state.alerts].slice(0, 50),
      }));
    });

    socket.on('service:health:update', (data) => {
      set((state) => ({
        serviceHealth: { ...state.serviceHealth, ...data },
      }));
    });

    set({ socket });
  },

  disconnect: () => {
    const { socket } = get();
    if (socket) {
      socket.disconnect();
      set({ socket: null, connected: false });
    }
  },

  addAlert: (alert) => {
    set((state) => ({
      alerts: [alert, ...state.alerts].slice(0, 50),
    }));
  },

  removeAlert: (alertId) => {
    set((state) => ({
      alerts: state.alerts.filter((a) => a.id !== alertId),
    }));
  },

  clearAlerts: () => {
    set({ alerts: [] });
  },
}));

export default useWebSocketStore;
