import { useState } from 'react'
import { Save, Bell, Monitor, Palette, Zap } from 'lucide-react'

export default function SettingsPage() {
  const [settings, setSettings] = useState({
    refreshInterval: 30,
    enableNotifications: true,
    alertThreshold: {
      cpu: 80,
      memory: 90,
      responseTime: 500,
      errorRate: 1,
    },
    theme: 'light',
    soundEnabled: true,
    autoRefresh: true,
  })

  const [saved, setSaved] = useState(false)

  const handleSave = () => {
    // Save settings
    localStorage.setItem('dashboard-settings', JSON.stringify(settings))
    setSaved(true)
    setTimeout(() => setSaved(false), 2000)
  }

  const updateSettings = (key: string, value: any) => {
    setSettings((prev) => ({ ...prev, [key]: value }))
  }

  const updateAlertThreshold = (key: string, value: number) => {
    setSettings((prev) => ({
      ...prev,
      alertThreshold: { ...prev.alertThreshold, [key]: value },
    }))
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Settings</h1>
          <p className="mt-1 text-sm text-gray-500">
            Configure your dashboard preferences
          </p>
        </div>

        <button
          onClick={handleSave}
          className={`btn btn-primary ${saved ? 'bg-green-600 hover:bg-green-700' : ''}`}
        >
          <Save className="w-4 h-4 mr-2" />
          {saved ? 'Saved!' : 'Save Settings'}
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* General Settings */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center">
              <Monitor className="w-5 h-5 mr-2 text-gray-500" />
              <h3 className="card-title">General Settings</h3>
            </div>
          </div>
          <div className="card-body space-y-4">
            {/* Auto Refresh */}
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-900">Auto Refresh</p>
                <p className="text-xs text-gray-500">Automatically refresh dashboard data</p>
              </div>
              <button
                onClick={() => updateSettings('autoRefresh', !settings.autoRefresh)}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                  settings.autoRefresh ? 'bg-primary-600' : 'bg-gray-200'
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${
                    settings.autoRefresh ? 'translate-x-6' : 'translate-x-1'
                  }`}
                />
              </button>
            </div>

            {/* Refresh Interval */}
            <div>
              <label className="block text-sm font-medium text-gray-900 mb-2">
                Refresh Interval
              </label>
              <select
                value={settings.refreshInterval}
                onChange={(e) => updateSettings('refreshInterval', parseInt(e.target.value))}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value={10}>10 seconds</option>
                <option value={30}>30 seconds</option>
                <option value={60}>1 minute</option>
                <option value={300}>5 minutes</option>
              </select>
            </div>

            {/* Theme */}
            <div>
              <label className="block text-sm font-medium text-gray-900 mb-2">
                Theme
              </label>
              <select
                value={settings.theme}
                onChange={(e) => updateSettings('theme', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="light">Light</option>
                <option value="dark">Dark</option>
                <option value="auto">Auto (System)</option>
              </select>
            </div>
          </div>
        </div>

        {/* Notification Settings */}
        <div className="card">
          <div className="card-header">
            <div className="flex items-center">
              <Bell className="w-5 h-5 mr-2 text-gray-500" />
              <h3 className="card-title">Notifications</h3>
            </div>
          </div>
          <div className="card-body space-y-4">
            {/* Enable Notifications */}
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-900">Enable Notifications</p>
                <p className="text-xs text-gray-500">Receive browser notifications</p>
              </div>
              <button
                onClick={() => updateSettings('enableNotifications', !settings.enableNotifications)}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                  settings.enableNotifications ? 'bg-primary-600' : 'bg-gray-200'
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${
                    settings.enableNotifications ? 'translate-x-6' : 'translate-x-1'
                  }`}
                />
              </button>
            </div>

            {/* Sound Enabled */}
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-900">Sound Alerts</p>
                <p className="text-xs text-gray-500">Play sound for critical alerts</p>
              </div>
              <button
                onClick={() => updateSettings('soundEnabled', !settings.soundEnabled)}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                  settings.soundEnabled ? 'bg-primary-600' : 'bg-gray-200'
                }`}
              >
                <span
                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition ${
                    settings.soundEnabled ? 'translate-x-6' : 'translate-x-1'
                  }`}
                />
              </button>
            </div>
          </div>
        </div>

        {/* Alert Thresholds */}
        <div className="card lg:col-span-2">
          <div className="card-header">
            <div className="flex items-center">
              <Zap className="w-5 h-5 mr-2 text-gray-500" />
              <h3 className="card-title">Alert Thresholds</h3>
            </div>
          </div>
          <div className="card-body">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              {/* CPU Threshold */}
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  CPU Usage (%)
                </label>
                <input
                  type="range"
                  min="50"
                  max="100"
                  value={settings.alertThreshold.cpu}
                  onChange={(e) => updateAlertThreshold('cpu', parseInt(e.target.value))}
                  className="w-full"
                />
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                  <span>50%</span>
                  <span className="font-medium text-primary-600">
                    {settings.alertThreshold.cpu}%
                  </span>
                  <span>100%</span>
                </div>
              </div>

              {/* Memory Threshold */}
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  Memory Usage (%)
                </label>
                <input
                  type="range"
                  min="50"
                  max="100"
                  value={settings.alertThreshold.memory}
                  onChange={(e) => updateAlertThreshold('memory', parseInt(e.target.value))}
                  className="w-full"
                />
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                  <span>50%</span>
                  <span className="font-medium text-primary-600">
                    {settings.alertThreshold.memory}%
                  </span>
                  <span>100%</span>
                </div>
              </div>

              {/* Response Time Threshold */}
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  Response Time (ms)
                </label>
                <input
                  type="range"
                  min="100"
                  max="1000"
                  step="50"
                  value={settings.alertThreshold.responseTime}
                  onChange={(e) => updateAlertThreshold('responseTime', parseInt(e.target.value))}
                  className="w-full"
                />
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                  <span>100ms</span>
                  <span className="font-medium text-primary-600">
                    {settings.alertThreshold.responseTime}ms
                  </span>
                  <span>1000ms</span>
                </div>
              </div>

              {/* Error Rate Threshold */}
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  Error Rate (%)
                </label>
                <input
                  type="range"
                  min="0.1"
                  max="5"
                  step="0.1"
                  value={settings.alertThreshold.errorRate}
                  onChange={(e) => updateAlertThreshold('errorRate', parseFloat(e.target.value))}
                  className="w-full"
                />
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                  <span>0.1%</span>
                  <span className="font-medium text-primary-600">
                    {settings.alertThreshold.errorRate}%
                  </span>
                  <span>5%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* API Configuration */}
        <div className="card lg:col-span-2">
          <div className="card-header">
            <h3 className="card-title">API Configuration</h3>
          </div>
          <div className="card-body">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  API Base URL
                </label>
                <input
                  type="text"
                  defaultValue="http://localhost:8080"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                  placeholder="http://localhost:8080"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-900 mb-2">
                  WebSocket URL
                </label>
                <input
                  type="text"
                  defaultValue="ws://localhost:8080/ws"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                  placeholder="ws://localhost:8080/ws"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
