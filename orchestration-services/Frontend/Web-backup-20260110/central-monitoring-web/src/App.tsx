/**
 * App Component - Main Router
 */

import { Routes, Route, Navigate } from 'react-router-dom';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/live-map" element={<Dashboard />} />
      <Route path="/partners" element={<Dashboard />} />
      <Route path="/requests" element={<Dashboard />} />
      <Route path="/dispatch" element={<Dashboard />} />
      <Route path="/alerts" element={<Dashboard />} />
      <Route path="/reports" element={<Dashboard />} />
    </Routes>
  );
}

export default App;
