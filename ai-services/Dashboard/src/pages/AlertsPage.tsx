import { useState } from 'react'
import { Bell, AlertTriangle, CheckCircle, XCircle, Clock } from 'lucide-react'
import { format } from 'date-fns'

interface Alert {
  id: string
  type: 'critical' | 'warning' | 'info' | 'resolved'
  service: string
  message: string
  timestamp: Date
  acknowledged: boolean
}

const mockAlerts: Alert[] = [
  {
    id: '1',
    type: 'critical',
    service: 'ai-speech-recognition-service',
    message: 'Service error rate exceeded 2% threshold',
    timestamp: new Date(Date.now() - 5 * 60 * 1000),
    acknowledged: false,
  },
  {
    id: '2',
    type: 'warning',
    service: 'ai-image-recognition-service',
    message: 'CPU usage above 75% for extended period',
    timestamp: new Date(Date.now() - 15 * 60 * 1000),
    acknowledged: false,
  },
  {
    id: '3',
    type: 'critical',
    service: 'sentiment-analysis-service',
    message: 'Service is down - health check failed',
    timestamp: new Date(Date.now() - 30 * 60 * 1000),
    acknowledged: true,
  },
  {
    id: '4',
    type: 'info',
    service: 'ai-training-ml-service',
    message: 'Model training completed successfully',
    timestamp: new Date(Date.now() - 45 * 60 * 1000),
    acknowledged: true,
  },
  {
    id: '5',
    type: 'resolved',
    service: 'ai-chatbot-service',
    message: 'High memory usage resolved after restart',
    timestamp: new Date(Date.now() - 60 * 60 * 1000),
    acknowledged: true,
  },
  {
    id: '6',
    type: 'warning',
    service: 'route-optimization-service',
    message: 'Response time degraded (avg 350ms)',
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
    acknowledged: false,
  },
]

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<Alert[]>(mockAlerts)
  const [filter, setFilter] = useState<'all' | 'critical' | 'warning' | 'info' | 'resolved'>('all')
  const [showAcknowledged, setShowAcknowledged] = useState(true)

  const filteredAlerts = alerts.filter((alert) => {
    const matchesFilter = filter === 'all' || alert.type === filter
    const matchesAcknowledged = showAcknowledged || !alert.acknowledged
    return matchesFilter && matchesAcknowledged
  })

  const acknowledgeAlert = (id: string) => {
    setAlerts((prev) =>
      prev.map((alert) =>
        alert.id === id ? { ...alert, acknowledged: true } : alert
      )
    )
  }

  const resolveAlert = (id: string) => {
    setAlerts((prev) =>
      prev.map((alert) =>
        alert.id === id ? { ...alert, type: 'resolved' as const, acknowledged: true } : alert
      )
    )
  }

  const getAlertIcon = (type: Alert['type']) => {
    switch (type) {
      case 'critical':
        return <XCircle className="w-5 h-5 text-red-500" />
      case 'warning':
        return <AlertTriangle className="w-5 h-5 text-yellow-500" />
      case 'resolved':
        return <CheckCircle className="w-5 h-5 text-green-500" />
      default:
        return <Bell className="w-5 h-5 text-blue-500" />
    }
  }

  const getAlertBadgeClass = (type: Alert['type']) => {
    switch (type) {
      case 'critical':
        return 'bg-red-100 text-red-800'
      case 'warning':
        return 'bg-yellow-100 text-yellow-800'
      case 'resolved':
        return 'bg-green-100 text-green-800'
      default:
        return 'bg-blue-100 text-blue-800'
    }
  }

  const criticalCount = alerts.filter((a) => a.type === 'critical' && !a.acknowledged).length
  const warningCount = alerts.filter((a) => a.type === 'warning' && !a.acknowledged).length

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Alerts</h1>
          <p className="mt-1 text-sm text-gray-500">
            Monitor and manage service alerts
          </p>
        </div>

        <div className="flex items-center space-x-4">
          {criticalCount > 0 && (
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-red-100 text-red-800">
              <XCircle className="w-4 h-4 mr-1" />
              {criticalCount} Critical
            </span>
          )}
          {warningCount > 0 && (
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
              <AlertTriangle className="w-4 h-4 mr-1" />
              {warningCount} Warning
            </span>
          )}
        </div>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="card-body">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            {/* Type Filter */}
            <div className="flex items-center space-x-2">
              <span className="text-sm font-medium text-gray-700">Filter:</span>
              {(['all', 'critical', 'warning', 'info', 'resolved'] as const).map((type) => (
                <button
                  key={type}
                  onClick={() => setFilter(type)}
                  className={`px-3 py-1 text-sm font-medium rounded-lg capitalize transition-colors ${
                    filter === type
                      ? 'bg-primary-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  {type}
                </button>
              ))}
            </div>

            {/* Show Acknowledged Toggle */}
            <label className="flex items-center space-x-2">
              <input
                type="checkbox"
                checked={showAcknowledged}
                onChange={(e) => setShowAcknowledged(e.target.checked)}
                className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-gray-700">Show acknowledged</span>
            </label>
          </div>
        </div>
      </div>

      {/* Alerts List */}
      <div className="space-y-3">
        {filteredAlerts.map((alert) => (
          <div
            key={alert.id}
            className={`card transition-all ${
              alert.acknowledged ? 'opacity-60' : ''
            }`}
          >
            <div className="card-body">
              <div className="flex items-start justify-between">
                <div className="flex items-start space-x-3">
                  {getAlertIcon(alert.type)}
                  <div className="flex-1">
                    <div className="flex items-center space-x-2">
                      <h4 className="text-sm font-medium text-gray-900">
                        {alert.service}
                      </h4>
                      <span className={`badge ${getAlertBadgeClass(alert.type)}`}>
                        {alert.type}
                      </span>
                      {alert.acknowledged && (
                        <span className="badge badge-success">Acknowledged</span>
                      )}
                    </div>
                    <p className="mt-1 text-sm text-gray-600">{alert.message}</p>
                    <div className="mt-2 flex items-center text-xs text-gray-500">
                      <Clock className="w-3 h-3 mr-1" />
                      {format(alert.timestamp, 'MMM d, HH:mm')}
                    </div>
                  </div>
                </div>

                {!alert.acknowledged && alert.type !== 'resolved' && (
                  <div className="flex items-center space-x-2 ml-4">
                    <button
                      onClick={() => acknowledgeAlert(alert.id)}
                      className="btn btn-sm btn-secondary"
                    >
                      Acknowledge
                    </button>
                    {alert.type === 'critical' || alert.type === 'warning' ? (
                      <button
                        onClick={() => resolveAlert(alert.id)}
                        className="btn btn-sm btn-primary"
                      >
                        Resolve
                      </button>
                    ) : null}
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {filteredAlerts.length === 0 && (
        <div className="text-center py-12 card">
          <div className="card-body">
            <Bell className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No alerts found</h3>
            <p className="mt-1 text-sm text-gray-500">
              All systems are operating normally.
            </p>
          </div>
        </div>
      )}
    </div>
  )
}
