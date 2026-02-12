import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import LiveMap from './pages/LiveMap';
import PartnersMonitor from './pages/PartnersMonitor';
import RequestsMonitor from './pages/RequestsMonitor';
import DispatchHub from './pages/DispatchHub';
import AlertsPanel from './pages/AlertsPanel';
import Reports from './pages/Reports';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-100">
        <nav className="bg-blue-800 text-white p-4 shadow-lg">
          <div className="container mx-auto flex justify-between items-center">
            <h1 className="text-xl font-bold">Gogidix Central Monitoring</h1>
            <div className="flex gap-4 text-sm">
              <a href="/" className="hover:text-blue-200">Dashboard</a>
              <a href="/live-map" className="hover:text-blue-200">Live Map</a>
              <a href="/partners" className="hover:text-blue-200">Partners</a>
              <a href="/requests" className="hover:text-blue-200">Requests</a>
              <a href="/dispatch" className="hover:text-blue-200">Dispatch</a>
              <a href="/alerts" className="hover:text-blue-200">Alerts</a>
              <a href="/reports" className="hover:text-blue-200">Reports</a>
            </div>
          </div>
        </nav>

        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/live-map" element={<LiveMap />} />
          <Route path="/partners" element={<PartnersMonitor />} />
          <Route path="/requests" element={<RequestsMonitor />} />
          <Route path="/dispatch" element={<DispatchHub />} />
          <Route path="/alerts" element={<AlertsPanel />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
