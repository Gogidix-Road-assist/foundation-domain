import type { Meta, StoryObj } from '@storybook/react';
import { Box, Stack } from '@mui/material';
import { Skeleton, SkeletonCard, SkeletonList, SkeletonTable } from './Skeleton';

const meta: Meta<typeof Skeleton> = {
  title: 'Components/Skeleton',
  component: Skeleton,
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: 'select',
      options: ['text', 'circular', 'rectangular', 'rounded'],
    },
    animation: {
      control: 'select',
      options: ['pulse', 'wave', 'none'],
    },
    count: { control: 'number' },
  },
};

export default meta;
type Story = StoryObj<typeof Skeleton>;

export const Text: Story = {
  args: {
    variant: 'text',
    width: '100%',
  },
};

export const Circular: Story = {
  args: {
    variant: 'circular',
    width: 60,
    height: 60,
  },
};

export const Rectangular: Story = {
  args: {
    variant: 'rectangular',
    width: 200,
    height: 120,
  },
};

export const Rounded: Story = {
  args: {
    variant: 'rounded',
    width: 200,
    height: 120,
  },
};

export const MultipleLines: Story = {
  args: {
    variant: 'text',
    count: 5,
  },
};

export const WaveAnimation: Story = {
  args: {
    variant: 'text',
    animation: 'wave',
    count: 3,
  },
};

export const NoAnimation: Story = {
  args: {
    variant: 'text',
    animation: 'none',
    count: 3,
  },
};

export const SkeletonCardStory: StoryObj = {
  render: () => (
    <Stack spacing={2}>
      <SkeletonCard showAvatar lines={3} showAction />
      <SkeletonCard showAvatar lines={2} showAction />
    </Stack>
  ),
  name: 'Card Skeleton',
};

export const SkeletonCardNoAvatar: StoryObj = {
  render: () => (
    <SkeletonCard showAvatar={false} lines={4} showAction />
  ),
  name: 'Card without Avatar',
};

export const SkeletonListStory: StoryObj = {
  render: () => (
    <Box sx={{ maxWidth: 500 }}>
      <SkeletonList items={5} showAvatar />
    </Box>
  ),
  name: 'List Skeleton',
};

export const SkeletonListNoAvatar: StoryObj = {
  render: () => (
    <Box sx={{ maxWidth: 500 }}>
      <SkeletonList items={4} showAvatar={false} />
    </Box>
  ),
  name: 'List without Avatar',
};

export const SkeletonTableStory: StoryObj = {
  render: () => (
    <Box sx={{ maxWidth: 800 }}>
      <SkeletonTable rows={5} columns={4} showHeader />
    </Box>
  ),
  name: 'Table Skeleton',
};

export const DashboardSkeleton: StoryObj = {
  render: () => (
    <Stack spacing={4}>
      <Stack direction="row" spacing={2}>
        <Box sx={{ flex: 1 }}>
          <Skeleton variant="rectangular" height={120} />
        </Box>
        <Box sx={{ flex: 1 }}>
          <Skeleton variant="rectangular" height={120} />
        </Box>
        <Box sx={{ flex: 1 }}>
          <Skeleton variant="rectangular" height={120} />
        </Box>
      </Stack>
      <SkeletonCard showAvatar lines={4} showAction />
      <SkeletonTable rows={4} columns={5} showHeader />
    </Stack>
  ),
  name: 'Dashboard Loading State',
};

export const ProfileSkeleton: StoryObj = {
  render: () => (
    <Box sx={{ maxWidth: 400, p: 3, border: '1px solid #E5E7EB', borderRadius: 8 }}>
      <Stack alignItems="center" spacing={3}>
        <Skeleton variant="circular" width={100} height={100} />
        <Stack spacing={1} width="100%">
          <Skeleton variant="text" width="60%" sx={{ mx: 'auto' }} />
          <Skeleton variant="text" width="40%" sx={{ mx: 'auto' }} />
        </Stack>
        <Stack spacing={2} width="100%">
          <Skeleton variant="text" />
          <Skeleton variant="text" />
          <Skeleton variant="text" />
        </Stack>
        <Skeleton variant="rectangular" width={150} height={40} />
      </Stack>
    </Box>
  ),
  name: 'Profile Loading State',
};

export const CombinedSkeletons: StoryObj = {
  render: () => (
    <Stack spacing={4}>
      <Box>
        <h3>Text Skeletons</h3>
        <Stack spacing={1}>
          <Skeleton variant="text" />
          <Skeleton variant="text" width="80%" />
          <Skeleton variant="text" width="60%" />
        </Stack>
      </Box>
      <Box>
        <h3>Avatar Skeletons</h3>
        <Stack direction="row" spacing={2}>
          <Skeleton variant="circular" width={40} height={40} />
          <Skeleton variant="circular" width={48} height={48} />
          <Skeleton variant="circular" width={56} height={56} />
        </Stack>
      </Box>
      <Box>
        <h3>Card Skeletons</h3>
        <Stack direction="row" spacing={2}>
          <Skeleton variant="rectangular" width={200} height={120} />
          <Skeleton variant="rectangular" width={200} height={120} />
          <Skeleton variant="rectangular" width={200} height={120} />
        </Stack>
      </Box>
    </Stack>
  ),
  name: 'Combined Variants',
};
