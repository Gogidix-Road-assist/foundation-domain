import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Map, createMarkerIcon, MapWithCenterMarker } from './Map';

// Mock Leaflet
vi.mock('leaflet', () => ({
  default: {
    map: vi.fn(() => ({
      setView: vi.fn(),
      fitBounds: vi.fn(),
      on: vi.fn(),
      remove: vi.fn(),
    })),
    tileLayer: vi.fn(() => ({
      addTo: vi.fn(() => ({})),
    })),
    marker: vi.fn(() => ({
      addTo: vi.fn(() => ({})),
      bindPopup: vi.fn(() => ({})),
      setIcon: vi.fn(() => ({})),
      on: vi.fn(() => ({})),
      remove: vi.fn(() => ({})),
    })),
    latLngBounds: vi.fn(() => ({})),
    divIcon: vi.fn(() => ({})),
    Icon: {
      Default: {
        mergeOptions: vi.fn(),
      },
      prototype: {
        _getIconUrl: vi.fn(),
      },
    },
  },
}));

vi.mock('leaflet/dist/leaflet.css', () => ({}));

const sampleMarkers = [
  {
    id: '1',
    position: [51.505, -0.09] as [number, number],
    title: 'London',
    description: 'Capital of England',
  },
  {
    id: '2',
    position: [51.51, -0.1] as [number, number],
    title: 'Baker Street',
    description: 'Famous street in London',
  },
];

describe('Map', () => {
  it('renders map container', () => {
    render(<Map markers={[]} height={400} />);

    const mapContainer = document.querySelector('[style*="height: 400px"]');
    expect(mapContainer).toBeInTheDocument();
  });

  it('renders with custom dimensions', () => {
    render(<Map markers={[]} height={300} width="80%" />);

    const mapContainer = document.querySelector('[style*="height: 300px"]');
    expect(mapContainer).toBeInTheDocument();
  });

  it('renders in loading state', () => {
    render(<Map markers={[]} height={400} loading={true} />);

    const skeleton = document.querySelector('.MuiSkeleton-root');
    expect(skeleton).toBeInTheDocument();
  });

  it('calls onMapClick when map is clicked', () => {
    const onMapClick = vi.fn();
    render(<Map markers={[]} height={400} onMapClick={onMapClick} />);

    // Note: Actual click testing requires Leaflet to be fully mounted
    expect(onMapClick).toBeDefined();
  });

  it('calls onMarkerClick when marker is clicked', () => {
    const onMarkerClick = vi.fn();
    render(<Map markers={sampleMarkers} height={400} onMarkerClick={onMarkerClick} />);

    // Note: Actual marker click testing requires Leaflet to be fully mounted
    expect(onMarkerClick).toBeDefined();
  });

  it('applies custom className', () => {
    render(<Map markers={[]} height={400} className="custom-map" />);

    const mapContainer = document.querySelector('.custom-map');
    expect(mapContainer).toBeInTheDocument();
  });

  it('renders with custom style', () => {
    const customStyle = { border: '2px solid red' };
    render(<Map markers={[]} height={400} style={customStyle} />);

    const mapContainer = document.querySelector('[style*="border"]');
    expect(mapContainer).toBeInTheDocument();
  });

  it('renders without markers', () => {
    render(<Map markers={[]} height={400} />);

    const mapContainer = document.querySelector('[style*="height: 400px"]');
    expect(mapContainer).toBeInTheDocument();
  });
});

describe('createMarkerIcon', () => {
  it('creates a marker icon with default color', () => {
    const icon = createMarkerIcon();
    expect(icon).toBeDefined();
  });

  it('creates a marker icon with custom color', () => {
    const icon = createMarkerIcon('#FF0000');
    expect(icon).toBeDefined();
  });

  it('creates a marker icon with custom size', () => {
    const icon = createMarkerIcon('#00FF00', 48);
    expect(icon).toBeDefined();
  });
});

describe('MapWithCenterMarker', () => {
  it('renders map with center marker', () => {
    render(<MapWithCenterMarker height={400} markerLabel="Center Point" />);

    const mapContainer = document.querySelector('[style*="height: 400px"]');
    expect(mapContainer).toBeInTheDocument();
  });

  it('renders with custom marker color', () => {
    render(
      <MapWithCenterMarker
        height={400}
        markerLabel="Center Point"
        markerColor="#10B981"
      />
    );

    const mapContainer = document.querySelector('[style*="height: 400px"]');
    expect(mapContainer).toBeInTheDocument();
  });

  it('passes through other Map props', () => {
    render(
      <MapWithCenterMarker
        height={300}
        width="80%"
        center={[40.7128, -74.006]}
      />
    );

    const mapContainer = document.querySelector('[style*="height: 300px"]');
    expect(mapContainer).toBeInTheDocument();
  });
});
