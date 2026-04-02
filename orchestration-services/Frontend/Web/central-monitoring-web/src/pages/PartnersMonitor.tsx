import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { websocketService } from '../services/websocket';

interface Partner {
  id: string;
  name: string;
  type: 'TOWING' | 'MECHANIC' | 'INDEPENDENT';
  status: 'AVAILABLE' | 'BUSY' | 'OFFLINE';
  rating: number;
  completedJobs: number;
  currentJobId?: string;
  location: { lat: number; lng: number };
  specialties: string[];
  responseTime: number;
  phone: string;
  lastActive: string;
}

const PartnersMonitor: React.FC = () => {
  const [partners, setPartners] = useState<Partner[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'TOWING' | 'MECHANIC' | 'INDEPENDENT'>('ALL');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'AVAILABLE' | 'BUSY' | 'OFFLINE'>('ALL');
  const [search, setSearch] = useState('');

  useEffect(() => {
    fetchPartners();
    const subscription = websocketService.subscribe('/topic/partner-updates', (message) => {
      const update = JSON.parse(message.body);
      setPartners((prev) => prev.map((p) => (p.id === update.id ? { ...p, ...update } : p)));
    });
    return () => subscription.unsubscribe();
  }, []);

  const fetchPartners = async () => {
    try {
      const response = await api.get('/api/monitoring/partners');
      setPartners(response.data);
    } catch (error) {
      console.error('Error fetching partners:', error);
    }
  };

  const filteredPartners = partners.filter((p) => {
    if (filter !== 'ALL' && p.type !== filter) return false;
    if (statusFilter !== 'ALL' && p.status !== statusFilter) return false;
    if (search && !p.name.toLowerCase().includes(search.toLowerCase())) return false;
    return true;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return 'bg-green-100 text-green-800';
      case 'BUSY': return 'bg-orange-100 text-orange-800';
      case 'OFFLINE': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Partners Monitor</h1>
        <p className="text-gray-600">View and manage all towing companies, mechanics, and independent drivers</p>
      </div>

      <div className="p-6">
        <div className="bg-white rounded-lg shadow mb-6 p-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <input
              type="text"
              placeholder="Search partners..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="border rounded px-3 py-2"
            />
            <select value={filter} onChange={(e) => setFilter(e.target.value as any)} className="border rounded px-3 py-2">
              <option value="ALL">All Types</option>
              <option value="TOWING">Towing</option>
              <option value="MECHANIC">Mechanic</option>
              <option value="INDEPENDENT">Independent</option>
            </select>
            <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value as any)} className="border rounded px-3 py-2">
              <option value="ALL">All Statuses</option>
              <option value="AVAILABLE">Available</option>
              <option value="BUSY">Busy</option>
              <option value="OFFLINE">Offline</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Total Partners</p>
            <p className="text-3xl font-bold">{partners.length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Available Now</p>
            <p className="text-3xl font-bold text-green-600">{partners.filter((p) => p.status === 'AVAILABLE').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Currently Busy</p>
            <p className="text-3xl font-bold text-orange-600">{partners.filter((p) => p.status === 'BUSY').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Avg Response Time</p>
            <p className="text-3xl font-bold">{(partners.reduce((a, b) => a + b.responseTime, 0) / partners.length || 0).toFixed(1)}m</p>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Partner</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rating</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Jobs</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Response Time</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Last Active</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredPartners.map((partner) => (
                <tr key={partner.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{partner.name}</div>
                      <div className="text-sm text-gray-500">{partner.phone}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.type}</td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(partner.status)}`}>
                      {partner.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.rating.toFixed(1)} ★</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.completedJobs}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.responseTime}m</td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    {new Date(partner.lastActive).toLocaleString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default PartnersMonitor;
