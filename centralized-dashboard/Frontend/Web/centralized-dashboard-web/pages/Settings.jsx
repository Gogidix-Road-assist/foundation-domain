import React from 'react';
import './Settings.css';

const Settings = () => {
  return (
    <div className="settings">
      <h3>Settings</h3>

      <div className="settings-grid">
        <div className="settings-section">
          <h4>General Settings</h4>
          <div className="setting-item">
            <label>Dashboard Name</label>
            <input type="text" defaultValue="Foundation Domain Dashboard" />
          </div>
          <div className="setting-item">
            <label>Refresh Interval</label>
            <select>
              <option>5 seconds</option>
              <option selected>10 seconds</option>
              <option>30 seconds</option>
              <option>1 minute</option>
            </select>
          </div>
          <div className="setting-item">
            <label>Timezone</label>
            <select>
              <option selected>UTC</option>
              <option>GMT</option>
              <option>EST</option>
              <option>PST</option>
            </select>
          </div>
        </div>

        <div className="settings-section">
          <h4>Notification Settings</h4>
          <div className="setting-item checkbox">
            <label>
              <input type="checkbox" defaultChecked />
              Enable email notifications
            </label>
          </div>
          <div className="setting-item checkbox">
            <label>
              <input type="checkbox" defaultChecked />
              Enable push notifications
            </label>
          </div>
          <div className="setting-item checkbox">
            <label>
              <input type="checkbox" />
              Enable SMS alerts for critical issues
            </label>
          </div>
          <div className="setting-item">
            <label>Alert Threshold</label>
            <select>
              <option>Critical only</option>
              <option selected>Warning and Critical</option>
              <option>All alerts</option>
            </select>
          </div>
        </div>

        <div className="settings-section">
          <h4>API Configuration</h4>
          <div className="setting-item">
            <label>API Endpoint</label>
            <input type="text" defaultValue="http://localhost:3000/api" />
          </div>
          <div className="setting-item">
            <label>WebSocket URL</label>
            <input type="text" defaultValue="ws://localhost:3000" />
          </div>
          <div className="setting-item">
            <label>API Key</label>
            <input type="password" defaultValue="********************" />
          </div>
        </div>

        <div className="settings-section">
          <h4>Display Settings</h4>
          <div className="setting-item">
            <label>Theme</label>
            <select>
              <option selected>Light</option>
              <option>Dark</option>
              <option>System Default</option>
            </select>
          </div>
          <div className="setting-item">
            <label>Charts Default Period</label>
            <select>
              <option>Last 24 hours</option>
              <option selected>Last 7 days</option>
              <option>Last 30 days</option>
              <option>Custom</option>
            </select>
          </div>
          <div className="setting-item checkbox">
            <label>
              <input type="checkbox" defaultChecked />
              Show legends on charts
            </label>
          </div>
          <div className="setting-item checkbox">
            <label>
              <input type="checkbox" defaultChecked />
              Enable animations
            </label>
          </div>
        </div>
      </div>

      <div className="settings-actions">
        <button className="save-btn">Save Changes</button>
        <button className="reset-btn">Reset to Defaults</button>
      </div>
    </div>
  );
};

export default Settings;
