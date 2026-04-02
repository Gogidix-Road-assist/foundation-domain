import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Box, Typography } from '@mui/material';
import { Tabs } from './Tabs';

const meta: Meta<typeof Tabs> = {
  title: 'Components/Tabs',
  component: Tabs,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium'],
    },
    variant: {
      control: 'select',
      options: ['standard', 'scrollable', 'fullWidth'],
    },
    orientation: {
      control: 'select',
      options: ['horizontal', 'vertical'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Tabs>;

export const Default: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Tabs
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Overview', value: 'tab1' },
          { label: 'Features', value: 'tab2' },
          { label: 'Settings', value: 'tab3' },
        ]}
      />
    );
  },
};

export const WithIcons: StoryObj = {
  render: () => {
    const [value, setValue] = useState('home');
    return (
      <Tabs
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Home', value: 'home', icon: <span>🏠</span> },
          { label: 'Profile', value: 'profile', icon: <span>👤</span> },
          { label: 'Settings', value: 'settings', icon: <span>⚙️</span> },
        ]}
      />
    );
  },
};

export const Small: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Tabs
        size="small"
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2' },
          { label: 'Tab 3', value: 'tab3' },
        ]}
      />
    );
  },
};

export const Scrollable: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Tabs
        variant="scrollable"
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2' },
          { label: 'Tab 3', value: 'tab3' },
          { label: 'Tab 4', value: 'tab4' },
          { label: 'Tab 5', value: 'tab5' },
          { label: 'Tab 6', value: 'tab6' },
          { label: 'Tab 7', value: 'tab7' },
        ]}
      />
    );
  },
};

export const FullWidth: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Tabs
        variant="fullWidth"
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Overview', value: 'tab1' },
          { label: 'Features', value: 'tab2' },
          { label: 'Pricing', value: 'tab3' },
        ]}
      />
    );
  },
};

export const Vertical: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Box sx={{ display: 'flex', height: 200 }}>
        <Tabs
          orientation="vertical"
          value={value}
          onChange={(e, val) => setValue(val as string)}
          tabs={[
            { label: 'Dashboard', value: 'tab1' },
            { label: 'Analytics', value: 'tab2' },
            { label: 'Reports', value: 'tab3' },
          ]}
        />
        <Box sx={{ p: 3, borderLeft: '1px solid #E5E7EB', flexGrow: 1 }}>
          <Typography>Content for {value}</Typography>
        </Box>
      </Box>
    );
  },
};

export const WithDisabledTab: StoryObj = {
  render: () => {
    const [value, setValue] = useState('tab1');
    return (
      <Tabs
        value={value}
        onChange={(e, val) => setValue(val as string)}
        tabs={[
          { label: 'Active', value: 'tab1' },
          { label: 'Disabled', value: 'tab2', disabled: true },
          { label: 'Active Too', value: 'tab3' },
        ]}
      />
    );
  },
};

export const WithContentPanel: StoryObj = {
  render: () => {
    const [value, setValue] = useState('overview');
    return (
      <Box>
        <Tabs
          value={value}
          onChange={(e, val) => setValue(val as string)}
          tabs={[
            { label: 'Overview', value: 'overview' },
            { label: 'Details', value: 'details' },
            { label: 'Activity', value: 'activity' },
          ]}
        />
        <Box sx={{ p: 3, mt: 2, backgroundColor: '#F9FAFB', borderRadius: '8px' }}>
          <Typography variant="body1">
            {value === 'overview' && 'This is the overview content.'}
            {value === 'details' && 'This is the detailed content.'}
            {value === 'activity' && 'This is the activity log content.'}
          </Typography>
        </Box>
      </Box>
    );
  },
};
