import { useStore } from '../store/useStore';
import { alertsAPI } from '../services/api';
import './AlertsPanel.css';

function AlertsPanel() {
  const { alerts, removeAlert } = useStore();

  const handleAcknowledge = async (alertId) => {
    try {
      await alertsAPI.acknowledge(alertId);
      removeAlert(alertId);
    } catch (error) {
      console.error('Error acknowledging alert:', error);
    }
  };

  const handleDismiss = async (alertId) => {
    try {
      await alertsAPI.dismiss(alertId);
      removeAlert(alertId);
    } catch (error) {
      console.error('Error dismissing alert:', error);
    }
  };

  const getAlertIcon = (severity) => {
    switch (severity?.toLowerCase()) {
      case 'critical': return '🔴';
      case 'warning': return '🟡';
      case 'info': return '🔵';
      default: return '⚪';
    }
  };

  if (alerts.length === 0) {
    return null;
  }

  return (
    <div className="alerts-panel">
      <div className="alerts-header">
        <h3>⚠️ Active Alerts ({alerts.length})</h3>
      </div>
      <div className="alerts-list">
        {alerts.slice(0, 5).map((alert) => (
          <div key={alert.id} className={`alert-item alert-${alert.severity?.toLowerCase()}`}>
            <div className="alert-content">
              <span className="alert-icon">{getAlertIcon(alert.severity)}</span>
              <div className="alert-details">
                <p className="alert-title">{alert.title || alert.message}</p>
                {alert.description && <p className="alert-description">{alert.description}</p>}
                <p className="alert-time">{new Date(alert.createdAt).toLocaleString()}</p>
              </div>
            </div>
            <div className="alert-actions">
              <button
                onClick={() => handleAcknowledge(alert.id)}
                className="btn btn-sm btn-secondary"
              >
                Acknowledge
              </button>
              <button
                onClick={() => handleDismiss(alert.id)}
                className="btn btn-sm btn-danger"
              >
                Dismiss
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AlertsPanel;
