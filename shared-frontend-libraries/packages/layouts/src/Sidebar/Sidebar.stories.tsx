import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Dashboard, Settings, People, Assessment, Business, AccountTree } from '@mui/icons-material';
import { Sidebar, SidebarItem } from './Sidebar';

const meta: Meta<typeof Sidebar> = {
  title: 'Layouts/Sidebar',
  component: Sidebar,
  tags: ['autodocs'],
  argTypes: {
    collapsed: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Sidebar>;

const mockItems: SidebarItem[] = [
  { id: 'dashboard', label: 'Dashboard', icon: <Dashboard />, path: '/dashboard' },
  { id: 'users', label: 'Users', icon: <People />, path: '/users' },
  { id: 'business', label: 'Business', icon: <Business />, path: '/business' },
  {
    id: 'reports',
    label: 'Reports',
    icon: <Assessment />,
    children: [
      { id: 'sales', label: 'Sales Report', icon: <AccountTree /> },
      { id: 'inventory', label: 'Inventory', icon: <AccountTree /> },
    ],
  },
  { id: 'settings', label: 'Settings', icon: <Settings />, path: '/settings' },
];

export const Default: StoryObj = {
  render: () => {
    const [selectedItem, setSelectedItem] = useState<string | null>(null);

    return (
      <Box sx={{ display: 'flex', height: '100vh', backgroundColor: '#F9FAFB' }}>
        <Sidebar
          items={mockItems}
          onItemClick={(item) => setSelectedItem(item.id)}
        />
        <Box sx={{ flexGrow: 1, p: 4 }}>
          <h3>Selected: {selectedItem}</h3>
        </Box>
      </Box>
    );
  },
};

export const Collapsed: Story = {
  args: {
    collapsed: true,
    items: mockItems,
  },
};

export const WithNestedItems: StoryObj = {
  render: () => {
    return (
      <Box sx={{ display: 'flex', height: '400px', backgroundColor: '#F9FAFB' }}>
        <Sidebar items={mockItems} />
        <Box sx={{ flexGrow: 1, p: 4, backgroundColor: '#FFFFFF' }}>
          <h3>Main Content</h3>
        </Box>
      </Box>
    );
  },
};
