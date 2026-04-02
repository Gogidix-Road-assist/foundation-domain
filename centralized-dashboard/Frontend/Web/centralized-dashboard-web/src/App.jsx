import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import DashboardConfig from './pages/DashboardConfig';
import Analytics from './pages/Analytics';
import Reports from './pages/Reports';
import Settings from './pages/Settings';
import { useStore } from './store/useStore';
import './styles/main.css';

function App() {
  const isAuthenticated = useStore((state) => state.isAuthenticated);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/dashboard" replace />} />
        <Route path="/" element={<Navigate to={isAuthenticated ? "/dashboard" : "/login"} replace />} />
        <Route path="/dashboard" element={isAuthenticated ? <Dashboard /> : <Navigate to="/login" replace />} />
        <Route path="/dashboards/configure" element={isAuthenticated ? <DashboardConfig /> : <Navigate to="/login" replace />} />
        <Route path="/analytics" element={isAuthenticated ? <Analytics /> : <Navigate to="/login" replace />} />
        <Route path="/reports" element={isAuthenticated ? <Reports /> : <Navigate to="/login" replace />} />
        <Route path="/settings" element={isAuthenticated ? <Settings /> : <Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
