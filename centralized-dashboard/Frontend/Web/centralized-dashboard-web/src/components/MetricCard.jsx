import './MetricCard.css';

function MetricCard({ title, value, change, trend, icon }) {
  const isPositive = trend === 'up';

  return (
    <div className="metric-card">
      <div className="metric-header">
        <span className="metric-icon">{icon}</span>
        <span className="metric-title">{title}</span>
      </div>
      <div className="metric-value">{value}</div>
      <div className={`metric-change ${isPositive ? 'positive' : 'negative'}`}>
        {isPositive ? '↑' : '↓'} {change}
      </div>
    </div>
  );
}

export default MetricCard;
