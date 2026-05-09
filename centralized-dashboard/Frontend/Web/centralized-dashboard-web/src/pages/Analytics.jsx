import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { analyticsAPI } from '../services/api';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function Analytics() {
  const navigate = useNavigate();
  const { dashboards, currentDashboard, isAuthenticated } = useStore();
  const [period, setPeriod] = useState('7d');
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState(null);
  const [usageData, setUsageData] = useState([]);
  const [performanceData, setPerformanceData] = useState([]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (currentDashboard) {
      loadAnalytics();
    }
  }, [isAuthenticated, currentDashboard, period, navigate]);

  const loadAnalytics = async () => {
    try {
      setLoading(true);

      const [statsRes, usageRes, perfRes] = await Promise.all([
        analyticsAPI.getDashboardStats(currentDashboard.id),
        analyticsAPI.getUsageReport(currentDashboard.id, period),
        analyticsAPI.getPerformanceMetrics(currentDashboard.id),
      ]);

      setStats(statsRes.data);
      setUsageData(usageRes.data.dailyStats || []);
      setPerformanceData(perfRes.data.metrics || []);
    } catch (error) {
      console.error('Error loading analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = ['#1a5490', '#28a745', '#ffc107', '#dc3545', '#17a2b8'];

  // Sample data for demonstration
  const sampleUsageData = [
    { day: 'Mon', views: 420, users: 85, sessions: 320 },
    { day: 'Tue', views: 580, users: 120, sessions: 450 },
    { day: 'Wed', views: 520, users: 105, sessions: 410 },
    { day: 'Thu', views: 650, users: 140, sessions: 520 },
    { day: 'Fri', views: 720, users: 160, sessions: 580 },
    { day: 'Sat', views: 480, users: 95, sessions: 380 },
    { day: 'Sun', views: 390, users: 78, sessions: 310 },
  ];

  const sampleDeviceData = [
    { name: 'Desktop', value: 65 },
    { name: 'Mobile', value: 28 },
    { name: 'Tablet', value: 7 },
  ];

  if (loading) {
    return <div className="loading">Loading analytics...</div>;
  }

  return (
    <div className="page-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Link to="/dashboard" className="back-link">← Back to Dashboard</Link>
          <h2>Analytics</h2>
        </div>
        <nav className="sidebar-nav">
          <Link to="/dashboard" className="nav-link">
            🏠 Dashboard
          </Link>
          <Link to="/dashboards/configure" className="nav-link">
            ⚙️ Configure
          </Link>
          <Link to="/analytics" className="nav-link active">
            📈 Analytics
          </Link>
          <Link to="/reports" className="nav-link">
            📄 Reports
          </Link>
          <Link to="/settings" className="nav-link">
            🔧 Settings
          </Link>
        </nav>
      </aside>

      <main className="main-content">
        <header className="page-header">
          <div>
            <h1>Dashboard Analytics</h1>
            <p className="subtitle">
              {currentDashboard?.name || 'Dashboard'} - Usage and performance insights
            </p>
          </div>
          <select
            value={period}
            onChange={(e) => setPeriod(e.target.value)}
            className="period-selector"
          >
            <option value="24h">Last 24 Hours</option>
            <option value="7d">Last 7 Days</option>
            <option value="30d">Last 30 Days</option>
            <option value="90d">Last 90 Days</option>
          </select>
        </header>

        {/* Stats Cards */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">👁️</div>
            <div>
              <h3>Total Views</h3>
              <p className="stat-value">{stats?.totalViews || 3760}</p>
              <p className="stat-change positive">+15.3%</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">👥</div>
            <div>
              <h3>Unique Users</h3>
              <p className="stat-value">{stats?.uniqueUsers || 783}</p>
              <p className="stat-change positive">+8.7%</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">⏱️</div>
            <div>
              <h3>Avg. Session Duration</h3>
              <p className="stat-value">{stats?.avgSessionDuration || '4m 32s'}</p>
              <p className="stat-change positive">+12.5%</p>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">⚡</div>
            <div>
              <h3>Avg. Load Time</h3>
              <p className="stat-value">{stats?.avgLoadTime || '1.2s'}</p>
              <p className="stat-change negative">-8.3%</p>
            </div>
          </div>
        </div>

        {/* Usage Trends Chart */}
        <section className="chart-section">
          <h2>Usage Trends</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={sampleUsageData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="day" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="views" stroke="#1a5490" strokeWidth={2} />
              <Line type="monotone" dataKey="sessions" stroke="#28a745" strokeWidth={2} />
              <Line type="monotone" dataKey="users" stroke="#ffc107" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </section>

        <div className="charts-row">
          {/* Device Usage */}
          <section className="chart-section">
            <h2>Device Usage</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={sampleDeviceData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {sampleDeviceData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </section>

          {/* Event Distribution */}
          <section className="chart-section">
            <h2>Event Distribution</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={usageData.length > 0 ? usageData : [
                { event: 'VIEW', count: 2450 },
                { event: 'EDIT', count: 320 },
                { event: 'REFRESH', count: 890 },
                { event: 'EXPORT', count: 45 },
              ]}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="event" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="count" fill="#1a5490" />
              </BarChart>
            </ResponsiveContainer>
          </section>
        </div>

        {/* Top Users Table */}
        <section className="table-section">
          <h2>Most Active Users</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Sessions</th>
                <th>Actions</th>
                <th>Last Active</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>john.doe@rapidassist.ie</td>
                <td>145</td>
                <td>892</td>
                <td>2 minutes ago</td>
              </tr>
              <tr>
                <td>sarah.smith@rapidassist.ie</td>
                <td>128</td>
                <td>654</td>
                <td>15 minutes ago</td>
              </tr>
              <tr>
                <td>mike.johnson@rapidassist.ie</td>
                <td>112</td>
                <td>523</td>
                <td>1 hour ago</td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default Analytics;
