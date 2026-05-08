import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { websocketService } from '../services/websocket';

interface ServiceRequest {
  id: string;
  customerName: string;
  customerPhone: string;
  status: 'PENDING' | 'MATCHING' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  vehicle: string;
  issue: string;
  location: { address: string; lat: number; lng: number };
  assignedPartnerId?: string;
  assignedPartnerName?: string;
  estimatedArrival?: string;
  createdAt: string;
  matchedAt?: string;
  assignedAt?: string;
}

const RequestsMonitor: React.FC = () => {
  const [requests, setRequests] = useState<ServiceRequest[]>([]);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [priorityFilter, setPriorityFilter] = useState<string>('ALL');

  useEffect(() => {
    fetchRequests();
    const subscription = websocketService.subscribe('/topic/request-updates', (message) => {
      const update = JSON.parse(message.body);
      setRequests((prev) => {
        const index = prev.findIndex((r) => r.id === update.id);
        if (index >= 0) return prev.map((r, i) => (i === index ? { ...r, ...update } : r));
        return [...prev, update];
      });
    });
    return () => subscription.unsubscribe();
  }, []);

  const fetchRequests = async () => {
    try {
      const response = await api.get('/api/monitoring/requests');
      setRequests(response.data);
    } catch (error) {
      console.error('Error fetching requests:', error);
    }
  };

  const filteredRequests = requests.filter((r) => {
    if (statusFilter !== 'ALL' && r.status !== statusFilter) return false;
    if (priorityFilter !== 'ALL' && r.priority !== priorityFilter) return false;
    return true;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'MATCHING': return 'bg-blue-100 text-blue-800';
      case 'ASSIGNED': return 'bg-purple-100 text-purple-800';
      case 'IN_PROGRESS': return 'bg-indigo-100 text-indigo-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'CRITICAL': return 'text-red-600 font-bold';
      case 'HIGH': return 'text-orange-600 font-semibold';
      case 'MEDIUM': return 'text-yellow-600';
      case 'LOW': return 'text-green-600';
      default: return 'text-gray-600';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Requests Monitor</h1>
        <p className="text-gray-600">Track all service requests by status and priority</p>
      </div>

      <div className="p-6">
        <div className="bg-white rounded-lg shadow mb-6 p-4 flex gap-4">
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} className="border rounded px-3 py-2">
            <option value="ALL">All Statuses</option>
            <option value="PENDING">Pending</option>
            <option value="MATCHING">Matching</option>
            <option value="ASSIGNED">Assigned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
          </select>
          <select value={priorityFilter} onChange={(e) => setPriorityFilter(e.target.value)} className="border rounded px-3 py-2">
            <option value="ALL">All Priorities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Pending</p>
            <p className="text-3xl font-bold text-yellow-600">{requests.filter((r) => r.status === 'PENDING').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Matching</p>
            <p className="text-3xl font-bold text-blue-600">{requests.filter((r) => r.status === 'MATCHING').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">In Progress</p>
            <p className="text-3xl font-bold text-indigo-600">{requests.filter((r) => r.status === 'IN_PROGRESS').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Completed Today</p>
            <p className="text-3xl font-bold text-green-600">{requests.filter((r) => r.status === 'COMPLETED' && new Date(r.createdAt).toDateString() === new Date().toDateString()).length}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-4">
            <p className="text-gray-600 text-sm">Critical</p>
            <p className="text-3xl font-bold text-red-600">{requests.filter((r) => r.priority === 'CRITICAL').length}</p>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Request ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Priority</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Vehicle</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Issue</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Assigned Partner</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredRequests.map((request) => (
                <tr key={request.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-mono text-gray-900">{request.id.substring(0, 8)}</td>
                  <td className="px-6 py-4">
                    <div>
                      <div className="text-sm font-medium text-gray-900">{request.customerName}</div>
                      <div className="text-sm text-gray-500">{request.customerPhone}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(request.status)}`}>
                      {request.status}
                    </span>
                  </td>
                  <td className={`px-6 py-4 text-sm ${getPriorityColor(request.priority)}`}>{request.priority}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{request.vehicle}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{request.issue}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{request.assignedPartnerName || 'Unassigned'}</td>
                  <td className="px-6 py-4 text-sm text-gray-500">{new Date(request.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default RequestsMonitor;
