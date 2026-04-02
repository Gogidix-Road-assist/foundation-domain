interface StatusFilterProps {
  selected: string
  onChange: (value: string) => void
}

export default function StatusFilter({ selected, onChange }: StatusFilterProps) {
  const statuses = [
    { value: 'all', label: 'All Status' },
    { value: 'healthy', label: 'Healthy' },
    { value: 'degraded', label: 'Degraded' },
    { value: 'down', label: 'Down' },
  ]

  return (
    <select
      value={selected}
      onChange={(e) => onChange(e.target.value)}
      className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
    >
      {statuses.map((status) => (
        <option key={status.value} value={status.value}>
          {status.label}
        </option>
      ))}
    </select>
  )
}
