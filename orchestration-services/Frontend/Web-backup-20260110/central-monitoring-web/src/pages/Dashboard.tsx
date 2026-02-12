/**
 * Main Dashboard Page
 * Central Monitoring Dashboard - Gogidix Road Assist
 */

import { useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMonitoringStore } from '../services/store';
import wsService from '../services/websocket';
import apiService from '../services/api';
import StatsWidget from '../components/StatsWidget';
import LiveMap from '../components/LiveMap';
import RequestList from '../components/RequestList';
import PartnerStatus from '../components/PartnerStatus';
import AlertPanel from '../components/AlertPanel';

const Dashboard = () => {
  const navigate = useNavigate();
  const {
    stats,
    partners,
    activeRequests,
    alerts,
    alertsCount,
    statsLoading,
    partnersLoading,
    fetchDashboardStats,
    fetchPartners,
    fetchRequests,
    fetchAlerts,
    updateStats,
    updatePartners,
    addAlert,
    viewMode,
    setViewMode
  } = useMonitoringStore();

  // Initialize data and WebSocket connection
  useEffect(() => {
    // Fetch initial data
    fetchDashboardStats();
    fetchPartners();
    fetchRequests();
    fetchAlerts();

    // Connect to WebSocket
    wsService.connect();

    // Subscribe to real-time updates
    const unsubStats = wsService.onDashboardStats((newStats) => {
      updateStats(newStats);
    });

    const unsubPartners = wsService.onPartnerUpdate((partner) => {
      // Update partner in the list
      updatePartners((prevPartners) => {
        const index = prevPartners.findIndex(p => p.driverId === partner.driverId);
        if (index >= 0) {
          const newPartners = [...prevPartners];
          newPartners[index] = partner;
          return newPartners;
        }
        return prevPartners;
      });
    });

    const unsubAlerts = wsService.onAlert((alert) => {
      addAlert(alert);
    });

    // Cleanup on unmount
    return () => {
      unsubStats();
      unsubPartners();
      unsubAlerts();
      wsService.disconnect();
    };
  }, [fetchDashboardStats, fetchPartners, fetchRequests, fetchAlerts, updateStats, updatePartners, addAlert]);

  const handleViewRequest = useCallback((requestId: string) => {
    navigate(`/requests/${requestId}`);
  }, [navigate]);

  const handleAssignPartner = useCallback((requestId: string, partnerId: string) => {
    // Navigate to dispatch hub
    navigate(`/dispatch?requestId=${requestId}&partnerId=${partnerId}`);
  }, [navigate]);

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <h1 className="text-2xl font-bold text-gray-900">
              Central Monitoring Dashboard
            </h1>
            <span className="px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded-full">
              Live
            </span>
          </div>
          <div className="flex items-center gap-4">
            <button
              onClick={() => navigate('/alerts')}
              className="relative p-2 text-gray-600 hover:bg-gray-100 rounded-full"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
              {alertsCount > 0 && (
                <span className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                  {alertsCount > 9 ? '9+' : alertsCount}
                </span>
              )}
            </button>
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center text-white text-sm font-medium">
                OM
              </div>
              <span className="text-sm font-medium text-gray-700">Operations Manager</span>
            </div>
          </div>
        </div>
      </header>

      {/* Navigation */}
      <nav className="bg-white border-b border-gray-200 px-6">
        <div className="flex items-center gap-6">
          <button
            onClick={() => navigate('/dashboard')}
            className="px-4 py-3 text-sm font-medium text-blue-600 border-b-2 border-blue-600"
          >
            Dashboard
          </button>
          <button
            onClick={() => navigate('/live-map')}
            className="px-4 py-3 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            Live Map
          </button>
          <button
            onClick={() => navigate('/partners')}
            className="px-4 py-3 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            Partners
          </button>
          <button
            onClick={() => navigate('/requests')}
            className="px-4 py-3 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            Requests
          </button>
          <button
            onClick={() => navigate('/dispatch')}
            className="px-4 py-3 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            Dispatch
          </button>
          <button
            onClick={() => navigate('/reports')}
            className="px-4 py-3 text-sm font-medium text-gray-600 hover:text-gray-900"
          >
            Reports
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="p-6">
        {/* Stats Widgets */}
        <div className="grid grid-cols-4 gap-4 mb-6">
          <StatsWidget
            title="Active Requests"
            value={stats?.totalUsersWaiting ?? 0}
            subtitle="Users waiting for service"
            color="blue"
            loading={statsLoading}
          />
          <StatsWidget
            title="Partners Available"
            value={stats?.totalPartnersAvailable ?? 0}
            subtitle={`On job: ${stats?.totalPartnersOnJob ?? 0}`}
            color="green"
            loading={statsLoading}
          />
          <StatsWidget
            title="Avg Response Time"
            value={`${stats?.regionalStats ? Object.values(stats.regionalStats).reduce((acc, r) => acc + r.averageResponseTime, 0) / Object.values(stats.regionalStats).length : 0}`}
            subtitle="Minutes"
            color="amber"
            loading={statsLoading}
          />
          <StatsWidget
            title="SLA Achievement"
            value="96.5%"
            subtitle="Last 24 hours"
            color="purple"
            loading={statsLoading}
          />
        </div>

        {/* Main Grid */}
        <div className={`grid ${viewMode === 'split' ? 'grid-cols-2' : 'grid-cols-1'} gap-6`}>
          {/* Map Section */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
            <div className="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
              <h2 className="font-semibold text-gray-900">Live Map</h2>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setViewMode('map')}
                  className={`px-3 py-1 text-xs font-medium rounded ${
                    viewMode === 'map' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
                  }`}
                >
                  Full
                </button>
                <button
                  onClick={() => setViewMode('split')}
                  className={`px-3 py-1 text-xs font-medium rounded ${
                    viewMode === 'split' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
                  }`}
                >
                  Split
                </button>
              </div>
            </div>
            <LiveMap partners={partners} requests={activeRequests} loading={partnersLoading} />
          </div>

          {/* Side Panel */}
          <div className="space-y-6">
            {/* Active Requests */}
            <div className="bg-white rounded-lg shadow-sm border border-gray-200">
              <div className="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
                <h2 className="font-semibold text-gray-900">Active Requests</h2>
                <button
                  onClick={() => navigate('/requests')}
                  className="text-sm text-blue-600 hover:text-blue-700"
                >
                  View All
                </button>
              </div>
              <RequestList
                requests={activeRequests.slice(0, 5)}
                onViewRequest={handleViewRequest}
                onAssignPartner={handleAssignPartner}
              />
            </div>

            {/* Partner Status */}
            <div className="bg-white rounded-lg shadow-sm border border-gray-200">
              <div className="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
                <h2 className="font-semibold text-gray-900">Partner Status</h2>
                <button
                  onClick={() => navigate('/partners')}
                  className="text-sm text-blue-600 hover:text-blue-700"
                >
                  View All
                </button>
              </div>
              <PartnerStatus stats={stats?.partnerTypeStats} loading={statsLoading} />
            </div>

            {/* Alerts Panel */}
            {alerts.length > 0 && (
              <AlertPanel alerts={alerts.slice(0, 3)} />
            )}
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
