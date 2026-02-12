/**
 * Live Map Component
 * Displays real-time map with partners and requests
 */

import { FC, useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import type { PartnerLocation, ServiceRequest } from '../types';
import { EntityStatus } from '../types';

// Fix for default marker icons in React
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
});

// Custom icons
const createPartnerIcon = (status: EntityStatus) => {
  const color = status === EntityStatus.AVAILABLE ? '#22c55e' :
                status === EntityStatus.BUSY ? '#ef4444' :
                status === EntityStatus.EN_ROUTE ? '#f59e0b' : '#9ca3af';

  return L.divIcon({
    className: 'custom-marker',
    html: `<div style="
      width: 32px;
      height: 32px;
      background-color: ${color};
      border: 3px solid white;
      border-radius: 50%;
      box-shadow: 0 2px 8px rgba(0,0,0,0.3);
      display: flex;
      align-items: center;
      justify-content: center;
    ">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="white">
        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
        <circle cx="12" cy="9" r="2.5" fill="${color}"/>
      </svg>
    </div>`,
    iconSize: [32, 32],
    iconAnchor: [16, 16],
    popupAnchor: [0, -16]
  });
};

const createUserIcon = () => {
  return L.divIcon({
    className: 'custom-marker',
    html: `<div style="
      width: 28px;
      height: 28px;
      background-color: #3b82f6;
      border: 3px solid white;
      border-radius: 50%;
      box-shadow: 0 2px 8px rgba(0,0,0,0.3);
      display: flex;
      align-items: center;
      justify-content: center;
    ">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="white">
        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
      </svg>
    </div>`,
    iconSize: [28, 28],
    iconAnchor: [14, 14],
    popupAnchor: [0, -14]
  });
};

// Component to auto-fit bounds
interface MapBoundsProps {
  partners: PartnerLocation[];
  requests: ServiceRequest[];
}

const MapBounds: FC<MapBoundsProps> = ({ partners, requests }) => {
  const map = useMap();

  useEffect(() => {
    if (partners.length === 0 && requests.length === 0) return;

    const bounds = L.latLngBounds([]);

    partners.forEach(p => {
      if (p.latitude && p.longitude) {
        bounds.extend([p.latitude, p.longitude]);
      }
    });

    requests.forEach(r => {
      if (r.location?.latitude && r.location?.longitude) {
        bounds.extend([r.location.latitude, r.location.longitude]);
      }
    });

    if (bounds.isValid()) {
      map.fitBounds(bounds, { padding: [50, 50], maxZoom: 14 });
    }
  }, [partners, requests, map]);

  return null;
};

interface LiveMapProps {
  partners: PartnerLocation[];
  requests: ServiceRequest[];
  loading?: boolean;
}

const LiveMap: FC<LiveMapProps> = ({ partners, requests, loading }) => {
  const [mapReady, setMapReady] = useState(false);

  if (loading) {
    return (
      <div className="h-96 bg-gray-100 flex items-center justify-center">
        <div className="animate-pulse text-gray-400">Loading map...</div>
      </div>
    );
  }

  // Default center (London)
  const center: L.LatLngExpression = [51.5074, -0.1278];

  return (
    <div className="relative h-96">
      <MapContainer
        center={center}
        zoom={10}
        className="h-full w-full"
        whenReady={() => setMapReady(true)}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        <MapBounds partners={partners} requests={requests} />

        {/* Partner Markers */}
        {partners.map((partner) => (
          <Marker
            key={partner.driverId}
            position={[partner.latitude, partner.longitude]}
            icon={createPartnerIcon(partner.status)}
          >
            <Popup>
              <div className="p-2">
                <h3 className="font-semibold text-sm">{partner.driverName || `Driver ${partner.driverId}`}</h3>
                <p className="text-xs text-gray-600">
                  Status: <span className={`font-medium ${
                    partner.status === EntityStatus.AVAILABLE ? 'text-green-600' :
                    partner.status === EntityStatus.BUSY ? 'text-red-600' :
                    'text-amber-600'
                  }`}>{partner.status}</span>
                </p>
                {partner.currentJobId && (
                  <p className="text-xs text-gray-600">Job: {partner.currentJobId}</p>
                )}
                {partner.vehicleRegistration && (
                  <p className="text-xs text-gray-600">Vehicle: {partner.vehicleRegistration}</p>
                )}
                <p className="text-xs text-gray-500">
                  Updated: {new Date(partner.lastUpdate).toLocaleTimeString()}
                </p>
              </div>
            </Popup>
          </Marker>
        ))}

        {/* User Request Markers */}
        {requests.map((request) => (
          <Marker
            key={request.requestId}
            position={[request.location.latitude, request.location.longitude]}
            icon={createUserIcon()}
          >
            <Popup>
              <div className="p-2">
                <h3 className="font-semibold text-sm">Request {request.requestId}</h3>
                <p className="text-xs text-gray-600">
                  Service: {request.serviceType}
                </p>
                <p className="text-xs text-gray-600">
                  Status: <span className={`font-medium ${
                    request.status === 'PENDING' ? 'text-amber-600' :
                    request.status === 'ASSIGNED' ? 'text-blue-600' :
                    'text-green-600'
                  }`}>{request.status}</span>
                </p>
                {request.assignedPartner && (
                  <p className="text-xs text-gray-600">
                    Assigned: {request.assignedPartner.name}
                  </p>
                )}
                <p className="text-xs text-gray-500">{request.location.address}</p>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>

      {/* Legend */}
      <div className="absolute bottom-4 left-4 bg-white rounded-lg shadow-lg p-3 text-xs">
        <div className="font-semibold mb-2">Legend</div>
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-green-500"></div>
            <span>Available Partner</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-red-500"></div>
            <span>Busy Partner</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-amber-500"></div>
            <span>En Route</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-full bg-blue-500"></div>
            <span>User Request</span>
          </div>
        </div>
      </div>

      {/* Stats overlay */}
      <div className="absolute top-4 right-4 bg-white rounded-lg shadow-lg p-3 text-xs">
        <div className="font-semibold mb-2">Live Stats</div>
        <div className="space-y-1">
          <div className="flex justify-between gap-4">
            <span className="text-gray-600">Partners:</span>
            <span className="font-medium">{partners.length}</span>
          </div>
          <div className="flex justify-between gap-4">
            <span className="text-gray-600">Requests:</span>
            <span className="font-medium">{requests.length}</span>
          </div>
          <div className="flex justify-between gap-4">
            <span className="text-gray-600">Available:</span>
            <span className="font-medium text-green-600">
              {partners.filter(p => p.status === EntityStatus.AVAILABLE).length}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LiveMap;
