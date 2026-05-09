import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { dashboardAPI, servicesAPI } from '../services/api';
import { wsService } from '../services/websocket';
import MetricCard from '../components/MetricCard';
import ServiceStatusCard from '../components/ServiceStatusCard';
import ActivityFeed from '../components/ActivityFeed';
import AlertsPanel from '../components/AlertsPanel';

function Dashboard() {
  const navigate = useNavigate();
  const {
    currentDashboard,
    dashboards,
    user,
    isAuthenticated,
    isDarkMode,
    services,
    alerts,
    metrics,
    setCurrentDashboard,
    setDashboards,
    setServices,
    setAlerts,
    logout,
  } = useStore();

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    loadDashboardData();
    connectWebSocket();

    return () => {
      wsService.disconnect();
    };
  }, [isAuthenticated, navigate]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);

      // Load dashboards
      const dashboardsRes = await dashboardAPI.getAll();
      setDashboards(dashboardsRes.data);

      // Set first dashboard as current if none selected
      if (dashboardsRes.data.length > 0 && !currentDashboard) {
        setCurrentDashboard(dashboardsRes.data[0]);
      }

      // Load services
      const servicesRes = await servicesAPI.getAll();
      setServices(servicesRes.data);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const connectWebSocket = () => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      wsService.connect(token);

      if (currentDashboard) {
        wsService.joinDashboard(currentDashboard.id);
      }
    }
  };

  const handleDashboardChange = (dashboard) => {
    setCurrentDashboard(dashboard);
    wsService.joinDashboard(dashboard.id);
  };

  const handleLogout = () => {
    wsService.disconnect();
    logout();
    localStorage.removeItem('auth_token');
    navigate('/login');
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className={`dashboard-layout ${isDarkMode ? 'dark' : ''}`}>
      <aside className="sidebar">
        <div className="sidebar-header">
          <h2>📊 Centralized Dashboard</h2>
          <p className="sidebar-subtitle">Rapid Assist Platform</p>
          {user && <p className="user-info">Welcome, {user.name}</p>}
        </div>

        <nav className="sidebar-nav">
          <Link to="/dashboard" className="nav-link active">
            🏠 Dashboard
          </Link>
          <Link to="/dashboards/configure" className="nav-link">
            ⚙️ Configure Dashboards
          </Link>
          <Link to="/analytics" className="nav-link">
            📈 Analytics
          </Link>
          <Link to="/reports" className="nav-link">
            📄 Reports
          </Link>
          <Link to="/settings" className="nav-link">
            🔧 Settings
          </Link>
        </nav>

        <div className="sidebar-footer">
          <div className="connection-status">
            <span className={`status-dot ${wsService.isConnected() ? 'online' : 'offline'}`}></span>
            {wsService.isConnected() ? 'Connected' : 'Disconnected'}
          </div>
          <button onClick={handleLogout} className="btn btn-logout">
            Sign Out
          </button>
        </div>
      </aside>

      <main className="main-content">
        <header className="page-header">
          <div>
            <h1>{currentDashboard?.name || 'Main Dashboard'}</h1>
            <p className="subtitle">
              {currentDashboard?.description || 'Platform overview and monitoring'}
            </p>
          </div>
          <div className="header-actions">
            <select
              value={currentDashboard?.id || ''}
              onChange={(e) => {
                const dashboard = dashboards.find(d => d.id === e.target.value);
                if (dashboard) handleDashboardChange(dashboard);
              }}
              className="dashboard-selector"
            >
              {dashboards.map((dash) => (
                <option key={dash.id} value={dash.id}>
                  {dash.name}
                </option>
              ))}
            </select>
            <button
              onClick={() => useStore.getState().toggleDarkMode()}
              className="btn btn-secondary"
            >
              {isDarkMode ? '☀️ Light' : '🌙 Dark'}
            </button>
          </div>
        </header>

        {/* Alerts Panel */}
        {alerts.length > 0 && <AlertsPanel />}

        {/* Metrics Grid */}
        <div className="metrics-grid">
          <MetricCard
            title="Total Requests"
            value={metrics.totalRequests || 125430}
            change="+12.5%"
            trend="up"
            icon="📊"
          />
          <MetricCard
            title="Active Users"
            value={metrics.activeUsers || 3248}
            change="+8.2%"
            trend="up"
            icon="👥"
          />
          <MetricCard
            title="Success Rate"
            value={`${metrics.successRate || 99.8}%`}
            change="+0.2%"
            trend="up"
            icon="✅"
          />
          <MetricCard
            title="Avg Response Time"
            value={`${metrics.avgResponseTime || 145}ms`}
            change="-15ms"
            trend="down"
            icon="⚡"
          />
        </div>

        {/* Services Status */}
        <section className="services-section">
          <h2>Service Status</h2>
          <div className="services-grid">
            {services.map((service) => (
              <ServiceStatusCard key={service.id} service={service} />
            ))}
          </div>
        </section>

        {/* Activity Feed */}
        <section className="activity-section">
          <ActivityFeed />
        </section>
      </main>
    </div>
  );
}

export default Dashboard;
