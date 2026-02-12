import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts'
import { AIService } from '../data/services'

interface ServiceStatusChartProps {
  services: AIService[]
}

export default function ServiceStatusChart({ services }: ServiceStatusChartProps) {
  const healthy = services.filter(s => s.status === 'healthy').length
  const degraded = services.filter(s => s.status === 'degraded').length
  const down = services.filter(s => s.status === 'down').length

  const data = [
    { name: 'Healthy', value: healthy, color: '#22c55e' },
    { name: 'Degraded', value: degraded, color: '#eab308' },
    { name: 'Down', value: down, color: '#ef4444' },
  ]

  return (
    <div className="card">
      <div className="card-header">
        <h3 className="card-title">Service Status Overview</h3>
      </div>
      <div className="card-body">
        <ResponsiveContainer width="100%" height={250}>
          <PieChart>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={80}
              paddingAngle={2}
              dataKey="value"
            >
              {data.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip />
            <Legend />
          </PieChart>
        </ResponsiveContainer>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mt-4">
          <div className="text-center">
            <p className="text-2xl font-semibold text-green-600">{healthy}</p>
            <p className="text-xs text-gray-500">Healthy</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-semibold text-yellow-600">{degraded}</p>
            <p className="text-xs text-gray-500">Degraded</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-semibold text-red-600">{down}</p>
            <p className="text-xs text-gray-500">Down</p>
          </div>
        </div>
      </div>
    </div>
  )
}
