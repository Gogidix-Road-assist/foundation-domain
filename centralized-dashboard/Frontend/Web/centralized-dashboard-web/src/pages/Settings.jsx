import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';

function Settings() {
  const navigate = useNavigate();
  const { isAuthenticated, user, isDarkMode, toggleDarkMode } = useStore();
  const [activeTab, setActiveTab] = useState('profile');
  const [saving, setSaving] = useState(false);

  const [profileData, setProfileData] = useState({
    name: '',
    email: '',
    role: '',
  });

  const [notifications, setNotifications] = useState({
    emailAlerts: true,
    dashboardUpdates: true,
    systemNotifications: true,
    weeklyReport: false,
  });

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (user) {
      setProfileData({
        name: user.name || '',
        email: user.email || '',
        role: user.role || '',
      });
    }
  }, [isAuthenticated, user, navigate]);

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setSaving(true);

    // Simulate API call
    setTimeout(() => {
      setSaving(false);
      alert('Profile updated successfully!');
    }, 1000);
  };

  const handleSaveNotifications = async (e) => {
    e.preventDefault();
    setSaving(true);

    // Simulate API call
    setTimeout(() => {
      setSaving(false);
      alert('Notification preferences saved!');
    }, 1000);
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setSaving(true);

    // Simulate API call
    setTimeout(() => {
      setSaving(false);
      alert('Password changed successfully!');
    }, 1000);
  };

  return (
    <div className="page-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Link to="/dashboard" className="back-link">← Back to Dashboard</Link>
          <h2>Settings</h2>
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
          <Link to="/reports" className="nav-link">
            📄 Reports
          </Link>
          <Link to="/settings" className="nav-link active">
            🔧 Settings
          </Link>
        </nav>
      </aside>

      <main className="main-content">
        <header className="page-header">
          <h1>Settings</h1>
          <p className="subtitle">Manage your account and preferences</p>
        </header>

        <div className="settings-layout">
          {/* Settings Tabs */}
          <div className="settings-tabs">
            <button
              className={`tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              Profile
            </button>
            <button
              className={`tab-btn ${activeTab === 'notifications' ? 'active' : ''}`}
              onClick={() => setActiveTab('notifications')}
            >
              Notifications
            </button>
            <button
              className={`tab-btn ${activeTab === 'security' ? 'active' : ''}`}
              onClick={() => setActiveTab('security')}
            >
              Security
            </button>
            <button
              className={`tab-btn ${activeTab === 'appearance' ? 'active' : ''}`}
              onClick={() => setActiveTab('appearance')}
            >
              Appearance
            </button>
          </div>

          {/* Settings Content */}
          <div className="settings-content">
            {activeTab === 'profile' && (
              <div className="settings-panel">
                <h2>Profile Settings</h2>
                <form onSubmit={handleSaveProfile}>
                  <div className="form-group">
                    <label htmlFor="name">Full Name</label>
                    <input
                      type="text"
                      id="name"
                      value={profileData.name}
                      onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="email">Email Address</label>
                    <input
                      type="email"
                      id="email"
                      value={profileData.email}
                      onChange={(e) => setProfileData({ ...profileData, email: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label htmlFor="role">Role</label>
                    <input
                      type="text"
                      id="role"
                      value={profileData.role}
                      disabled
                    />
                  </div>

                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? 'Saving...' : 'Save Changes'}
                  </button>
                </form>
              </div>
            )}

            {activeTab === 'notifications' && (
              <div className="settings-panel">
                <h2>Notification Preferences</h2>
                <form onSubmit={handleSaveNotifications}>
                  <div className="toggle-item">
                    <label className="toggle-label">
                      <input
                        type="checkbox"
                        checked={notifications.emailAlerts}
                        onChange={(e) => setNotifications({ ...notifications, emailAlerts: e.target.checked })}
                      />
                      <span>Email Alerts</span>
                    </label>
                    <p>Receive email notifications for important alerts</p>
                  </div>

                  <div className="toggle-item">
                    <label className="toggle-label">
                      <input
                        type="checkbox"
                        checked={notifications.dashboardUpdates}
                        onChange={(e) => setNotifications({ ...notifications, dashboardUpdates: e.target.checked })}
                      />
                      <span>Dashboard Updates</span>
                    </label>
                    <p>Get notified when dashboards are updated</p>
                  </div>

                  <div className="toggle-item">
                    <label className="toggle-label">
                      <input
                        type="checkbox"
                        checked={notifications.systemNotifications}
                        onChange={(e) => setNotifications({ ...notifications, systemNotifications: e.target.checked })}
                      />
                      <span>System Notifications</span>
                    </label>
                    <p>Receive system-wide notifications and announcements</p>
                  </div>

                  <div className="toggle-item">
                    <label className="toggle-label">
                      <input
                        type="checkbox"
                        checked={notifications.weeklyReport}
                        onChange={(e) => setNotifications({ ...notifications, weeklyReport: e.target.checked })}
                      />
                      <span>Weekly Summary Report</span>
                    </label>
                    <p>Receive a weekly summary of dashboard activity</p>
                  </div>

                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? 'Saving...' : 'Save Changes'}
                  </button>
                </form>
              </div>
            )}

            {activeTab === 'security' && (
              <div className="settings-panel">
                <h2>Security Settings</h2>

                <div className="form-section">
                  <h3>Change Password</h3>
                  <form onSubmit={handleChangePassword}>
                    <div className="form-group">
                      <label htmlFor="currentPassword">Current Password</label>
                      <input type="password" id="currentPassword" />
                    </div>
                    <div className="form-group">
                      <label htmlFor="newPassword">New Password</label>
                      <input type="password" id="newPassword" />
                    </div>
                    <div className="form-group">
                      <label htmlFor="confirmPassword">Confirm New Password</label>
                      <input type="password" id="confirmPassword" />
                    </div>
                    <button type="submit" className="btn btn-primary" disabled={saving}>
                      {saving ? 'Updating...' : 'Update Password'}
                    </button>
                  </form>
                </div>

                <div className="form-section">
                  <h3>Two-Factor Authentication</h3>
                  <p className="help-text">Add an extra layer of security to your account</p>
                  <button className="btn btn-secondary">Enable 2FA</button>
                </div>

                <div className="form-section">
                  <h3>Active Sessions</h3>
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Device</th>
                        <th>Location</th>
                        <th>Last Active</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>Chrome on Windows</td>
                        <td>Dublin, Ireland</td>
                        <td>Current session</td>
                        <td>-</td>
                      </tr>
                      <tr>
                        <td>Firefox on macOS</td>
                        <td>Cork, Ireland</td>
                        <td>2 hours ago</td>
                        <td><button className="btn btn-sm btn-danger">Revoke</button></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {activeTab === 'appearance' && (
              <div className="settings-panel">
                <h2>Appearance Settings</h2>

                <div className="toggle-item">
                  <label className="toggle-label">
                    <input
                      type="checkbox"
                      checked={isDarkMode}
                      onChange={toggleDarkMode}
                    />
                    <span>Dark Mode</span>
                  </label>
                  <p>Enable dark mode for reduced eye strain</p>
                </div>

                <div className="form-group">
                  <label htmlFor="themeColor">Theme Color</label>
                  <div className="color-options">
                    {['#1a5490', '#28a745', '#dc3545', '#ffc107', '#17a2b8', '#6f42c1'].map((color) => (
                      <button
                        key={color}
                        className="color-option"
                        style={{ backgroundColor: color }}
                        onClick={() => console.log('Set theme color:', color)}
                      />
                    ))}
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="language">Language</label>
                  <select id="language">
                    <option value="en">English</option>
                    <option value="ga">Gaeilge (Irish)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="timezone">Timezone</label>
                  <select id="timezone">
                    <option value="Europe/Dublin">Europe/Dublin (GMT+1)</option>
                    <option value="Europe/London">Europe/London (GMT+0)</option>
                  </select>
                </div>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

export default Settings;
