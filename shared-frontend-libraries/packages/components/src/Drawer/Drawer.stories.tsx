import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Typography, Box, IconButton } from '@mui/material';
import { Menu, Dashboard, Settings, People, Timeline, Help } from '@mui/icons-material';
import { Drawer, DrawerTrigger } from './Drawer';

const meta: Meta<typeof Drawer> = {
  title: 'Components/Drawer',
  component: Drawer,
  tags: ['autodocs'],
  argTypes: {
    anchor: {
      control: 'select',
      options: ['left', 'right', 'top', 'bottom'],
    },
    variant: {
      control: 'select',
      options: ['temporary', 'permanent', 'persistent'],
    },
    open: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Drawer>;

export const Default: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <DrawerTrigger onClick={() => setOpen(true)} />
        <Drawer
          open={open}
          onClose={() => setOpen(false)}
          title="Navigation"
          items={[
            { label: 'Dashboard', icon: <Dashboard />, onClick: () => console.log('Dashboard'), active: true },
            { label: 'Team', icon: <People />, onClick: () => console.log('Team') },
            { label: 'Activity', icon: <Timeline />, onClick: () => console.log('Activity') },
            { label: 'Settings', icon: <Settings />, onClick: () => console.log('Settings') },
            { label: 'Help', icon: <Help />, onClick: () => console.log('Help') },
          ]}
        >
          <Box sx={{ p: 3 }}>
            <Typography variant="body2" color="text.secondary">
              RapidAssist v1.0.0
            </Typography>
          </Box>
        </Drawer>
        <Box sx={{ p: 3 }}>
          <Typography>Click the menu icon to open the drawer.</Typography>
        </Box>
      </>
    );
  },
};

export const RightAnchored: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <IconButton onClick={() => setOpen(true)} sx={{ float: 'right' }}>
          <Menu />
        </IconButton>
        <Drawer
          open={open}
          onClose={() => setOpen(false)}
          anchor="right"
          title="Options"
          items={[
            { label: 'Profile', onClick: () => {} },
            { label: 'Notifications', onClick: () => {} },
            { label: 'Logout', onClick: () => {} },
          ]}
        />
        <Box sx={{ p: 3, clear: 'both' }}>
          <Typography>Drawer opens from the right.</Typography>
        </Box>
      </>
    );
  },
};

export const BottomAnchored: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <Box sx={{ position: 'fixed', bottom: 80, right: 24, zIndex: 1000 }}>
          <IconButton onClick={() => setOpen(true)} sx={{ bgcolor: 'primary.main', color: 'white' }}>
            <Menu />
          </IconButton>
        </Box>
        <Drawer
          open={open}
          onClose={() => setOpen(false)}
          anchor="bottom"
          title="Quick Actions"
          items={[
            { label: 'New Request', onClick: () => {} },
            { label: 'View History', onClick: () => {} },
            { label: 'Contact Support', onClick: () => {} },
          ]}
        />
        <Box sx={{ p: 3 }}>
          <Typography>Click the floating button to open the bottom drawer.</Typography>
        </Box>
      </>
    );
  },
};

export const WithCustomContent: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <IconButton onClick={() => setOpen(true)}>
          <Menu />
        </IconButton>
        <Drawer open={open} onClose={() => setOpen(false)} title="User Profile">
          <Box sx={{ p: 3, textAlign: 'center' }}>
            <Box
              sx={{
                width: 80,
                height: 80,
                borderRadius: '50%',
                bgcolor: '#E5E7EB',
                margin: '0 auto 16px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Typography variant="h4">JD</Typography>
            </Box>
            <Typography variant="h6" gutterBottom>
              John Doe
            </Typography>
            <Typography variant="body2" color="text.secondary" gutterBottom>
              john.doe@example.com
            </Typography>
            <Box sx={{ mt: 2 }}>
              <Typography variant="caption" color="text.secondary">
                Premium Member
              </Typography>
            </Box>
          </Box>
        </Drawer>
        <Box sx={{ p: 3 }}>
          <Typography>Drawer with custom user profile content.</Typography>
        </Box>
      </>
    );
  },
};

export const WithDisabledItems: StoryObj = {
  render: () => {
    const [open, setOpen] = useState(false);
    return (
      <>
        <IconButton onClick={() => setOpen(true)}>
          <Menu />
        </IconButton>
        <Drawer
          open={open}
          onClose={() => setOpen(false)}
          title="Menu"
          items={[
            { label: 'Dashboard', onClick: () => {} },
            { label: 'Reports', onClick: () => {} },
            { label: 'Admin (Disabled)', onClick: () => {}, disabled: true },
            { label: 'Settings', onClick: () => {} },
          ]}
        />
        <Box sx={{ p: 3 }}>
          <Typography>Some menu items can be disabled.</Typography>
        </Box>
      </>
    );
  },
};

export const Permanent: StoryObj = {
  render: () => {
    const [selected, setSelected] = useState('dashboard');
    return (
      <Box sx={{ display: 'flex', height: '100vh' }}>
        <Drawer
          open
          onClose={() => {}}
          anchor="left"
          variant="permanent"
          title="RapidAssist"
          items={[
            {
              label: 'Dashboard',
              icon: <Dashboard />,
              onClick: () => setSelected('dashboard'),
              active: selected === 'dashboard',
            },
            {
              label: 'Team',
              icon: <People />,
              onClick: () => setSelected('team'),
              active: selected === 'team',
            },
            {
              label: 'Activity',
              icon: <Timeline />,
              onClick: () => setSelected('activity'),
              active: selected === 'activity',
            },
            {
              label: 'Settings',
              icon: <Settings />,
              onClick: () => setSelected('settings'),
              active: selected === 'settings',
            },
          ]}
        />
        <Box component="main" sx={{ flexGrow: 1, p: 3, bgcolor: '#F9FAFB' }}>
          <Typography variant="h4" gutterBottom>
            {selected.charAt(0).toUpperCase() + selected.slice(1)}
          </Typography>
          <Typography>This is a permanent sidebar drawer.</Typography>
        </Box>
      </Box>
    );
  },
};
