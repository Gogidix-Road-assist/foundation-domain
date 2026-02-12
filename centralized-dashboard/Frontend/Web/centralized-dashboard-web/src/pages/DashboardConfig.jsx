import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { dashboardAPI, widgetAPI } from '../services/api';

function DashboardConfig() {
  const navigate = useNavigate();
  const { dashboards, currentDashboard, isAuthenticated, setCurrentDashboard } = useStore();
  const [loading, setLoading] = useState(false);
  const [editingDashboard, setEditingDashboard] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    layout: 'GRID',
    theme: { primaryColor: '#1a5490', mode: 'light' },
    permissions: { view: ['*'], edit: ['admin'], delete: ['admin'] },
  });

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  const handleCreateDashboard = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await dashboardAPI.create(formData);
      const newDashboard = response.data;
      useStore.getState().addDashboard(newDashboard);
      setShowCreateForm(false);
      setFormData({
        name: '',
        description: '',
        layout: 'GRID',
        theme: { primaryColor: '#1a5490', mode: 'light' },
        permissions: { view: ['*'], edit: ['admin'], delete: ['admin'] },
      });
    } catch (error) {
      console.error('Error creating dashboard:', error);
      alert('Failed to create dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateDashboard = async (e) => {
    e.preventDefault();
    if (!editingDashboard) return;

    setLoading(true);

    try {
      await dashboardAPI.update(editingDashboard.id, formData);
      useStore.getState().updateDashboard(editingDashboard.id, formData);
      setEditingDashboard(null);
    } catch (error) {
      console.error('Error updating dashboard:', error);
      alert('Failed to update dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteDashboard = async (dashboardId) => {
    if (!confirm('Are you sure you want to delete this dashboard?')) return;

    try {
      await dashboardAPI.delete(dashboardId);
      useStore.getState().deleteDashboard(dashboardId);
      if (currentDashboard?.id === dashboardId) {
        setCurrentDashboard(null);
      }
    } catch (error) {
      console.error('Error deleting dashboard:', error);
      alert('Failed to delete dashboard');
    }
  };

  const handleCloneDashboard = async (dashboardId) => {
    try {
      const response = await dashboardAPI.clone(dashboardId);
      useStore.getState().addDashboard(response.data);
    } catch (error) {
      console.error('Error cloning dashboard:', error);
      alert('Failed to clone dashboard');
    }
  };

  const startEdit = (dashboard) => {
    setEditingDashboard(dashboard);
    setFormData({
      name: dashboard.name,
      description: dashboard.description,
      layout: dashboard.layout,
      theme: dashboard.theme,
      permissions: dashboard.permissions,
    });
  };

  return (
    <div className="page-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Link to="/dashboard" className="back-link">← Back to Dashboard</Link>
          <h2>Dashboard Configuration</h2>
        </div>
        <nav className="sidebar-nav">
          <Link to="/dashboard" className="nav-link">
            🏠 Dashboard
          </Link>
          <Link to="/dashboards/configure" className="nav-link active">
            ⚙️ Configure
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
      </aside>

      <main className="main-content">
        <header className="page-header">
          <div>
            <h1>Dashboard Management</h1>
            <p className="subtitle">Create and manage your dashboards</p>
          </div>
          <button
            onClick={() => setShowCreateForm(true)}
            className="btn btn-primary"
          >
            + New Dashboard
          </button>
        </header>

        {/* Create/Edit Form */}
        {(showCreateForm || editingDashboard) && (
          <div className="form-card">
            <h2>{editingDashboard ? 'Edit Dashboard' : 'Create New Dashboard'}</h2>
            <form onSubmit={editingDashboard ? handleUpdateDashboard : handleCreateDashboard}>
              <div className="form-group">
                <label htmlFor="name">Dashboard Name</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  placeholder="My Dashboard"
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  rows="3"
                  placeholder="Dashboard description"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="layout">Layout</label>
                  <select
                    id="layout"
                    value={formData.layout}
                    onChange={(e) => setFormData({ ...formData, layout: e.target.value })}
                  >
                    <option value="GRID">Grid</option>
                    <option value="FREE_FORM">Free Form</option>
                    <option value="TABS">Tabs</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="primaryColor">Primary Color</label>
                  <input
                    type="color"
                    id="primaryColor"
                    value={formData.theme.primaryColor}
                    onChange={(e) => setFormData({
                      ...formData,
                      theme: { ...formData.theme, primaryColor: e.target.value }
                    })}
                  />
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Saving...' : (editingDashboard ? 'Update Dashboard' : 'Create Dashboard')}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateForm(false);
                    setEditingDashboard(null);
                  }}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Dashboards List */}
        <div className="dashboards-list">
          {dashboards.map((dashboard) => (
            <div key={dashboard.id} className="dashboard-card">
              <div className="dashboard-info">
                <h3>{dashboard.name}</h3>
                <p>{dashboard.description}</p>
                <div className="dashboard-meta">
                  <span className="badge">Layout: {dashboard.layout}</span>
                  <span className={`status ${dashboard.isActive ? 'active' : 'inactive'}`}>
                    {dashboard.isActive ? 'Active' : 'Inactive'}
                  </span>
                </div>
              </div>
              <div className="dashboard-actions">
                <button
                  onClick={() => { setCurrentDashboard(dashboard); navigate('/dashboard'); }}
                  className="btn btn-sm btn-primary"
                >
                  View
                </button>
                <button
                  onClick={() => startEdit(dashboard)}
                  className="btn btn-sm btn-secondary"
                >
                  Edit
                </button>
                <button
                  onClick={() => handleCloneDashboard(dashboard.id)}
                  className="btn btn-sm btn-secondary"
                >
                  Clone
                </button>
                <button
                  onClick={() => handleDeleteDashboard(dashboard.id)}
                  className="btn btn-sm btn-danger"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        {dashboards.length === 0 && (
          <div className="empty-state">
            <h3>No dashboards found</h3>
            <p>Create your first dashboard to get started.</p>
          </div>
        )}
      </main>
    </div>
  );
}

export default DashboardConfig;
