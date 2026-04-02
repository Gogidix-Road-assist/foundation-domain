/**
 * Alert Panel Component
 * Displays active alerts
 */

import { FC } from 'react';
import { formatDistanceToNow } from 'date-fns';
import type { Alert } from '../types';
import { AlertType } from '../types';

interface AlertPanelProps {
  alerts: Alert[];
  onAcknowledge?: (alertId: string) => void;
}

const alertConfig: Record<AlertType, { icon: string; color: string; bg: string }> = {
  [AlertType.SLA_BREACH]: { icon: '⚠️', color: 'text-red-600', bg: 'bg-red-50' },
  [AlertType.LONG_WAIT_TIME]: { icon: '⏱️', color: 'text-amber-600', bg: 'bg-amber-50' },
  [AlertType.PARTNER_UNAVAILABLE]: { icon: '🚫', color: 'text-gray-600', bg: 'bg-gray-50' },
  [AlertType.EMERGENCY_REQUEST]: { icon: '🚨', color: 'text-red-600', bg: 'bg-red-50' },
  [AlertType.SYSTEM_ISSUE]: { icon: '⚙️', color: 'text-orange-600', bg: 'bg-orange-50' },
  [AlertType.HIGH_VOLUME]: { icon: '📊', color: 'text-blue-600', bg: 'bg-blue-50' }
};

const AlertPanel: FC<AlertPanelProps> = ({ alerts, onAcknowledge }) => {
  if (alerts.length === 0) {
    return null;
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      <div className="px-4 py-3 border-b border-gray-200 flex items-center justify-between">
        <h2 className="font-semibold text-gray-900">Active Alerts</h2>
        <span className="px-2 py-0.5 text-xs font-medium bg-red-100 text-red-700 rounded-full">
          {alerts.length}
        </span>
      </div>
      <div className="divide-y divide-gray-100">
        {alerts.slice(0, 3).map((alert) => {
          const config = alertConfig[alert.type];

          return (
            <div key={alert.alertId} className={`p-3 ${config.bg}`}>
              <div className="flex items-start gap-3">
                <span className="text-lg">{config.icon}</span>
                <div className="flex-1 min-w-0">
                  <p className={`text-sm font-medium ${config.color}`}>
                    {alert.title}
                  </p>
                  <p className="text-xs text-gray-600 mt-1">{alert.message}</p>
                  <p className="text-xs text-gray-400 mt-1">
                    {formatDistanceToNow(new Date(alert.createdAt), { addSuffix: true })}
                  </p>
                </div>
                {onAcknowledge && !alert.acknowledged && (
                  <button
                    onClick={() => onAcknowledge(alert.alertId)}
                    className="px-2 py-1 text-xs font-medium text-white bg-gray-600 rounded hover:bg-gray-700"
                  >
                    Ack
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default AlertPanel;
