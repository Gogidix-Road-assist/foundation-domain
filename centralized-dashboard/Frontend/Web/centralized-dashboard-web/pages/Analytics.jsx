import React from 'react';
import './Analytics.css';

const Analytics = () => {
  return (
    <div className="analytics">
      <h3>Analytics Dashboard</h3>

      <div className="analytics-grid">
        <div className="chart-card">
          <h4>Request Trends</h4>
          <div className="chart-placeholder">
            <div className="chart-bars">
              <div className="bar" style={{ height: '60%' }}></div>
              <div className="bar" style={{ height: '80%' }}></div>
              <div className="bar" style={{ height: '45%' }}></div>
              <div className="bar" style={{ height: '90%' }}></div>
              <div className="bar" style={{ height: '70%' }}></div>
              <div className="bar" style={{ height: '85%' }}></div>
              <div className="bar" style={{ height: '95%' }}></div>
            </div>
            <div className="chart-label">
              <span>Mon</span>
              <span>Tue</span>
              <span>Wed</span>
              <span>Thu</span>
              <span>Fri</span>
              <span>Sat</span>
              <span>Sun</span>
            </div>
          </div>
        </div>

        <div className="chart-card">
          <h4>Error Rate</h4>
          <div className="donut-chart">
            <div className="donut-inner">
              <div className="donut-value">0.02%</div>
              <div className="donut-label">Error Rate</div>
            </div>
          </div>
        </div>

        <div className="chart-card">
          <h4>Response Time Distribution</h4>
          <div className="metric-row">
            <div className="metric-box">
              <div className="metric-label">p50</div>
              <div className="metric-number">45ms</div>
            </div>
            <div className="metric-box">
              <div className="metric-label">p95</div>
              <div className="metric-number">120ms</div>
            </div>
            <div className="metric-box">
              <div className="metric-label">p99</div>
              <div className="metric-number">250ms</div>
            </div>
          </div>
        </div>

        <div className="chart-card">
          <h4>Top Services by Requests</h4>
          <div className="service-list">
            <div className="service-list-item">
              <span>API Gateway</span>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: '92%' }}></div>
              </div>
              <span>92K</span>
            </div>
            <div className="service-list-item">
              <span>Auth Service</span>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: '78%' }}></div>
              </div>
              <span>78K</span>
            </div>
            <div className="service-list-item">
              <span>Metrics Service</span>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: '65%' }}></div>
              </div>
              <span>65K</span>
            </div>
            <div className="service-list-item">
              <span>Config Service</span>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: '45%' }}></div>
              </div>
              <span>45K</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
