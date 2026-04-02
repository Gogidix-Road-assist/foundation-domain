import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { IconButton } from '@mui/material';
import { MoreVert, Account, Settings, Logout, Edit, Delete, Share, Star } from '@mui/icons-material';
import { Menu } from './Menu';

const meta: Meta<typeof Menu> = {
  title: 'Components/Menu',
  component: Menu,
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof Menu>;

export const Default: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    return (
      <>
        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
          <MoreVert />
        </IconButton>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          items={[
            { label: 'Profile', value: 'profile', icon: <Account fontSize="small" /> },
            { label: 'Settings', value: 'settings', icon: <Settings fontSize="small" /> },
            { label: 'Logout', value: 'logout', icon: <Logout fontSize="small" /> },
          ]}
        />
      </>
    );
  },
};

export const WithDangerItems: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    return (
      <>
        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
          <MoreVert />
        </IconButton>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          items={[
            { label: 'Edit', value: 'edit', icon: <Edit fontSize="small" /> },
            { label: 'Share', value: 'share', icon: <Share fontSize="small" /> },
            { label: 'Favorite', value: 'favorite', icon: <Star fontSize="small" /> },
            { label: 'Delete', value: 'delete', icon: <Delete fontSize="small" />, danger: true },
          ]}
        />
      </>
    );
  },
};

export const WithDisabledItems: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    return (
      <>
        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
          <MoreVert />
        </IconButton>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          items={[
            { label: 'View', value: 'view' },
            { label: 'Edit', value: 'edit', disabled: true },
            { label: 'Delete', value: 'delete', danger: true },
          ]}
        />
      </>
    );
  },
};

export const ActionMenu: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px', backgroundColor: '#F9FAFB' }}>
        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} style={{ backgroundColor: 'white' }}>
          <MoreVert />
        </IconButton>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          items={[
            { label: 'Copy Link', value: 'copy' },
            { label: 'Download', value: 'download' },
            { label: 'Archive', value: 'archive' },
            { label: 'Move to Trash', value: 'trash', danger: true },
          ]}
        />
      </div>
    );
  },
};

export const UserMenu: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    return (
      <>
        <div
          onClick={(e) => setAnchorEl(e.currentTarget)}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            padding: '8px 12px',
            borderRadius: '8px',
            cursor: 'pointer',
            backgroundColor: '#F3F4F6',
          }}
        >
          <div style={{ width: 32, height: 32, borderRadius: '50%', backgroundColor: '#D1D5DB' }}></div>
          <span>John Doe</span>
        </div>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          onSelect={(value) => console.log('Selected:', value)}
          items={[
            { label: 'My Profile', value: 'profile', icon: <Account fontSize="small" /> },
            { label: 'Account Settings', value: 'settings', icon: <Settings fontSize="small" /> },
            { label: 'Sign Out', value: 'logout', icon: <Logout fontSize="small" />, danger: true },
          ]}
        />
      </>
    );
  },
};

export const ContextMenu: StoryObj = {
  render: () => {
    const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
    const handleContextMenu = (e: React.MouseEvent) => {
      e.preventDefault();
      setAnchorEl(e.currentTarget as HTMLElement);
    };

    return (
      <div
        onContextMenu={handleContextMenu}
        style={{
          height: '200px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: '#F9FAFB',
          borderRadius: '8px',
          border: '2px dashed #D1D5DB',
        }}
      >
        <p style={{ color: '#6B7280', margin: 0 }}>Right-click here</p>
        <Menu
          anchorEl={anchorEl}
          onClose={() => setAnchorEl(null)}
          items={[
            { label: 'Cut', value: 'cut' },
            { label: 'Copy', value: 'copy' },
            { label: 'Paste', value: 'paste' },
            { label: 'Delete', value: 'delete', danger: true },
          ]}
        />
      </div>
    );
  },
};
