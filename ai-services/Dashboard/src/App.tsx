import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Sidebar from './components/Sidebar'
import Header from './components/Header'
import DashboardOverview from './pages/DashboardOverview'
import ServicesPage from './pages/ServicesPage'
import MetricsPage from './pages/MetricsPage'
import AlertsPage from './pages/AlertsPage'
import SettingsPage from './pages/SettingsPage'
import { ServicesProvider } from './contexts/ServicesContext'
import { WebSocketProvider } from './contexts/WebSocketContext'

function App() {
  const [sidebarOpen, setSidebarOpen] = useState(true)

  return (
    <Router>
      <WebSocketProvider>
        <ServicesProvider>
          <div className="min-h-screen bg-gray-50">
            <Header onMenuClick={() => setSidebarOpen(!sidebarOpen)} />

            <div className="flex">
              <Sidebar isOpen={sidebarOpen} />

              <main
                className={`flex-1 p-6 transition-all duration-300 ${
                  sidebarOpen ? 'ml-64' : 'ml-0'
                }`}
              >
                <Routes>
                  <Route path="/" element={<DashboardOverview />} />
                  <Route path="/services" element={<ServicesPage />} />
                  <Route path="/metrics" element={<MetricsPage />} />
                  <Route path="/alerts" element={<AlertsPage />} />
                  <Route path="/settings" element={<SettingsPage />} />
                </Routes>
              </main>
            </div>
          </div>
        </ServicesProvider>
      </WebSocketProvider>
    </Router>
  )
}

export default App