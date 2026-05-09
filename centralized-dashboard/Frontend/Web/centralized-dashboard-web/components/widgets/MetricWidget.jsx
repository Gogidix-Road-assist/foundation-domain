import React from 'react';
import './MetricWidget.css';

const MetricWidget = ({ title, value, change, positive, icon }) => {
  return (
    <div className="metric-widget">
      <div className="metric-header">
        <h3 className="metric-title">{title}</h3>
        {icon && <span className="metric-icon">{icon}</span>}
      </div>
      <div className="metric-value">{value}</div>
      {change && (
        <div className={`metric-change ${positive ? 'positive' : 'negative'}`}>
          {positive ? '↑' : '↓'} {change}
        </div>
      )}
    </div>
  );
};

export default MetricWidget;
