import { AIService } from '../data/services'
import { CATEGORY_COLORS } from '../data/services'
import {
  CheckCircle2,
  XCircle,
  AlertTriangle,
  Activity,
  Cpu,
  HardDrive,
  Zap,
  ExternalLink,
} from 'lucide-react'

interface ServiceCardProps {
  service: AIService
}

export default function ServiceCard({ service }: ServiceCardProps) {
  const getStatusIcon = () => {
    switch (service.status) {
      case 'healthy':
        return <CheckCircle2 className="w-5 h-5 text-green-500" />
      case 'degraded':
        return <AlertTriangle className="w-5 h-5 text-yellow-500" />
      case 'down':
        return <XCircle className="w-5 h-5 text-red-500" />
    }
  }

  const getStatusBadge = () => {
    switch (service.status) {
      case 'healthy':
        return 'badge-success'
      case 'degraded':
        return 'badge-warning'
      case 'down':
        return 'badge-danger'
    }
  }

  return (
    <div className="card hover:shadow-lg transition-all cursor-pointer">
      <div className="card-body">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center space-x-2">
            {getStatusIcon()}
            <div>
              <h3 className="text-sm font-semibold text-gray-900">
                {service.name}
              </h3>
              <span className={`badge ${getStatusBadge()} mt-1`}>
                {service.status}
              </span>
            </div>
          </div>
          <button className="p-1 rounded hover:bg-gray-100">
            <ExternalLink className="w-4 h-4 text-gray-400" />
          </button>
        </div>

        {/* Category */}
        <p className="text-xs text-gray-500 mb-4">{service.description}</p>

        {/* Metrics */}
        <div className="grid grid-cols-2 gap-2">
          <div className="flex items-center space-x-2">
            <Activity className="w-3 h-3 text-gray-400" />
            <span className="text-xs text-gray-600">
              {service.metrics.avgResponseTime}ms
            </span>
          </div>
          <div className="flex items-center space-x-2">
            <Zap className="w-3 h-3 text-gray-400" />
            <span className="text-xs text-gray-600">
              {service.metrics.cpuUsage}%
            </span>
          </div>
          <div className="flex items-center space-x-2">
            <HardDrive className="w-3 h-3 text-gray-400" />
            <span className="text-xs text-gray-600">
              {service.metrics.memoryUsage}MB
            </span>
          </div>
          <div className="flex items-center space-x-2">
            <Cpu className="w-3 h-3 text-gray-400" />
            <span className="text-xs text-gray-600">
              {service.metrics.errorRate}%
            </span>
          </div>
        </div>

        {/* Progress Bars */}
        <div className="mt-4 space-y-2">
          <div>
            <div className="flex justify-between text-xs text-gray-500 mb-1">
              <span>CPU</span>
              <span>{service.metrics.cpuUsage}%</span>
            </div>
            <div className="h-1.5 bg-gray-200 rounded-full overflow-hidden">
              <div
                className={`h-full rounded-full ${
                  service.metrics.cpuUsage > 80
                    ? 'bg-red-500'
                    : service.metrics.cpuUsage > 60
                    ? 'bg-yellow-500'
                    : 'bg-green-500'
                }`}
                style={{ width: `${service.metrics.cpuUsage}%` }}
              />
            </div>
          </div>
          <div>
            <div className="flex justify-between text-xs text-gray-500 mb-1">
              <span>Memory</span>
              <span>{service.metrics.memoryUsage}MB</span>
            </div>
            <div className="h-1.5 bg-gray-200 rounded-full overflow-hidden">
              <div
                className={`h-full rounded-full ${
                  service.metrics.memoryUsage > 1024
                    ? 'bg-red-500'
                    : service.metrics.memoryUsage > 768
                    ? 'bg-yellow-500'
                    : 'bg-green-500'
                }`}
                style={{ width: `${Math.min(service.metrics.memoryUsage / 2048 * 100, 100)}%` }}
              />
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="mt-4 pt-3 border-t border-gray-100 flex items-center justify-between">
          <span className="text-xs text-gray-400">
            Port: {service.port}
          </span>
          <span className="text-xs text-gray-400">
            {service.metrics.uptime.toFixed(1)}% uptime
          </span>
        </div>
      </div>
    </div>
  )
}
