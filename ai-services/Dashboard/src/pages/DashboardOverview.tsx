import { useServices } from '../contexts/ServicesContext'
import { useWebSocket } from '../contexts/WebSocketContext'
import { format } from 'date-fns'
import {
  Activity,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  TrendingUp,
  TrendingDown,
  Cpu,
  HardDrive,
  Zap,
} from 'lucide-react'
import MetricCard from '../components/MetricCard'
import ServiceStatusChart from '../components/ServiceStatusChart'
import RecentAlerts from '../components/RecentAlerts'

export default function DashboardOverview() {
  const { services, getHealthyServices, getDegradedServices, getDownServices } = useServices()
  const { connected, lastMessage } = useWebSocket()

  const healthyServices = getHealthyServices()
  const degradedServices = getDegradedServices()
  const downServices = getDownServices()

  // Calculate overall metrics
  const totalRequests = services.reduce((sum, s) => sum + s.metrics.requestCount, 0)
  const avgResponseTime = services.reduce((sum, s) => sum + s.metrics.avgResponseTime, 0) / services.length
  const avgCpuUsage = services.reduce((sum, s) => sum + s.metrics.cpuUsage, 0) / services.length
  const avgMemoryUsage = services.reduce((sum, s) => sum + s.metrics.memoryUsage, 0) / services.length
  const avgErrorRate = services.reduce((sum, s) => sum + s.metrics.errorRate, 0) / services.length

  const metrics = [
    {
      title: 'Total Services',
      value: services.length.toString(),
      icon: Cpu,
      color: 'blue',
      trend: null,
    },
    {
      title: 'Healthy Services',
      value: healthyServices.length.toString(),
      icon: CheckCircle2,
      color: 'green',
      trend: '+2 from yesterday',
    },
    {
      title: 'Degraded Services',
      value: degradedServices.length.toString(),
      icon: AlertTriangle,
      color: 'yellow',
      trend: degradedServices.length > 0 ? 'Needs attention' : null,
    },
    {
      title: 'Down Services',
      value: downServices.length.toString(),
      icon: XCircle,
      color: 'red',
      trend: downServices.length > 0 ? 'Critical' : null,
    },
    {
      title: 'Total Requests',
      value: totalRequests.toLocaleString(),
      icon: Activity,
      color: 'purple',
      trend: '+12.5% from last hour',
    },
    {
      title: 'Avg Response Time',
      value: `${Math.round(avgResponseTime)}ms`,
      icon: Zap,
      color: 'cyan',
      trend: avgResponseTime < 200 ? 'Good' : 'Needs optimization',
    },
    {
      title: 'Avg CPU Usage',
      value: `${Math.round(avgCpuUsage)}%`,
      icon: TrendingUp,
      color: 'orange',
      trend: avgCpuUsage < 70 ? 'Normal' : 'High',
    },
    {
      title: 'Avg Memory Usage',
      value: `${Math.round(avgMemoryUsage)}MB`,
      icon: HardDrive,
      color: 'indigo',
      trend: 'Stable',
    },
  ]

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">AI Services Dashboard</h1>
          <p className="mt-1 text-sm text-gray-500">
            Real-time monitoring of all 27 AI services
          </p>
        </div>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <span>Last updated: {format(new Date(), 'HH:mm:ss')}</span>
          {connected && (
            <span className="flex items-center text-green-600">
              <span className="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse" />
              Live
            </span>
          )}
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {metrics.map((metric) => (
          <MetricCard key={metric.title} {...metric} />
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <ServiceStatusChart services={services} />
        <RecentAlerts />
      </div>

      {/* Services by Category */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Core AI Services */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Core AI Services</h3>
          </div>
          <div className="card-body">
            <div className="space-y-3">
              {services.filter(s => s.category === 'core').slice(0, 5).map((service) => (
                <div key={service.id} className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div className={`w-2 h-2 rounded-full mr-2 ${
                      service.status === 'healthy' ? 'bg-green-500' :
                      service.status === 'degraded' ? 'bg-yellow-500' : 'bg-red-500'
                    }`} />
                    <span className="text-sm text-gray-700">{service.name}</span>
                  </div>
                  <span className="text-xs text-gray-500">{service.metrics.avgResponseTime}ms</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Business Intelligence Services */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Business Intelligence</h3>
          </div>
          <div className="card-body">
            <div className="space-y-3">
              {services.filter(s => s.category === 'business-intelligence').slice(0, 5).map((service) => (
                <div key={service.id} className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div className={`w-2 h-2 rounded-full mr-2 ${
                      service.status === 'healthy' ? 'bg-green-500' :
                      service.status === 'degraded' ? 'bg-yellow-500' : 'bg-red-500'
                    }`} />
                    <span className="text-sm text-gray-700">{service.name}</span>
                  </div>
                  <span className="text-xs text-gray-500">{service.metrics.avgResponseTime}ms</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Business Operations Services */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Business Operations</h3>
          </div>
          <div className="card-body">
            <div className="space-y-3">
              {services.filter(s => s.category === 'business-operations').slice(0, 5).map((service) => (
                <div key={service.id} className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div className={`w-2 h-2 rounded-full mr-2 ${
                      service.status === 'healthy' ? 'bg-green-500' :
                      service.status === 'degraded' ? 'bg-yellow-500' : 'bg-red-500'
                    }`} />
                    <span className="text-sm text-gray-700">{service.name}</span>
                  </div>
                  <span className="text-xs text-gray-500">{service.metrics.avgResponseTime}ms</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Real-time Updates */}
      {lastMessage && (
        <div className="card animate-slide-in">
          <div className="card-body">
            <div className="flex items-center">
              <Activity className="w-5 h-5 text-blue-500 mr-3" />
              <div className="flex-1">
                <p className="text-sm font-medium text-gray-900">
                  Real-time Update: {lastMessage.type}
                </p>
                <p className="text-xs text-gray-500">{JSON.stringify(lastMessage.data)}</p>
              </div>
              <span className="text-xs text-gray-400">
                {format(new Date(), 'HH:mm:ss')}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
