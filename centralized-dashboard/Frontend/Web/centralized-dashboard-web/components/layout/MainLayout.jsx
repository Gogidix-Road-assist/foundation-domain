import React, { useState } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import './MainLayout.css';

const MainLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();

  const menuItems = [
    { path: '/dashboard', icon: '📊', label: 'Dashboard' },
    { path: '/services', icon: '⚙️', label: 'Service Health' },
    { path: '/analytics', icon: '📈', label: 'Analytics' },
    { path: '/reports', icon: '📄', label: 'Reports' },
    { path: '/settings', icon: '⚡', label: 'Settings' },
  ];

  return (
    <div className="main-layout">
      <aside className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
        <div className="sidebar-header">
          <h1 className="logo">Foundation Domain</h1>
          <button className="collapse-btn" onClick={() => setCollapsed(!collapsed)}>
            {collapsed ? '→' : '←'}
          </button>
        </div>
        <nav className="sidebar-nav">
          {menuItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
            >
              <span className="nav-icon">{item.icon}</span>
              {!collapsed && <span className="nav-label">{item.label}</span>}
            </Link>
          ))}
        </nav>
      </aside>

      <main className="main-content">
        <header className="header">
          <div className="header-left">
            <h2>{menuItems.find((i) => i.path === location.pathname)?.label || 'Dashboard'}</h2>
          </div>
          <div className="header-right">
            <button className="notification-btn">🔔</button>
            <div className="user-profile">Admin</div>
          </div>
        </header>

        <div className="content">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;