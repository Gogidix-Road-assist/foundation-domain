import type { Meta, StoryObj } from '@storybook/react';
import { RealTimeTracker, VehicleCard } from './RealTimeTracker';

const meta = {
  title: 'Dashboards/RealTimeTracker',
  component: RealTimeTracker,
  tags: ['autodocs'],
  parameters: {
    layout: 'fullscreen',
  },
} satisfies Meta<typeof RealTimeTracker>;

export default meta;
type Story = StoryObj<typeof RealTimeTracker>;

const mockVehicles = [
  {
    id: '001',
    type: 'mechanic' as const,
    status: 'active' as const,
    position: { lat: 51.505, lng: -0.09 },
    driver: { name: 'John Smith', phone: '+1-234-567-8901' },
    destination: '123 Main St, London',
    eta: '15 min',
    speed: 45,
    lastUpdate: '2 min ago',
  },
  {
    id: '002',
    type: 'tow' as const,
    status: 'active' as const,
    position: { lat: 51.51, lng: -0.1 },
    driver: { name: 'Jane Doe' },
    destination: '456 Oak Ave',
    eta: '22 min',
    speed: 52,
    lastUpdate: '3 min ago',
  },
  {
    id: '003',
    type: 'mechanic' as const,
    status: 'idle' as const,
    position: { lat: 51.52, lng: -0.08 },
    driver: { name: 'Bob Johnson' },
    destination: '789 Pine Rd',
    eta: 'Available',
    speed: 0,
    lastUpdate: '5 min ago',
  },
  {
    id: '004',
    type: 'truck' as const,
    status: 'offline' as const,
    position: { lat: 51.49, lng: -0.11 },
    driver: { name: 'Alice Brown' },
    speed: 0,
    lastUpdate: '1 hr ago',
  },
  {
    id: '005',
    type: 'ambulance' as const,
    status: 'error' as const,
    position: { lat: 51.50, lng: -0.12 },
    driver: { name: 'Charlie Wilson' },
    destination: 'Emergency Center',
    eta: 'On site',
    speed: 0,
    lastUpdate: '30 min ago',
  },
];

export const Default: Story = {
  args: {
    vehicles: mockVehicles,
    height: 700,
  },
};

export const WithoutMap: Story = {
  args: {
    vehicles: mockVehicles,
    showMap: false,
    height: 600,
  },
};

export const WithSelectedVehicle: Story = {
  args: {
    vehicles: mockVehicles,
    selectedVehicleId: '001',
    height: 700,
  },
};

export const WithAutoRefreshDisabled: Story = {
  args: {
    vehicles: mockVehicles,
    autoRefresh: false,
    height: 600,
  },
};

export const SmallHeight: Story = {
  args: {
    vehicles: mockVehicles,
    height: 400,
  },
};

export const CustomRefreshInterval: Story = {
  args: {
    vehicles: mockVehicles,
    autoRefresh: true,
    refreshInterval: 10000, // 10 seconds
    height: 600,
  },
};

export const WithOnRefresh: Story = {
  args: {
    vehicles: mockVehicles,
    onRefresh: () => console.log('Refreshing...'),
    height: 600,
  },
};

export const WithOnSelectVehicle: Story = {
  args: {
    vehicles: mockVehicles,
    onSelectVehicle: (vehicle) => console.log('Selected:', vehicle),
    height: 600,
  },
};

export const AllActive: Story = {
  args: {
    vehicles: mockVehicles.map((v) => ({ ...v, status: 'active' as const })),
    height: 600,
  },
};

export const MixedStatus: Story = {
  args: {
    vehicles: [
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
        eta: 'Available',
        speed: 0,
        lastUpdate: '5 min ago',
      },
      {
        id: '003',
        type: 'ambulance' as const,
        status: 'error' as const,
        position: { lat: 51.52, lng: -0.08 },
        driver: { name: 'Bob Johnson' },
        destination: 'Emergency',
        eta: 'On site',
        speed: 0,
        lastUpdate: '10 min ago',
      },
    ],
    height: 600,
  },
};

// VehicleCard stories
const vehicleCardMeta = {
  title: 'Dashboards/RealTimeTracker/VehicleCard',
  component: VehicleCard,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof VehicleCard>;

const vehicleCardDefault: StoryObj<typeof VehicleCard> = {
  render: (args) => <VehicleCard {...args} />,
};

export const VehicleCardDefault = {
  ...vehicleCardMeta,
  ...vehicleCardDefault,
  args: {
    vehicle: mockVehicles[0],
  },
};

export const VehicleCardIdle = {
  ...vehicleCardMeta,
  ...vehicleCardDefault,
  args: {
    vehicle: mockVehicles[2],
  },
};

export const VehicleCardError = {
  ...vehicleCardMeta,
  ...vehicleCardDefault,
  args: {
    vehicle: mockVehicles[4],
  },
};

export const VehicleCardSelected = {
  ...vehicleCardMeta,
  ...vehicleCardDefault,
  args: {
    vehicle: mockVehicles[0],
    selected: true,
  },
};

export const VehicleCardClickable = {
  ...vehicleCardMeta,
  ...vehicleCardDefault,
  args: {
    vehicle: mockVehicles[0],
    onClick: (v) => console.log('Clicked:', v),
  },
};

export const VehicleCardsGrid = {
  ...vehicleCardMeta,
  render: () => (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' }}>
      {mockVehicles.slice(0, 4).map((vehicle) => (
        <VehicleCard key={vehicle.id} vehicle={vehicle} />
      ))}
    </div>
  ),
};
