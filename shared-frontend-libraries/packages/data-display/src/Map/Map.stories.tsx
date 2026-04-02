import type { Meta, StoryObj } from '@storybook/react';
import { Map, createMarkerIcon, MapWithCenterMarker } from './Map';

const meta = {
  title: 'Data Display/Map',
  component: Map,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof Map>;

export default meta;
type Story = StoryObj<typeof Map>;

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
  {
    id: '3',
    position: [51.52, -0.08] as [number, number],
    title: 'Regent Park',
    description: 'Beautiful park in London',
  },
  {
    id: '4',
    position: [51.49, -0.11] as [number, number],
    title: 'Westminster',
    description: 'Political center of UK',
  },
];

export const Default: Story = {
  args: {
    markers: sampleMarkers,
    center: [51.505, -0.09],
    zoom: 13,
    height: 400,
  },
};

export const WithoutPopups: Story = {
  args: {
    markers: sampleMarkers,
    showPopup: false,
    center: [51.505, -0.09],
    height: 400,
  },
};

export const CustomSize: Story = {
  args: {
    markers: sampleMarkers,
    height: 500,
    width: '80%',
    center: [51.505, -0.09],
  },
};

export const DifferentZoom: Story = {
  args: {
    markers: sampleMarkers,
    center: [51.505, -0.09],
    zoom: 10,
    height: 400,
  },
};

export const Loading: Story = {
  args: {
    markers: [],
    height: 400,
    loading: true,
  },
};

export const SingleMarker: Story = {
  args: {
    markers: [
      {
        id: '1',
        position: [51.505, -0.09] as [number, number],
        title: 'Location',
        description: 'Single marker on the map',
      },
    ],
    center: [51.505, -0.09],
    height: 400,
  },
};

export const ManyMarkers: Story = {
  args: {
    markers: Array.from({ length: 20 }, (_, i) => ({
      id: String(i),
      position: [
        51.5 + (Math.random() - 0.5) * 0.1,
        -0.09 + (Math.random() - 0.5) * 0.1,
      ] as [number, number],
      title: `Location ${i + 1}`,
      description: `Marker number ${i + 1}`,
    })),
    center: [51.505, -0.09],
    height: 400,
  },
};

export const MapWithCenterMarkerDefault: Story = {
  render: (args) => <MapWithCenterMarker {...args} />,
  args: {
    center: [51.505, -0.09],
    markerLabel: 'Center Location',
    height: 400,
  },
};

export const MapWithCenterMarkerCustomColor: Story = {
  render: (args) => <MapWithCenterMarker {...args} />,
  args: {
    center: [40.7128, -74.006],
    markerLabel: 'New York City',
    markerColor: '#EF4444',
    height: 400,
  },
};

export const WithClickHandlers: Story = {
  args: {
    markers: sampleMarkers,
    center: [51.505, -0.09],
    onMarkerClick: (marker) => console.log('Marker clicked:', marker),
    onMapClick: (lat, lng) => console.log('Map clicked:', lat, lng),
    height: 400,
  },
};

export const CustomCenter: Story = {
  args: {
    markers: sampleMarkers,
    center: [40.7128, -74.006],
    zoom: 12,
    height: 400,
  },
};

// Helper to demonstrate custom marker icons
export function CustomMarkerIconDemo() {
  const customIcon = createMarkerIcon('#10B981', 40);

  return (
    <Map
      markers={[
        {
          id: '1',
          position: [51.505, -0.09] as [number, number],
          title: 'Custom Icon',
          description: 'Marker with custom green icon',
          icon: customIcon,
        },
      ]}
      center={[51.505, -0.09]}
      height={400}
    />
  );
}

export const CustomIcons: Story = {
  render: () => <CustomMarkerIconDemo />,
};
