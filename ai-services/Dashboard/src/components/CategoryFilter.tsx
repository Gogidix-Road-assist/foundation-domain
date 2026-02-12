interface CategoryFilterProps {
  selected: string
  onChange: (value: string) => void
}

export default function CategoryFilter({ selected, onChange }: CategoryFilterProps) {
  const categories = [
    { value: 'all', label: 'All Categories' },
    { value: 'core', label: 'Core AI' },
    { value: 'business-intelligence', label: 'Business Intelligence' },
    { value: 'business-operations', label: 'Business Operations' },
  ]

  return (
    <select
      value={selected}
      onChange={(e) => onChange(e.target.value)}
      className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
    >
      {categories.map((category) => (
        <option key={category.value} value={category.value}>
          {category.label}
        </option>
      ))}
    </select>
  )
}
