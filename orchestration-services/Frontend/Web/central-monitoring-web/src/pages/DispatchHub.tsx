import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { websocketService } from '../services/websocket';

interface PendingRequest {
  id: string;
  customerName: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  vehicle: string;
  issue: string;
  location: { address: string; lat: number; lng: number };
  createdAt: string;
}

interface CandidatePartner {
  id: string;
  name: string;
  type: 'TOWING' | 'MECHANIC' | 'INDEPENDENT';
  status: string;
  rating: number;
  distance: number;
  estimatedArrival: number;
  price: number;
  score: number;
}

const DispatchHub: React.FC = () => {
  const [pendingRequests, setPendingRequests] = useState<PendingRequest[]>([]);
  const [selectedRequest, setSelectedRequest] = useState<PendingRequest | null>(null);
  const [candidates, setCandidates] = useState<CandidatePartner[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchPendingRequests();
    const subscription = websocketService.subscribe('/topic/new-requests', (message) => {
      const request = JSON.parse(message.body);
      setPendingRequests((prev) => [...prev, request]);
    });
    return () => subscription.unsubscribe();
  }, []);

  const fetchPendingRequests = async () => {
    try {
      const response = await api.get('/api/dispatching/pending');
      setPendingRequests(response.data);
    } catch (error) {
      console.error('Error fetching pending requests:', error);
    }
  };

  const selectRequest = async (request: PendingRequest) => {
    setSelectedRequest(request);
    setLoading(true);
    try {
      const response = await api.post('/api/matching/find-partners', {
        requestId: request.id,
        location: request.location,
        priority: request.priority,
      });
      setCandidates(response.data);
    } catch (error) {
      console.error('Error finding partners:', error);
    } finally {
      setLoading(false);
    }
  };

  const assignPartner = async (partnerId: string) => {
    if (!selectedRequest) return;
    try {
      await api.post(`/api/dispatching/assign`, {
        requestId: selectedRequest.id,
        partnerId,
      });
      alert('Partner assigned successfully!');
      setPendingRequests((prev) => prev.filter((r) => r.id !== selectedRequest.id));
      setSelectedRequest(null);
      setCandidates([]);
    } catch (error) {
      alert('Error assigning partner');
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'CRITICAL': return 'bg-red-100 border-red-500';
      case 'HIGH': return 'bg-orange-100 border-orange-500';
      case 'MEDIUM': return 'bg-yellow-100 border-yellow-500';
      case 'LOW': return 'bg-green-100 border-green-500';
      default: return 'bg-gray-100';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Dispatch Hub</h1>
        <p className="text-gray-600">Central dispatch coordination - match and assign partners to requests</p>
      </div>

      <div className="p-6">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div>
            <h2 className="text-lg font-semibold mb-4">Pending Requests ({pendingRequests.length})</h2>
            <div className="space-y-4">
              {pendingRequests.map((request) => (
                <div
                  key={request.id}
                  className={`bg-white rounded-lg shadow p-4 border-l-4 cursor-pointer transition hover:shadow-lg ${
                    selectedRequest?.id === request.id ? 'ring-2 ring-blue-500' : ''
                  } ${getPriorityColor(request.priority)}`}
                  onClick={() => selectRequest(request)}
                >
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-semibold">{request.customerName}</h3>
                    <span className={`text-xs px-2 py-1 rounded font-semibold ${
                      request.priority === 'CRITICAL' ? 'bg-red-200 text-red-800' : 'bg-gray-200'
                    }`}>{request.priority}</span>
                  </div>
                  <p className="text-sm text-gray-600 mb-1">{request.vehicle}</p>
                  <p className="text-sm text-gray-700 mb-2">{request.issue}</p>
                  <p className="text-xs text-gray-500">
                    {request.location.address} • {new Date(request.createdAt).toLocaleTimeString()}
                  </p>
                </div>
              ))}
              {pendingRequests.length === 0 && (
                <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                  No pending requests
                </div>
              )}
            </div>
          </div>

          <div>
            {selectedRequest ? (
              <>
                <h2 className="text-lg font-semibold mb-4">Partner Candidates</h2>
                {loading ? (
                  <div className="bg-white rounded-lg shadow p-8 text-center">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
                    <p className="mt-4 text-gray-600">Finding best matches...</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {candidates.map((partner, index) => (
                      <div key={partner.id} className="bg-white rounded-lg shadow p-4">
                        <div className="flex justify-between items-start">
                          <div>
                            <div className="flex items-center gap-2">
                              <span className={`text-xs px-2 py-1 rounded ${index === 0 ? 'bg-green-100 text-green-800' : 'bg-gray-100'}`}>
                                #{index + 1} Match
                              </span>
                              <span className="text-xs text-gray-500">{partner.type}</span>
                            </div>
                            <h3 className="font-semibold mt-1">{partner.name}</h3>
                            <p className="text-sm text-gray-600">Rating: {partner.rating.toFixed(1)} ★</p>
                          </div>
                          <div className="text-right">
                            <p className="text-lg font-bold text-blue-600">{partner.score.toFixed(0)}%</p>
                            <p className="text-xs text-gray-500">Match Score</p>
                          </div>
                        </div>
                        <div className="mt-4 grid grid-cols-3 gap-4 text-sm">
                          <div>
                            <p className="text-gray-500">Distance</p>
                            <p className="font-semibold">{partner.distance.toFixed(1)} km</p>
                          </div>
                          <div>
                            <p className="text-gray-500">ETA</p>
                            <p className="font-semibold">{partner.estimatedArrival} min</p>
                          </div>
                          <div>
                            <p className="text-gray-500">Price</p>
                            <p className="font-semibold">€{partner.price.toFixed(2)}</p>
                          </div>
                        </div>
                        <button
                          onClick={() => assignPartner(partner.id)}
                          className="mt-4 w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
                        >
                          Assign Partner
                        </button>
                      </div>
                    ))}
                    {candidates.length === 0 && (
                      <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                        No partners available
                      </div>
                    )}
                  </div>
                )}
              </>
            ) : (
              <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                Select a request to view partner candidates
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default DispatchHub;
