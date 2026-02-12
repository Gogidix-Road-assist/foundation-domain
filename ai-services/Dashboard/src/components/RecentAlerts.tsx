import { Bell, AlertTriangle, XCircle } from 'lucide-react'
import { format } from 'date-fns'

const recentAlerts = [
  {
    id: '1',
    type: 'critical',
    service: 'ai-speech-recognition-service',
    message: 'High error rate detected (2.1%)',
    time: new Date(Date.now() - 5 * 60 * 1000),
  },
  {
    id: '2',
    type: 'warning',
    service: 'ai-image-recognition-service',
    message: 'CPU usage above 80%',
    time: new Date(Date.now() - 15 * 60 * 1000),
  },
  {
    id: '3',
    type: 'info',
    service: 'ai-training-ml-service',
    message: 'Model training completed',
    time: new Date(Date.now() - 30 * 60 * 1000),
  },
  {
    id: '4',
    type: 'critical',
    service: 'sentiment-analysis-service',
    message: 'Service is down',
    time: new Date(Date.now() - 45 * 60 * 1000),
  },
]

export default function RecentAlerts() {
  return (
    <div className="card">
      <div className="card-header flex items-center justify-between">
        <h3 className="card-title">Recent Alerts</h3>
        <button className="text-sm text-primary-600 hover:text-primary-700">
          View All
        </button>
      </div>
      <div className="card-body">
        <div className="space-y-3">
          {recentAlerts.map((alert) => (
            <div key={alert.id} className="flex items-start space-x-3 p-3 rounded-lg bg-gray-50">
              {alert.type === 'critical' && (
                <XCircle className="w-5 h-5 text-red-500 mt-0.5" />
              )}
              {alert.type === 'warning' && (
                <AlertTriangle className="w-5 h-5 text-yellow-500 mt-0.5" />
              )}
              {alert.type === 'info' && (
                <Bell className="w-5 h-5 text-blue-500 mt-0.5" />
              )}

              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {alert.service}
                </p>
                <p className="text-xs text-gray-500 truncate">{alert.message}</p>
              </div>

              <span className="text-xs text-gray-400 whitespace-nowrap">
                {format(alert.time, 'HH:mm')}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
