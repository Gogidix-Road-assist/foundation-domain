import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import { RealTimeTracker, VehicleCard } from './RealTimeTracker';

const mockVehicles = [
  {
    id: '001',
    type: 'mechanic' as const,
    status: 'active' as const,
    position: { lat: 51.505, lng: -0.09 },
    driver: { name: 'John Smith' },
    destination: '123 Main St',
    eta: '15 min',
    speed: 45,
    lastUpdate: '2 min ago',
  },
  {
    id: '002',
    type: 'tow' as const,
    status: 'idle' as const,
    position: { lat: 51.51, lng: -0.1 },
    driver: { name: 'Jane Doe' },
    destination: '456 Oak Ave',
    eta: '30 min',
    speed: 0,
    lastUpdate: '5 min ago',
  },
  {
    id: '003',
    type: 'truck' as const,
    status: 'offline' as const,
    position: { lat: 51.52, lng: -0.08 },
    driver: { name: 'Bob Johnson' },
    speed: 0,
    lastUpdate: '1 hr ago',
  },
  {
    id: '004',
    type: 'ambulance' as const,
    status: 'error' as const,
    position: { lat: 51.49, lng: -0.11 },
    driver: { name: 'Alice Brown' },
    speed: 0,
    lastUpdate: '2 hrs ago',
  },
];

describe('RealTimeTracker', () => {
  it('renders with vehicles', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('Real-Time Tracker')).toBeInTheDocument();
    expect(screen.getByText('MECHANIC #001')).toBeInTheDocument();
    expect(screen.getByText('TOW #002')).toBeInTheDocument();
  });

  it('displays vehicle count', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('1 active, 3 inactive')).toBeInTheDocument();
  });

  it('shows map placeholder', () => {
    render(<RealTimeTracker vehicles={mockVehicles} showMap={true} height={500} />);

    expect(screen.getByText('Interactive Map Component')).toBeInTheDocument();
  });

  it('hides map when showMap is false', () => {
    render(<RealTimeTracker vehicles={mockVehicles} showMap={false} height={500} />);

    expect(screen.queryByText('Interactive Map Component')).not.toBeInTheDocument();
  });

  it('calls onRefresh when refresh button is clicked', () => {
    const onRefresh = vi.fn();
    render(<RealTimeTracker vehicles={mockVehicles} onRefresh={onRefresh} height={500} />);

    const refreshButton = screen.getByRole('button');
    fireEvent.click(refreshButton);

    expect(onRefresh).toHaveBeenCalled();
  });

  it('calls onSelectVehicle when vehicle card is clicked', () => {
    const onSelectVehicle = vi.fn();
    render(
      <RealTimeTracker
        vehicles={mockVehicles}
        onSelectVehicle={onSelectVehicle}
        height={500}
      />
    );

    const firstVehicle = screen.getByText('MECHANIC #001').closest('.MuiCard-root');
    firstVehicle && fireEvent.click(firstVehicle);

    expect(onSelectVehicle).toHaveBeenCalledWith(mockVehicles[0]);
  });

  it('highlights selected vehicle', () => {
    render(
      <RealTimeTracker
        vehicles={mockVehicles}
        selectedVehicleId="001"
        height={500}
      />
    );

    const firstVehicle = screen.getByText('MECHANIC #001').closest('.MuiCard-root');
    expect(firstVehicle).toHaveStyle({
      border: expect.stringContaining('2px solid'),
    });
  });

  it('sorts vehicles by status priority', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    const cards = screen.getAllByRole('article');
    // Error status should come first
    expect(cards[0]).toHaveTextContent('ERROR');
    // Active status should come before idle
    expect(cards[1]).toHaveTextContent('ACTIVE');
  });

  it('displays driver information', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('John Smith')).toBeInTheDocument();
    expect(screen.getByText('Jane Doe')).toBeInTheDocument();
  });

  it('displays ETA when available', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('ETA: 15 min')).toBeInTheDocument();
    expect(screen.getByText('ETA: 30 min')).toBeInTheDocument();
  });

  it('displays speed when available', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('45 km/h')).toBeInTheDocument();
  });

  it('displays last update time', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('2 min ago')).toBeInTheDocument();
    expect(screen.getByText('5 min ago')).toBeInTheDocument();
  });

  it('displays destination when available', () => {
    render(<RealTimeTracker vehicles={mockVehicles} height={500} />);

    expect(screen.getByText('123 Main St')).toBeInTheDocument();
    expect(screen.getByText('456 Oak Ave')).toBeInTheDocument();
  });

  it('applies custom className', () => {
    render(
      <RealTimeTracker vehicles={mockVehicles} className="custom-tracker" height={500} />
    );

    const tracker = document.querySelector('.custom-tracker');
    expect(tracker).toBeInTheDocument();
  });
});

describe('VehicleCard', () => {
  it('renders vehicle information', () => {
    render(<VehicleCard vehicle={mockVehicles[0]} />);

    expect(screen.getByText('MECHANIC #001')).toBeInTheDocument();
    expect(screen.getByText('active')).toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const onClick = vi.fn();
    render(<VehicleCard vehicle={mockVehicles[0]} onClick={onClick} />);

    const card = screen.getByText('MECHANIC #001').closest('.MuiCard-root');
    card && fireEvent.click(card);

    expect(onClick).toHaveBeenCalledWith(mockVehicles[0]);
  });

  it('shows selected state', () => {
    render(<VehicleCard vehicle={mockVehicles[0]} selected={true} />);

    const card = screen.getByText('MECHANIC #001').closest('.MuiCard-root');
    expect(card).toHaveStyle({
      border: expect.stringContaining('2px solid'),
    });
  });

  it('renders without driver info', () => {
    const vehicleWithoutDriver = {
      ...mockVehicles[0],
      driver: undefined,
    };
    render(<VehicleCard vehicle={vehicleWithoutDriver} />);

    expect(screen.queryByText('John Smith')).not.toBeInTheDocument();
  });
});
