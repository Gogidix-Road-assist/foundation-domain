import React, { useEffect, useState } from 'react';
import L from 'leaflet';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { websocketService } from '../services/websocket';
import { api } from '../services/api';

// Fix for default marker icons
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

interface PartnerLocation {
  id: string;
  name: string;
  type: 'TOWING' | 'MECHANIC' | 'INDEPENDENT';
  status: 'AVAILABLE' | 'BUSY' | 'OFFLINE';
  location: { lat: number; lng: number };
  currentJobId?: string;
  rating: number;
  completedJobs: number;
}

interface RequestLocation {
  id: string;
  customerName: string;
  status: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  location: { lat: number; lng: number };
  vehicle: string;
  issue: string;
  createdAt: string;
}

const LiveMap: React.FC = () => {
  const [partners, setPartners] = useState<PartnerLocation[]>([]);
  const [requests, setRequests] = useState<RequestLocation[]>([]);
  const [filter, setFilter] = useState<'ALL' | 'TOWING' | 'MECHANIC' | 'INDEPENDENT'>('ALL');
  const [statusFilter, setStatusFilter] = useState<'ALL' | 'AVAILABLE' | 'BUSY'>('ALL');

  useEffect(() => {
    const mapCenter = L.latLng(53.35014, -6.266155); // Dublin

    const fetchLocations = async () => {
      try {
        const [partnersRes, requestsRes] = await Promise.all([
          api.get('/api/monitoring/location/active-partners'),
          api.get('/api/monitoring/matching/active-requests'),
        ]);
        setPartners(partnersRes.data);
        setRequests(requestsRes.data);
      } catch (error) {
        console.error('Error fetching locations:', error);
      }
    };

    fetchLocations();

    // Subscribe to location updates via WebSocket
    const subscription = websocketService.subscribe('/topic/location-updates', (message) => {
      const update = JSON.parse(message.body);
      if (update.type === 'PARTNER') {
        setPartners((prev) =>
          prev.map((p) => (p.id === update.id ? { ...p, location: update.location } : p))
        );
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, []);

  const getPartnerIcon = (type: string, status: string) => {
    const color = status === 'AVAILABLE' ? 'green' : status === 'BUSY' ? 'orange' : 'gray';
    return L.divIcon({
      className: 'custom-marker',
      html: `<div style="background-color: ${color}; width: 24px; height: 24px; border-radius: 50%; border: 3px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"></div>`,
      iconSize: [24, 24],
      iconAnchor: [12, 12],
    });
  };

  const getRequestIcon = (priority: string) => {
    const color = priority === 'CRITICAL' ? 'red' : priority === 'HIGH' ? 'orange' : 'blue';
    return L.divIcon({
      className: 'custom-marker',
      html: `<div style="background-color: ${color}; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"></div>`,
      iconSize: [20, 20],
      iconAnchor: [10, 10],
    });
  };

  const filteredPartners = partners.filter((p) => {
    if (filter !== 'ALL' && p.type !== filter) return false;
    if (statusFilter !== 'ALL' && p.status !== statusFilter) return false;
    return true;
  });

  return (
    <div className="h-screen flex flex-col">
      <div className="bg-white border-b px-6 py-4">
        <h1 className="text-2xl font-bold text-gray-800">Live Map</h1>
        <p className="text-gray-600">Real-time GPS tracking of partners and requests</p>
      </div>

      <div className="flex-1 relative">
        <MapContainer center={[53.35014, -6.266155]} zoom={10} style={{ height: '100%', width: '100%' }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

          {filteredPartners.map((partner) => (
            <Marker
              key={partner.id}
              position={[partner.location.lat, partner.location.lng]}
              icon={getPartnerIcon(partner.type, partner.status)}
            >
              <Popup>
                <div className="p-2">
                  <h3 className="font-bold">{partner.name}</h3>
                  <p className="text-sm text-gray-600">{partner.type}</p>
                  <p className="text-sm">
                    Status:{' '}
                    <span className={`font-semibold ${partner.status === 'AVAILABLE' ? 'text-green-600' : 'text-orange-600'}`}>
                      {partner.status}
                    </span>
                  </p>
                  <p className="text-sm">Rating: {partner.rating.toFixed(1)} ★</p>
                  <p className="text-sm">Jobs Completed: {partner.completedJobs}</p>
                </div>
              </Popup>
            </Marker>
          ))}

          {requests.map((request) => (
            <Marker
              key={request.id}
              position={[request.location.lat, request.location.lng]}
              icon={getRequestIcon(request.priority)}
            >
              <Popup>
                <div className="p-2">
                  <h3 className="font-bold">{request.customerName}</h3>
                  <p className="text-sm text-gray-600">{request.vehicle}</p>
                  <p className="text-sm">Issue: {request.issue}</p>
                  <p className="text-sm">
                    Priority:{' '}
                    <span className={`font-semibold text-${request.priority === 'CRITICAL' ? 'red' : 'orange'}-600`}>
                      {request.priority}
                    </span>
                  </p>
                  <p className="text-xs text-gray-500">
                    {new Date(request.createdAt).toLocaleString()}
                  </p>
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>

        <div className="absolute top-4 left-4 bg-white rounded-lg shadow-lg p-4 w-72">
          <h3 className="font-semibold mb-3">Filters</h3>

          <div className="mb-4">
            <label className="text-sm font-medium text-gray-700 block mb-1">Partner Type</label>
            <select
              value={filter}
              onChange={(e) => setFilter(e.target.value as any)}
              className="w-full border rounded px-3 py-2 text-sm"
            >
              <option value="ALL">All Types</option>
              <option value="TOWING">Towing</option>
              <option value="MECHANIC">Mechanic</option>
              <option value="INDEPENDENT">Independent Driver</option>
            </select>
          </div>

          <div className="mb-4">
            <label className="text-sm font-medium text-gray-700 block mb-1">Status</label>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as any)}
              className="w-full border rounded px-3 py-2 text-sm"
            >
              <option value="ALL">All Statuses</option>
              <option value="AVAILABLE">Available</option>
              <option value="BUSY">Busy</option>
            </select>
          </div>

          <div className="border-t pt-3">
            <h4 className="text-sm font-medium text-gray-700 mb-2">Legend</h4>
            <div className="space-y-1 text-xs">
              <div className="flex items-center">
                <div className="w-3 h-3 rounded-full bg-green-500 mr-2"></div>
                <span>Available Partner</span>
              </div>
              <div className="flex items-center">
                <div className="w-3 h-3 rounded-full bg-orange-500 mr-2"></div>
                <span>Busy Partner</span>
              </div>
              <div className="flex items-center">
                <div className="w-3 h-3 rounded-full bg-red-500 mr-2"></div>
                <span>Critical Request</span>
              </div>
              <div className="flex items-center">
                <div className="w-3 h-3 rounded-full bg-blue-500 mr-2"></div>
                <span>Normal Request</span>
              </div>
            </div>
          </div>

          <div className="border-t pt-3 mt-3">
            <h4 className="text-sm font-medium text-gray-700 mb-2">Statistics</h4>
            <div className="text-xs space-y-1">
              <p>Total Partners: {partners.length}</p>
              <p>Available: {partners.filter((p) => p.status === 'AVAILABLE').length}</p>
              <p>Active Requests: {requests.length}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LiveMap;
