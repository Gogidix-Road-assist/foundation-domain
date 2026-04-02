import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { websocketService } from '../services/websocket';

interface Alert {
  id: string;
  type: 'SLA_BREACH' | 'CRITICAL_INCIDENT' | 'SYSTEM_ERROR' | 'PARTNER_OFFLINE' | 'HIGH_VOLUME';
  severity: 'INFO' | 'WARNING' | 'CRITICAL';
  title: string;
  message: string;
  entityId?: string;
  entityType?: string;
  createdAt: string;
  acknowledged: boolean;
  resolvedAt?: string;
}

const AlertsPanel: React.FC = () => {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'UNACKNOWLEDGED' | 'ACKNOWLEDGED'>('ALL');
  const [severityFilter, setSeverityFilter] = useState<'ALL' | 'INFO' | 'WARNING' | 'CRITICAL'>('ALL');

  useEffect(() => {
    fetchAlerts();
    const subscription = websocketService.subscribe('/topic/alerts', (message) => {
      const alert = JSON.parse(message.body);
      setAlerts((prev) => [alert, ...prev]);
    });
    return () => subscription.unsubscribe();
  }, []);

  const fetchAlerts = async () => {
    try {
      const response = await api.get('/api/alerting/alerts');
      setAlerts(response.data);
    } catch (error) {
      console.error('Error fetching alerts:', error);
    }
  };

  const acknowledgeAlert = async (alertId: string) => {
    try {
      await api.patch(`/api/alerting/alerts/${alertId}/acknowledge`);
      setAlerts((prev) => prev.map((a) => (a.id === alertId ? { ...a, acknowledged: true } : a)));
    } catch (error) {
      console.error('Error acknowledging alert:', error);
    }
  };

  const resolveAlert = async (alertId: string) => {
    try {
      await api.patch(`/api/alerting/alerts/${alertId}/resolve`);
      setAlerts((prev) => prev.map((a) => (a.id === alertId ? { ...a, resolvedAt: new Date().toISOString() } : a)));
    } catch (error) {
      console.error('Error resolving alert:', error);
    }
  };

  const filteredAlerts = alerts.filter((a) => {
    if (filter === 'UNACKNOWLEDGED' && a.acknowledged) return false;
    if (filter === 'ACKNOWLEDGED' && !a.acknowledged) return false;
    if (severityFilter !== 'ALL' && a.severity !== severityFilter) return false;
    return true;
  });

  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'CRITICAL': return 'bg-red-100 border-red-500';
      case 'WARNING': return 'bg-yellow-100 border-yellow-500';
      case 'INFO': return 'bg-blue-100 border-blue-500';
      default: return 'bg-gray-100';
    }
  };

  const getSeverityIcon = (severity: string) => {
    switch (severity) {
      case 'CRITICAL': return '🔴';
      case 'WARNING': return '⚠️';
      case 'INFO': return 'ℹ️';
      default: return '📢';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Alerts Panel</h1>
        <p className="text-gray-600">SLA breaches, critical incidents, and emergency alerts</p>
      </div>

      <div className="p-6">
        <div className="bg-white rounded-lg shadow mb-6 p-4 flex gap-4">
          <select value={filter} onChange={(e) => setFilter(e.target.value as any)} className="border rounded px-3 py-2">
            <option value="ALL">All Alerts</option>
            <option value="UNACKNOWLEDGED">Unacknowledged</option>
            <option value="ACKNOWLEDGED">Acknowledged</option>
          </select>
          <select value={severityFilter} onChange={(e) => setSeverityFilter(e.target.value as any)} className="border rounded px-3 py-2">
            <option value="ALL">All Severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="WARNING">Warning</option>
            <option value="INFO">Info</option>
          </select>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow p-4 border-l-4 border-red-500">
            <p className="text-gray-600 text-sm">Critical</p>
            <p className="text-3xl font-bold text-red-600">{alerts.filter((a) => a.severity === 'CRITICAL' && !a.resolvedAt).length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4 border-l-4 border-yellow-500">
            <p className="text-gray-600 text-sm">Warning</p>
            <p className="text-3xl font-bold text-yellow-600">{alerts.filter((a) => a.severity === 'WARNING' && !a.resolvedAt).length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4 border-l-4 border-blue-500">
            <p className="text-gray-600 text-sm">Info</p>
            <p className="text-3xl font-bold text-blue-600">{alerts.filter((a) => a.severity === 'INFO' && !a.resolvedAt).length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4 border-l-4 border-gray-500">
            <p className="text-gray-600 text-sm">Unacknowledged</p>
            <p className="text-3xl font-bold">{alerts.filter((a) => !a.acknowledged).length}</p>
          </div>
        </div>

        <div className="space-y-4">
          {filteredAlerts.map((alert) => (
            <div
              key={alert.id}
              className={`bg-white rounded-lg shadow p-4 border-l-4 ${getSeverityColor(alert.severity)} ${
                alert.acknowledged ? 'opacity-60' : ''
              }`}
            >
              <div className="flex justify-between items-start">
                <div className="flex items-start gap-3">
                  <span className="text-2xl">{getSeverityIcon(alert.severity)}</span>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold text-gray-900">{alert.title}</h3>
                      <span className={`text-xs px-2 py-1 rounded ${
                        alert.severity === 'CRITICAL' ? 'bg-red-200 text-red-800' :
                        alert.severity === 'WARNING' ? 'bg-yellow-200 text-yellow-800' :
                        'bg-blue-200 text-blue-800'
                      }`}>{alert.severity}</span>
                      {!alert.acknowledged && <span className="text-xs px-2 py-1 rounded bg-gray-200">New</span>}
                    </div>
                    <p className="text-gray-700 mb-2">{alert.message}</p>
                    <div className="flex gap-4 text-xs text-gray-500">
                      <span>Type: {alert.type.replace(/_/g, ' ')}</span>
                      {alert.entityType && <span>Entity: {alert.entityType}</span>}
                      <span>{new Date(alert.createdAt).toLocaleString()}</span>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col gap-2">
                  {!alert.acknowledged && (
                    <button
                      onClick={() => acknowledgeAlert(alert.id)}
                      className="px-3 py-1 text-sm bg-gray-600 text-white rounded hover:bg-gray-700"
                    >
                      Acknowledge
                    </button>
                  )}
                  {!alert.resolvedAt && (
                    <button
                      onClick={() => resolveAlert(alert.id)}
                      className="px-3 py-1 text-sm bg-green-600 text-white rounded hover:bg-green-700"
                    >
                      Resolve
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
          {filteredAlerts.length === 0 && (
            <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
              No alerts found
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AlertsPanel;
