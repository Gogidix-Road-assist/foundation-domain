import { useState } from 'react'
import { useServices } from '../contexts/ServicesContext'
import {
  LineChart,
  Line,
  BarChart,
  Bar,
  AreaChart,
  Area,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

export default function MetricsPage() {
  const { services } = useServices()
  const [timeRange, setTimeRange] = useState<'1h' | '24h' | '7d' | '30d'>('24h')
  const [selectedMetric, setSelectedMetric] = useState<'responseTime' | 'requests' | 'errors' | 'cpu'>('responseTime')

  // Prepare chart data
  const responseTimeData = services.map((s) => ({
    name: s.name.substring(0, 20),
    value: s.metrics.avgResponseTime,
  }))

  const requestCountData = services
    .sort((a, b) => b.metrics.requestCount - a.metrics.requestCount)
    .slice(0, 10)
    .map((s) => ({
      name: s.name.substring(0, 15),
      requests: s.metrics.requestCount,
    }))

  const cpuUsageData = services
    .sort((a, b) => b.metrics.cpuUsage - a.metrics.cpuUsage)
    .slice(0, 10)
    .map((s) => ({
      name: s.name.substring(0, 15),
      cpu: s.metrics.cpuUsage,
    }))

  const statusDistribution = [
    { name: 'Healthy', value: services.filter((s) => s.status === 'healthy').length, color: '#22c55e' },
    { name: 'Degraded', value: services.filter((s) => s.status === 'degraded').length, color: '#eab308' },
    { name: 'Down', value: services.filter((s) => s.status === 'down').length, color: '#ef4444' },
  ]

  // Generate time series data
  const generateTimeSeriesData = () => {
    const now = new Date()
    const data = []
    for (let i = 23; i >= 0; i--) {
      const hour = new Date(now.getTime() - i * 60 * 60 * 1000)
      data.push({
        time: hour.getHours() + ':00',
        requests: Math.floor(Math.random() * 50000) + 30000,
        errors: Math.floor(Math.random() * 500) + 100,
        avgResponseTime: Math.floor(Math.random() * 200) + 100,
      })
    }
    return data
  }

  const timeSeriesData = generateTimeSeriesData()

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Metrics & Analytics</h1>
          <p className="mt-1 text-sm text-gray-500">
            Detailed performance metrics for all AI services
          </p>
        </div>

        {/* Time Range Selector */}
        <div className="flex items-center space-x-2">
          {(['1h', '24h', '7d', '30d'] as const).map((range) => (
            <button
              key={range}
              onClick={() => setTimeRange(range)}
              className={`px-3 py-1.5 text-sm font-medium rounded-lg transition-colors ${
                timeRange === range
                  ? 'bg-primary-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {range}
            </button>
          ))}
        </div>
      </div>

      {/* Time Series Chart */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Request Volume & Response Time (24h)</h3>
        </div>
        <div className="card-body">
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={timeSeriesData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="time" />
              <YAxis yAxisId="left" />
              <YAxis yAxisId="right" orientation="right" />
              <Tooltip />
              <Legend />
              <Area
                yAxisId="left"
                type="monotone"
                dataKey="requests"
                stroke="#0ea5e9"
                fill="#0ea5e9"
                fillOpacity={0.2}
                name="Requests"
              />
              <Line
                yAxisId="right"
                type="monotone"
                dataKey="avgResponseTime"
                stroke="#8b5cf6"
                strokeWidth={2}
                name="Avg Response Time (ms)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Response Time by Service */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Average Response Time (ms)</h3>
          </div>
          <div className="card-body">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={responseTimeData.slice(0, 10)}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
                <YAxis />
                <Tooltip />
                <Bar dataKey="value" fill="#0ea5e9" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Request Count by Service */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Top 10 Services by Request Count</h3>
          </div>
          <div className="card-body">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={requestCountData} layout="horizontal">
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis dataKey="name" type="category" width={120} />
                <Tooltip />
                <Bar dataKey="requests" fill="#8b5cf6" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* More Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* CPU Usage */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">CPU Usage (%)</h3>
          </div>
          <div className="card-body">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={cpuUsageData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" angle={-45} textAnchor="end" height={80} />
                <YAxis domain={[0, 100]} />
                <Tooltip />
                <Bar dataKey="cpu" fill="#f59e0b">
                  {cpuUsageData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={entry.cpu > 80 ? '#ef4444' : entry.cpu > 60 ? '#f59e0b' : '#22c55e'}
                    />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Status Distribution */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Service Status Distribution</h3>
          </div>
          <div className="card-body">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={statusDistribution}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {statusDistribution.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Error Rate Over Time */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Error Rate Over Time (24h)</h3>
        </div>
        <div className="card-body">
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={timeSeriesData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="time" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="errors" stroke="#ef4444" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
