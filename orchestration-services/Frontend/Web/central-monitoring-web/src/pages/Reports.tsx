import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';

interface ReportMetrics {
  totalRequests: number;
  completedRequests: number;
  averageResponseTime: number;
  slaBreachRate: number;
  partnerUtilization: number;
  customerSatisfaction: number;
}

interface TimeSeriesData {
  timestamp: string;
  requests: number;
  completions: number;
  avgResponseTime: number;
}

interface PartnerPerformance {
  name: string;
  completedJobs: number;
  rating: number;
  averageResponseTime: number;
}

const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6'];

const Reports: React.FC = () => {
  const [metrics, setMetrics] = useState<ReportMetrics | null>(null);
  const [timeSeriesData, setTimeSeriesData] = useState<TimeSeriesData[]>([]);
  const [partnerPerformance, setPartnerPerformance] = useState<PartnerPerformance[]>([]);
  const [period, setPeriod] = useState<'24h' | '7d' | '30d'>('7d');

  useEffect(() => {
    fetchReports();
  }, [period]);

  const fetchReports = async () => {
    try {
      const [metricsRes, timeSeriesRes, partnerRes] = await Promise.all([
        api.get(`/api/reporting/metrics?period=${period}`),
        api.get(`/api/reporting/timeseries?period=${period}`),
        api.get('/api/reporting/partner-performance'),
      ]);
      setMetrics(metricsRes.data);
      setTimeSeriesData(timeSeriesRes.data);
      setPartnerPerformance(partnerRes.data);
    } catch (error) {
      console.error('Error fetching reports:', error);
    }
  };

  const categoryData = metrics ? [
    { name: 'Completed', value: metrics.completedRequests },
    { name: 'Pending', value: metrics.totalRequests - metrics.completedRequests },
  ] : [];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Performance Reports</h1>
        <p className="text-gray-600">Operational analytics and utilization reports</p>
      </div>

      <div className="p-6">
        <div className="bg-white rounded-lg shadow mb-6 p-4 flex justify-between items-center">
          <select
            value={period}
            onChange={(e) => setPeriod(e.target.value as any)}
            className="border rounded px-3 py-2"
          >
            <option value="24h">Last 24 Hours</option>
            <option value="7d">Last 7 Days</option>
            <option value="30d">Last 30 Days</option>
          </select>
          <button
            onClick={fetchReports}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            Refresh
          </button>
        </div>

        {metrics && (
          <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-6">
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">Total Requests</p>
              <p className="text-2xl font-bold">{metrics.totalRequests}</p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">Completed</p>
              <p className="text-2xl font-bold text-green-600">{metrics.completedRequests}</p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">Avg Response Time</p>
              <p className="text-2xl font-bold">{metrics.averageResponseTime.toFixed(1)}m</p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">SLA Breach Rate</p>
              <p className={`text-2xl font-bold ${metrics.slaBreachRate > 5 ? 'text-red-600' : 'text-green-600'}`}>
                {metrics.slaBreachRate.toFixed(1)}%
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">Partner Utilization</p>
              <p className="text-2xl font-bold text-blue-600">{metrics.partnerUtilization.toFixed(0)}%</p>
            </div>
            <div className="bg-white rounded-lg shadow p-4">
              <p className="text-gray-600 text-sm">Customer Satisfaction</p>
              <p className="text-2xl font-bold text-yellow-600">{metrics.customerSatisfaction.toFixed(1)} ★</p>
            </div>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4">Request Volume Over Time</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={timeSeriesData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="requests" stroke="#3b82f6" name="Requests" />
                <Line type="monotone" dataKey="completions" stroke="#10b981" name="Completions" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4">Response Time Trend</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={timeSeriesData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="timestamp" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="avgResponseTime" stroke="#f59e0b" name="Avg Response (min)" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4">Request Status</h2>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie data={categoryData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                  {categoryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4">Top Partner Performance</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={partnerPerformance.slice(0, 5)}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="completedJobs" fill="#3b82f6" name="Jobs Completed" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="mt-6 bg-white rounded-lg shadow overflow-hidden">
          <div className="px-6 py-4 border-b">
            <h2 className="text-lg font-semibold">All Partners Performance</h2>
          </div>
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Partner</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Completed Jobs</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rating</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Avg Response Time</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {partnerPerformance.map((partner, index) => (
                <tr key={index} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-gray-900">{partner.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.completedJobs}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.rating.toFixed(1)} ★</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{partner.averageResponseTime.toFixed(1)}m</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Reports;
