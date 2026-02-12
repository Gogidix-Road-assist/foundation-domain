import './ServiceStatusCard.css';

function ServiceStatusCard({ service }) {
  const statusClass = service.status?.toLowerCase() || 'unknown';
  const statusLabels = {
    healthy: 'Healthy',
    degraded: 'Degraded',
    down: 'Down',
    unknown: 'Unknown',
  };

  return (
    <div className={`service-card service-${statusClass}`}>
      <div className="service-header">
        <h3>{service.name}</h3>
        <span className={`status-badge ${statusClass}`}>
          {statusLabels[statusClass] || 'Unknown'}
        </span>
      </div>
      <div className="service-info">
        <p className="service-url">{service.url || service.host}</p>
        {service.port && <p className="service-port">Port: {service.port}</p>}
      </div>
      {service.lastUpdated && (
        <p className="service-last-updated">
          Last updated: {new Date(service.lastUpdated).toLocaleTimeString()}
        </p>
      )}
      {service.responseTime && (
        <div className="service-metrics">
          <span>Response: {service.responseTime}ms</span>
        </div>
      )}
    </div>
  );
}

export default ServiceStatusCard;
