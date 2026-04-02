import React, { useState } from 'react';
import './Reports.css';

const Reports = () => {
  const [selectedReport, setSelectedReport] = useState(null);

  const reports = [
    {
      id: 'performance',
      name: 'Performance Report',
      description: 'System performance metrics and trends',
      format: 'PDF',
      schedule: 'Daily',
    },
    {
      id: 'security',
      name: 'Security Audit Report',
      description: 'Security events and vulnerability scan results',
      format: 'PDF',
      schedule: 'Weekly',
    },
    {
      id: 'usage',
      name: 'Usage Statistics',
      description: 'API usage and service consumption data',
      format: 'CSV',
      schedule: 'Monthly',
    },
    {
      id: 'health',
      name: 'Service Health Report',
      description: 'Health check results and uptime statistics',
      format: 'PDF',
      schedule: 'Daily',
    },
  ];

  return (
    <div className="reports">
      <div className="reports-header">
        <h3>Reports</h3>
        <button className="generate-btn">Generate Custom Report</button>
      </div>

      <div className="reports-grid">
        {reports.map((report) => (
          <div key={report.id} className="report-card">
            <div className="report-header">
              <h4>{report.name}</h4>
              <span className="report-format">{report.format}</span>
            </div>
            <p className="report-description">{report.description}</p>
            <div className="report-meta">
              <span className="report-schedule">Schedule: {report.schedule}</span>
            </div>
            <div className="report-actions">
              <button className="report-btn primary">Download</button>
              <button className="report-btn secondary">Schedule</button>
            </div>
          </div>
        ))}
      </div>

      <div className="recent-reports">
        <h4>Recent Reports</h4>
        <table className="reports-table">
          <thead>
            <tr>
              <th>Report Name</th>
              <th>Generated</th>
              <th>Format</th>
              <th>Size</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Performance Report - December 2025</td>
              <td>2025-12-25 10:30 AM</td>
              <td>PDF</td>
              <td>2.4 MB</td>
              <td>
                <button className="table-action-btn">Download</button>
              </td>
            </tr>
            <tr>
              <td>Security Audit Report - Week 51</td>
              <td>2025-12-24 08:00 AM</td>
              <td>PDF</td>
              <td>1.8 MB</td>
              <td>
                <button className="table-action-btn">Download</button>
              </td>
            </tr>
            <tr>
              <td>Usage Statistics - November 2025</td>
              <td>2025-12-01 00:00 AM</td>
              <td>CSV</td>
              <td>856 KB</td>
              <td>
                <button className="table-action-btn">Download</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Reports;
