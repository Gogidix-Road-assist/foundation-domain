import type { Meta, StoryObj } from '@storybook/react';
import { Button } from '../Button';
import { Badge } from './Badge';

const meta: Meta<typeof Badge> = {
  title: 'Components/Badge',
  component: Badge,
  tags: ['autodocs'],
  argTypes: {
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'error', 'success', 'warning'],
    },
    dot: { control: 'boolean' },
    showZero: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Badge>;

export const Default: Story = {
  args: {
    count: 5,
    children: <Button>Notifications</Button>,
  },
};

export const Zero: Story = {
  args: {
    count: 0,
    showZero: true,
    children: <Button>Inbox</Button>,
  },
};

export const HiddenZero: Story = {
  args: {
    count: 0,
    showZero: false,
    children: <Button>Inbox</Button>,
  },
};

export const MaxValue: Story = {
  args: {
    count: 150,
    max: 99,
    children: <Button>Messages</Button>,
  },
};

export const Dot: Story = {
  args: {
    dot: true,
    children: <Button>Status</Button>,
  },
};

export const Colors: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px' }}>
      <Badge count={5} color="primary">
        <Button>Primary</Button>
      </Badge>
      <Badge count={3} color="secondary">
        <Button>Secondary</Button>
      </Badge>
      <Badge count={10} color="error">
        <Button>Error</Button>
      </Badge>
      <Badge count={2} color="success">
        <Button>Success</Button>
      </Badge>
      <Badge count={7} color="warning">
        <Button>Warning</Button>
      </Badge>
    </div>
  ),
};

export const WithoutAnchor: Story = {
  args: {
    count: 42,
  },
};

export const LargeCount: Story = {
  args: {
    count: 999,
    max: 999,
    children: <Button>Large Count</Button>,
  },
};
