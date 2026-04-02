import React, { useState, useEffect } from 'react';
import './ServiceHealth.css';

const ServiceHealth = () => {
  const [services, setServices] = useState([]);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    // Sample service data
    setServices([
      {
        id: 'api-gateway',
        name: 'API Gateway',
        status: 'HEALTHY',
        uptime: '99.9%',
        requests: 45231,
        errors: 0,
        lastCheck: new Date().toISOString(),
      },
      {
        id: 'auth-service',
        name: 'Authentication Service',
        status: 'HEALTHY',
        uptime: '99.8%',
        requests: 12543,
        errors: 2,
        lastCheck: new Date().toISOString(),
      },
      {
        id: 'config-service',
        name: 'Configuration Service',
        status: 'HEALTHY',
        uptime: '100%',
        requests: 3245,
        errors: 0,
        lastCheck: new Date().toISOString(),
      },
      {
        id: 'discovery-service',
        name: 'Service Discovery',
        status: 'WARNING',
        uptime: '98.5%',
        requests: 8765,
        errors: 15,
        lastCheck: new Date().toISOString(),
      },
      {
        id: 'metrics-service',
        name: 'Metrics Collector',
        status: 'HEALTHY',
        uptime: '99.7%',
        requests: 23456,
        errors: 1,
        lastCheck: new Date().toISOString(),
      },
      {
        id: 'alert-service',
        name: 'Alert Service',
        status: 'HEALTHY',
        uptime: '99.9%',
        requests: 5678,
        errors: 0,
        lastCheck: new Date().toISOString(),
      },
    ]);
  }, []);

  const filteredServices = services.filter((service) =>
    filter === 'ALL' ? true : service.status === filter
  );

  const statusCounts = {
    HEALTHY: services.filter((s) => s.status === 'HEALTHY').length,
    WARNING: services.filter((s) => s.status === 'WARNING').length,
    CRITICAL: services.filter((s) => s.status === 'CRITICAL').length,
  };

  return (
    <div className="service-health">
      <div className="health-header">
        <h3>Service Health Monitoring</h3>
        <div className="health-filters">
          <button
            className={`filter-btn ${filter === 'ALL' ? 'active' : ''}`}
            onClick={() => setFilter('ALL')}
          >
            All ({services.length})
          </button>
          <button
            className={`filter-btn ${filter === 'HEALTHY' ? 'active' : ''}`}
            onClick={() => setFilter('HEALTHY')}
          >
            Healthy ({statusCounts.HEALTHY})
          </button>
          <button
            className={`filter-btn ${filter === 'WARNING' ? 'active' : ''}`}
            onClick={() => setFilter('WARNING')}
          >
            Warning ({statusCounts.WARNING})
          </button>
          <button
            className={`filter-btn ${filter === 'CRITICAL' ? 'active' : ''}`}
            onClick={() => setFilter('CRITICAL')}
          >
            Critical ({statusCounts.CRITICAL})
          </button>
        </div>
      </div>

      <div className="health-summary">
        <div className="summary-card healthy">
          <div className="summary-value">{statusCounts.HEALTHY}</div>
          <div className="summary-label">Healthy</div>
        </div>
        <div className="summary-card warning">
          <div className="summary-value">{statusCounts.WARNING}</div>
          <div className="summary-label">Warning</div>
        </div>
        <div className="summary-card critical">
          <div className="summary-value">{statusCounts.CRITICAL}</div>
          <div className="summary-label">Critical</div>
        </div>
        <div className="summary-card">
          <div className="summary-value">{services.length}</div>
          <div className="summary-label">Total Services</div>
        </div>
      </div>

      <div className="services-table">
        <table>
          <thead>
            <tr>
              <th>Service Name</th>
              <th>Status</th>
              <th>Uptime</th>
              <th>Requests</th>
              <th>Errors</th>
              <th>Last Check</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredServices.map((service) => (
              <tr key={service.id}>
                <td>{service.name}</td>
                <td>
                  <span className={`status-badge ${service.status.toLowerCase()}`}>
                    {service.status}
                  </span>
                </td>
                <td>{service.uptime}</td>
                <td>{service.requests.toLocaleString()}</td>
                <td>{service.errors}</td>
                <td>{new Date(service.lastCheck).toLocaleString()}</td>
                <td>
                  <button className="action-btn">View</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ServiceHealth;
