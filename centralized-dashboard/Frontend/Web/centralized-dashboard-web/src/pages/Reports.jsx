import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { reportsAPI } from '../services/api';

function Reports() {
  const navigate = useNavigate();
  const { isAuthenticated } = useStore();
  const [reports, setReports] = useState([]);
  const [executions, setExecutions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [selectedReport, setSelectedReport] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: 'DASHBOARD_SNAPSHOT',
    sourceType: 'DASHBOARD',
    sourceId: '',
    format: 'PDF',
    schedule: 'ON_DEMAND',
  });

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    loadReports();
  }, [isAuthenticated, navigate]);

  const loadReports = async () => {
    try {
      setLoading(true);
      const reportsRes = await reportsAPI.getAll();
      setReports(reportsRes.data);

      // Load executions for each report
      const executionsPromises = reportsRes.data.map(report =>
        reportsAPI.getById(report.id).then(res => res.data.executions || [])
      );
      const allExecutions = await Promise.all(executionsPromises);
      setExecutions(allExecutions.flat());
    } catch (error) {
      console.error('Error loading reports:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateReport = async (e) => {
    e.preventDefault();

    try {
      const response = await reportsAPI.create(formData);
      setReports([...reports, response.data]);
      setShowCreateForm(false);
      setFormData({
        name: '',
        description: '',
        type: 'DASHBOARD_SNAPSHOT',
        sourceType: 'DASHBOARD',
        sourceId: '',
        format: 'PDF',
        schedule: 'ON_DEMAND',
      });
    } catch (error) {
      console.error('Error creating report:', error);
      alert('Failed to create report');
    }
  };

  const handleExecuteReport = async (reportId) => {
    try {
      await reportsAPI.execute(reportId);
      alert('Report execution started');
    } catch (error) {
      console.error('Error executing report:', error);
      alert('Failed to execute report');
    }
  };

  const handleDownloadReport = async (reportId, format) => {
    try {
      const response = await reportsAPI.download(reportId, format);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `report.${format.toLowerCase()}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading report:', error);
      alert('Failed to download report');
    }
  };

  const handleDeleteReport = async (reportId) => {
    if (!confirm('Are you sure you want to delete this report?')) return;

    try {
      await reportsAPI.delete(reportId);
      setReports(reports.filter(r => r.id !== reportId));
    } catch (error) {
      console.error('Error deleting report:', error);
      alert('Failed to delete report');
    }
  };

  if (loading) {
    return <div className="loading">Loading reports...</div>;
  }

  return (
    <div className="page-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Link to="/dashboard" className="back-link">← Back to Dashboard</Link>
          <h2>Reports</h2>
        </div>
        <nav className="sidebar-nav">
          <Link to="/dashboard" className="nav-link">
            🏠 Dashboard
          </Link>
          <Link to="/dashboards/configure" className="nav-link">
            ⚙️ Configure
          </Link>
          <Link to="/analytics" className="nav-link">
            📈 Analytics
          </Link>
          <Link to="/reports" className="nav-link active">
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
            <h1>Report Management</h1>
            <p className="subtitle">Generate and schedule reports</p>
          </div>
          <button
            onClick={() => setShowCreateForm(true)}
            className="btn btn-primary"
          >
            + New Report
          </button>
        </header>

        {/* Create Report Form */}
        {showCreateForm && (
          <div className="form-card">
            <h2>Create New Report</h2>
            <form onSubmit={handleCreateReport}>
              <div className="form-group">
                <label htmlFor="name">Report Name</label>
                <input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  placeholder="Weekly Performance Report"
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  rows="3"
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="type">Report Type</label>
                  <select
                    id="type"
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  >
                    <option value="DASHBOARD_SNAPSHOT">Dashboard Snapshot</option>
                    <option value="USAGE_ANALYTICS">Usage Analytics</option>
                    <option value="PERFORMANCE_REPORT">Performance Report</option>
                    <option value="CUSTOM_ANALYTICS">Custom Analytics</option>
                    <option value="SUMMARY_REPORT">Summary Report</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="format">Format</label>
                  <select
                    id="format"
                    value={formData.format}
                    onChange={(e) => setFormData({ ...formData, format: e.target.value })}
                  >
                    <option value="PDF">PDF</option>
                    <option value="EXCEL">Excel</option>
                    <option value="CSV">CSV</option>
                    <option value="HTML">HTML</option>
                    <option value="JSON">JSON</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="schedule">Schedule</label>
                <select
                  id="schedule"
                  value={formData.schedule}
                  onChange={(e) => setFormData({ ...formData, schedule: e.target.value })}
                >
                  <option value="ON_DEMAND">On Demand</option>
                  <option value="HOURLY">Hourly</option>
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                </select>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-primary">Create Report</button>
                <button
                  type="button"
                  onClick={() => setShowCreateForm(false)}
                  className="btn btn-secondary"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Reports List */}
        <div className="reports-list">
          {reports.map((report) => (
            <div key={report.id} className="report-card">
              <div className="report-info">
                <h3>{report.name}</h3>
                <p>{report.description || 'No description'}</p>
                <div className="report-meta">
                  <span className="badge">{report.type}</span>
                  <span className="badge">{report.format}</span>
                  <span className="badge">{report.schedule}</span>
                  <span className={`status ${report.isActive ? 'active' : 'inactive'}`}>
                    {report.isActive ? 'Active' : 'Inactive'}
                  </span>
                </div>
              </div>
              <div className="report-actions">
                <button
                  onClick={() => handleExecuteReport(report.id)}
                  className="btn btn-sm btn-primary"
                >
                  Run Now
                </button>
                <button
                  onClick={() => handleDownloadReport(report.id, report.format)}
                  className="btn btn-sm btn-secondary"
                >
                  Download
                </button>
                <button
                  onClick={() => handleDeleteReport(report.id)}
                  className="btn btn-sm btn-danger"
                >
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Recent Executions */}
        <section className="executions-section">
          <h2>Recent Executions</h2>
          <table className="data-table">
            <thead>
              <tr>
                <th>Report</th>
                <th>Status</th>
                <th>Format</th>
                <th>Started</th>
                <th>Duration</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {executions.slice(0, 10).map((execution) => (
                <tr key={execution.id}>
                  <td>{execution.reportName || 'Report'}</td>
                  <td>
                    <span className={`status status-${execution.status?.toLowerCase()}`}>
                      {execution.status}
                    </span>
                  </td>
                  <td>{execution.format}</td>
                  <td>{new Date(execution.startedAt).toLocaleString()}</td>
                  <td>{execution.duration || '-'}</td>
                  <td>
                    {execution.status === 'COMPLETED' && (
                      <button
                        onClick={() => handleDownloadReport(execution.reportId, execution.format)}
                        className="btn btn-sm btn-secondary"
                      >
                        Download
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {reports.length === 0 && (
          <div className="empty-state">
            <h3>No reports found</h3>
            <p>Create your first report to get started.</p>
          </div>
        )}
      </main>
    </div>
  );
}

export default Reports;
