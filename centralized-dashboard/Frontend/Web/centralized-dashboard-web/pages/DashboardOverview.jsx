import React, { useEffect } from 'react';
import { useDashboardStore } from '../store/dashboardStore';
import { useWebSocketStore } from '../store/websocketStore';
import MetricWidget from '../components/widgets/MetricWidget';
import './DashboardOverview.css';

const DashboardOverview = () => {
  const { widgets, initializeSampleData } = useDashboardStore();
  const { metrics, connected } = useWebSocketStore();

  useEffect(() => {
    initializeSampleData();
  }, [initializeSampleData]);

  const metricWidgets = widgets.filter((w) => w.type === 'METRIC');

  return (
    <div className="dashboard-overview">
      <div className="dashboard-header">
        <h3>Platform Overview</h3>
        <div className="connection-status">
          <span className={`status-indicator ${connected ? 'connected' : 'disconnected'}`} />
          WebSocket: {connected ? 'Connected' : 'Disconnected'}
        </div>
      </div>

      <div className="metrics-grid">
        {metricWidgets.map((widget) => (
          <MetricWidget
            key={widget.id}
            title={widget.title}
            value={widget.value}
            change={widget.change}
            positive={widget.positive}
            icon={widget.icon}
          />
        ))}
      </div>

      <div className="dashboard-row">
        <div className="card">
          <h3>Real-Time Metrics</h3>
          <div className="metrics-list">
            {Object.entries(metrics).length > 0 ? (
              Object.entries(metrics).map(([key, value]) => (
                <div key={key} className="metric-item">
                  <span className="metric-key">{key}</span>
                  <span className="metric-val">{JSON.stringify(value)}</span>
                </div>
              ))
            ) : (
              <p className="no-data">Waiting for real-time data...</p>
            )}
          </div>
        </div>

        <div className="card">
          <h3>Recent Alerts</h3>
          <div className="alerts-list">
            <div className="alert-item warning">
              <span className="alert-icon">⚠️</span>
              <div className="alert-content">
                <div className="alert-title">High response time</div>
                <div className="alert-time">2 minutes ago</div>
              </div>
            </div>
            <div className="alert-item info">
              <span className="alert-icon">ℹ️</span>
              <div className="alert-content">
                <div className="alert-title">Service deployment completed</div>
                <div className="alert-time">15 minutes ago</div>
              </div>
            </div>
            <div className="alert-item success">
              <span className="alert-icon">✓</span>
              <div className="alert-content">
                <div className="alert-title">Health check passed</div>
                <div className="alert-time">1 hour ago</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardOverview;
