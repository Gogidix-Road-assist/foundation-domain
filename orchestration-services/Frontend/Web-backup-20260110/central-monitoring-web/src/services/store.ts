/**
 * Global State Store using Zustand
 */

import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import type { DashboardStats, PartnerLocation, ServiceRequest, Alert } from '../types';
import apiService from './api';

interface MonitoringState {
  // Dashboard stats
  stats: DashboardStats | null;
  statsLoading: boolean;
  statsError: string | null;

  // Partner locations
  partners: PartnerLocation[];
  partnersLoading: boolean;

  // Active requests
  activeRequests: ServiceRequest[];
  requestsLoading: boolean;

  // Alerts
  alerts: Alert[];
  alertsCount: number;
  alertsLoading: boolean;

  // UI state
  selectedRegion: string | null;
  viewMode: 'map' | 'list' | 'split';
  sidebarOpen: boolean;

  // Actions
  fetchDashboardStats: () => Promise<void>;
  fetchPartners: () => Promise<void>;
  fetchRequests: () => Promise<void>;
  fetchAlerts: () => Promise<void>;
  updateStats: (stats: DashboardStats) => void;
  updatePartners: (partners: PartnerLocation[]) => void;
  updateRequests: (requests: ServiceRequest[]) => void;
  addAlert: (alert: Alert) => void;
  acknowledgeAlert: (alertId: string) => void;
  setSelectedRegion: (region: string | null) => void;
  setViewMode: (mode: 'map' | 'list' | 'split') => void;
  toggleSidebar: () => void;
}

export const useMonitoringStore = create<MonitoringState>()(
  devtools(
    persist(
      (set, get) => ({
        // Initial state
        stats: null,
        statsLoading: false,
        statsError: null,

        partners: [],
        partnersLoading: false,

        activeRequests: [],
        requestsLoading: false,

        alerts: [],
        alertsCount: 0,
        alertsLoading: false,

        selectedRegion: null,
        viewMode: 'split',
        sidebarOpen: true,

        // Actions
        fetchDashboardStats: async () => {
          set({ statsLoading: true, statsError: null });
          try {
            const stats = await apiService.getDashboardStats();
            set({ stats, statsLoading: false });
          } catch (error) {
            set({
              statsError: error instanceof Error ? error.message : 'Failed to fetch stats',
              statsLoading: false
            });
          }
        },

        fetchPartners: async () => {
          set({ partnersLoading: true });
          try {
            const partners = await apiService.getPartnerLocations();
            set({ partners, partnersLoading: false });
          } catch (error) {
            console.error('Failed to fetch partners:', error);
            set({ partnersLoading: false });
          }
        },

        fetchRequests: async () => {
          set({ requestsLoading: true });
          try {
            const requests = await apiService.getActiveRequests();
            set({ activeRequests: requests, requestsLoading: false });
          } catch (error) {
            console.error('Failed to fetch requests:', error);
            set({ requestsLoading: false });
          }
        },

        fetchAlerts: async () => {
          set({ alertsLoading: true });
          try {
            const alerts = await apiService.getAlerts();
            set({
              alerts,
              alertsCount: alerts.filter(a => !a.acknowledged).length,
              alertsLoading: false
            });
          } catch (error) {
            console.error('Failed to fetch alerts:', error);
            set({ alertsLoading: false });
          }
        },

        updateStats: (stats) => set({ stats }),

        updatePartners: (partners) => set({ partners }),

        updateRequests: (requests) => set({ activeRequests: requests }),

        addAlert: (alert) => set((state) => ({
          alerts: [alert, ...state.alerts],
          alertsCount: state.alertsCount + 1
        })),

        acknowledgeAlert: async (alertId) => {
          await apiService.acknowledgeAlert(alertId);
          set((state) => ({
            alerts: state.alerts.map(a =>
              a.alertId === alertId ? { ...a, acknowledged: true } : a
            ),
            alertsCount: Math.max(0, state.alertsCount - 1)
          }));
        },

        setSelectedRegion: (region) => set({ selectedRegion: region }),

        setViewMode: (mode) => set({ viewMode: mode }),

        toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen }))
      }),
      {
        name: 'central-monitoring-storage',
        partialize: (state) => ({
          viewMode: state.viewMode,
          sidebarOpen: state.sidebarOpen,
          selectedRegion: state.selectedRegion
        })
      }
    )
  )
);
