import { useState, useEffect } from 'react';
import './ActivityFeed.css';

function ActivityFeed() {
  const [activities, setActivities] = useState([
    { id: 1, type: 'dashboard_view', user: 'John Doe', message: 'viewed Main Dashboard', time: '2 minutes ago', icon: '👁️' },
    { id: 2, type: 'service_alert', user: 'System', message: 'API Gateway response time elevated', time: '5 minutes ago', icon: '⚠️' },
    { id: 3, type: 'report_generated', user: 'Sarah Smith', message: 'generated Weekly Performance Report', time: '15 minutes ago', icon: '📄' },
    { id: 4, type: 'dashboard_created', user: 'Mike Johnson', message: 'created new dashboard "Fleet Analytics"', time: '1 hour ago', icon: '✨' },
    { id: 5, type: 'login', user: 'Jane Doe', message: 'logged in', time: '2 hours ago', icon: '🔐' },
    { id: 6, type: 'widget_updated', user: 'John Doe', message: 'updated metrics widget', time: '3 hours ago', icon: '📊' },
    { id: 7, type: 'service_recovered', user: 'System', message: 'Database Service recovered', time: '4 hours ago', icon: '✅' },
    { id: 8, type: 'export', user: 'Sarah Smith', message: 'exported analytics data', time: '5 hours ago', icon: '📥' },
  ]);

  const [filter, setFilter] = useState('all');

  const filteredActivities = filter === 'all'
    ? activities
    : activities.filter(a => a.type === filter);

  return (
    <div className="activity-feed">
      <div className="activity-header">
        <h2>Recent Activity</h2>
        <select
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          className="filter-select"
        >
          <option value="all">All Activity</option>
          <option value="dashboard_view">Dashboard Views</option>
          <option value="service_alert">Service Alerts</option>
          <option value="report_generated">Reports</option>
          <option value="login">Logins</option>
        </select>
      </div>
      <div className="activity-list">
        {filteredActivities.map((activity) => (
          <div key={activity.id} className="activity-item">
            <span className="activity-icon">{activity.icon}</span>
            <div className="activity-content">
              <p className="activity-message">
                <strong>{activity.user}</strong> {activity.message}
              </p>
              <p className="activity-time">{activity.time}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ActivityFeed;
