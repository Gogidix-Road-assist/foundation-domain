import { LucideIcon } from 'lucide-react'

interface MetricCardProps {
  title: string
  value: string
  icon: LucideIcon
  color: 'blue' | 'green' | 'yellow' | 'red' | 'purple' | 'cyan' | 'orange' | 'indigo'
  trend?: string | null
}

const colorClasses = {
  blue: 'bg-blue-50 text-blue-600',
  green: 'bg-green-50 text-green-600',
  yellow: 'bg-yellow-50 text-yellow-600',
  red: 'bg-red-50 text-red-600',
  purple: 'bg-purple-50 text-purple-600',
  cyan: 'bg-cyan-50 text-cyan-600',
  orange: 'bg-orange-50 text-orange-600',
  indigo: 'bg-indigo-50 text-indigo-600',
}

export default function MetricCard({ title, value, icon: Icon, color, trend }: MetricCardProps) {
  return (
    <div className="card hover:shadow-md transition-shadow">
      <div className="card-body">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-gray-500">{title}</p>
            <p className="mt-1 text-2xl font-semibold text-gray-900">{value}</p>
            {trend && (
              <p className="mt-1 text-xs text-gray-500">{trend}</p>
            )}
          </div>
          <div className={`p-3 rounded-lg ${colorClasses[color]}`}>
            <Icon className="w-6 h-6" />
          </div>
        </div>
      </div>
    </div>
  )
}
