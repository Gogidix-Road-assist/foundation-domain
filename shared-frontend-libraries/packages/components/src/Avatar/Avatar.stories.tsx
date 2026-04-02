import type { Meta, StoryObj } from '@storybook/react';
import { Avatar, AvatarGroup } from './Avatar';

const meta: Meta<typeof Avatar> = {
  title: 'Components/Avatar',
  component: Avatar,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium', 'large', 'xl'],
    },
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'success', 'warning', 'error', 'info'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Avatar>;

export const Default: Story = {
  args: {
    fallbackText: 'JD',
  },
};

export const Image: Story = {
  args: {
    src: 'https://i.pravatar.cc/150?img=1',
    alt: 'User Avatar',
  },
};

export const Sizes: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
      <Avatar size="small" fallbackText="S" />
      <Avatar size="medium" fallbackText="M" />
      <Avatar size="large" fallbackText="L" />
      <Avatar size="xl" fallbackText="XL" />
    </div>
  ),
};

export const Colors: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px' }}>
      <Avatar color="primary" fallbackText="PR" />
      <Avatar color="secondary" fallbackText="SE" />
      <Avatar color="success" fallbackText="SU" />
      <Avatar color="warning" fallbackText="WA" />
      <Avatar color="error" fallbackText="ER" />
      <Avatar color="info" fallbackText="IN" />
    </div>
  ),
};

export const Initials: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px' }}>
      <Avatar alt="Alice Smith" />
      <Avatar alt="Bob Johnson" />
      <Avatar alt="Charlie Brown" />
      <Avatar alt="Diana Prince" />
    </div>
  ),
};

export const AvatarGroupExample: StoryObj = {
  render: () => (
    <AvatarGroup>
      <Avatar src="https://i.pravatar.cc/150?img=1" alt="User 1" />
      <Avatar src="https://i.pravatar.cc/150?img=2" alt="User 2" />
      <Avatar src="https://i.pravatar.cc/150?img=3" alt="User 3" />
      <Avatar src="https://i.pravatar.cc/150?img=4" alt="User 4" />
      <Avatar src="https://i.pravatar.cc/150?img=5" alt="User 5" />
      <Avatar fallbackText="+5" />
    </AvatarGroup>
  ),
};

export const AvatarGroupWithMax: StoryObj = {
  render: () => (
    <AvatarGroup max={3}>
      <Avatar src="https://i.pravatar.cc/150?img=1" alt="User 1" />
      <Avatar src="https://i.pravatar.cc/150?img=2" alt="User 2" />
      <Avatar src="https://i.pravatar.cc/150?img=3" alt="User 3" />
      <Avatar src="https://i.pravatar.cc/150?img=4" alt="User 4" />
      <Avatar src="https://i.pravatar.cc/150?img=5" alt="User 5" />
    </AvatarGroup>
  ),
};

export const SmallGroup: StoryObj = {
  render: () => (
    <AvatarGroup size="small">
      <Avatar src="https://i.pravatar.cc/150?img=1" alt="User 1" />
      <Avatar src="https://i.pravatar.cc/150?img=2" alt="User 2" />
      <Avatar src="https://i.pravatar.cc/150?img=3" alt="User 3" />
    </AvatarGroup>
  ),
};
