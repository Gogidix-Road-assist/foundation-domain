import React, { useEffect, useRef, useState, useCallback } from 'react';
import { Box, Skeleton, Typography } from '@mui/material';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { motion } from 'framer-motion';

// Fix for default markers in React + Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

export interface MapMarker {
  id: string;
  position: [number, number];
  title?: string;
  description?: string;
  color?: string;
  icon?: L.Icon;
  popupContent?: React.ReactNode;
  onClick?: (marker: MapMarker) => void;
}

export interface MapProps {
  center?: [number, number];
  zoom?: number;
  markers?: MapMarker[];
  height?: number | string;
  width?: number | string;
  showPopup?: boolean;
  onMarkerClick?: (marker: MapMarker) => void;
  onMapClick?: (lat: number, lng: number) => void;
  className?: string;
  style?: React.CSSProperties;
  loading?: boolean;
}

export function Map({
  center: defaultCenter = [51.505, -0.09],
  zoom = 13,
  markers = [],
  height = 400,
  width = '100%',
  showPopup = true,
  onMarkerClick,
  onMapClick,
  className,
  style,
  loading = false,
}: MapProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<L.Map | null>(null);
  const markersRef = useRef<L.Marker[]>([]);
  const [center, setCenter] = useState<[number, number]>(defaultCenter);

  // Initialize map
  useEffect(() => {
    if (!mapRef.current || mapInstanceRef.current) return;

    const map = L.map(mapRef.current, {
      center,
      zoom,
      zoomControl: true,
      scrollWheelZoom: true,
      doubleClickZoom: true,
    });

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(map);

    mapInstanceRef.current = map;

    // Handle map click
    if (onMapClick) {
      map.on('click', (e) => {
        onMapClick(e.latlng.lat, e.latlng.lng);
      });
    }

    return () => {
      map.remove();
      mapInstanceRef.current = null;
    };
  }, []); // Only run once on mount

  // Update center when prop changes
  useEffect(() => {
    if (mapInstanceRef.current) {
      mapInstanceRef.current.setView(center, zoom);
    }
  }, [center, zoom]);

  // Handle markers
  useEffect(() => {
    if (!mapInstanceRef.current) return;

    // Remove existing markers
    markersRef.current.forEach((marker) => marker.remove());
    markersRef.current = [];

    // Add new markers
    markers.forEach((markerData) => {
      const marker = L.marker(markerData.position);

      if (showPopup && (markerData.title || markerData.description || markerData.popupContent)) {
        const popupContent = markerData.popupContent || (
          <div>
            {markerData.title && <strong>{markerData.title}</strong>}
            {markerData.description && <p>{markerData.description}</p>}
          </div>
        );

        // Create popup content element
        const popupElement = document.createElement('div');
        if (React.isValidElement(markerData.popupContent)) {
          // For React components, we'll use a simpler approach
          marker.bindPopup(
            `${markerData.title || ''}\n${markerData.description || ''}`
          );
        } else {
          marker.bindPopup(popupContent);
        }
      }

      if (markerData.icon) {
        marker.setIcon(markerData.icon);
      }

      if (onMarkerClick || markerData.onClick) {
        marker.on('click', () => {
          const handler = markerData.onClick || onMarkerClick;
          if (handler) handler(markerData);
        });
      }

      marker.addTo(mapInstanceRef.current);
      markersRef.current.push(marker);
    });

    return () => {
      markersRef.current.forEach((marker) => marker.remove());
      markersRef.current = [];
    };
  }, [markers, showPopup, onMarkerClick]);

  // Fit bounds when markers change
  useEffect(() => {
    if (markers.length > 0 && mapInstanceRef.current) {
      const bounds = L.latLngBounds(markers.map((m) => m.position));
      mapInstanceRef.current.fitBounds(bounds, { padding: [50, 50] });
    }
  }, [markers]);

  if (loading) {
    return (
      <Skeleton
        variant="rectangular"
        width={width}
        height={typeof height === 'number' ? height : 400}
        sx={{ borderRadius: 2 }}
      />
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.3 }}
    >
      <Box
        ref={mapRef}
        sx={{
          height,
          width,
          borderRadius: 2,
          overflow: 'hidden',
          position: 'relative',
          zIndex: 0,
        }}
        className={className}
        style={style}
      />
      {/* Leaflet attribution fix */}
      <style>{`
        .leaflet-control-attribution {
          background: rgba(255, 255, 255, 0.8) !important;
          padding: 4px !important;
        }
        .leaflet-popup-content-wrapper {
          padding: 0 !important;
        }
        .leaflet-popup-content {
          margin: 8px !important;
        }
      `}</style>
    </motion.div>
  );
}

Map.displayName = 'Map';

// Helper to create custom marker icon
export function createMarkerIcon(
  color: string = '#3B82F6',
  size: number = 32
): L.Icon {
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 24 24" fill="${color}">
      <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
    </svg>
  `;

  return L.divIcon({
    html: svg,
    className: 'custom-marker',
    iconSize: [size, size],
    iconAnchor: [size / 2, size],
    popupAnchor: [0, -size],
  });
}

// Map with centered marker variant
export interface MapWithCenterMarkerProps extends Omit<MapProps, 'markers'> {
  markerLabel?: string;
  markerColor?: string;
}

export function MapWithCenterMarker({
  markerLabel,
  markerColor,
  ...props
}: MapWithCenterMarkerProps) {
  return (
    <Map
      {...props}
      markers={[
        {
          id: 'center-marker',
          position: props.center || [51.505, -0.09],
          title: markerLabel,
          icon: createMarkerIcon(markerColor),
        },
      ]}
    />
  );
}

MapWithCenterMarker.displayName = 'MapWithCenterMarker';
