import type { Meta, StoryObj } from '@storybook/react';
import { Box, Stack } from '@mui/material';
import { Progress, CircularProgress, ProgressSteps } from './Progress';

const meta: Meta<typeof Progress> = {
  title: 'Components/Progress',
  component: Progress,
  tags: ['autodocs'],
  argTypes: {
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'success', 'warning', 'error'],
    },
    variant: {
      control: 'select',
      options: ['determinate', 'indeterminate', 'buffer'],
    },
    size: {
      control: 'select',
      options: ['small', 'medium', 'large'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Progress>;

export const Default: Story = {
  args: {
    value: 60,
  },
};

export const WithLabel: Story = {
  args: {
    value: 75,
    label: 'Upload Progress',
    showValue: true,
  },
};

export const Colors: Story = {
  render: () => (
    <Stack spacing={3}>
      <Progress value={80} color="primary" label="Primary" />
      <Progress value={65} color="success" label="Success" />
      <Progress value={45} color="warning" label="Warning" />
      <Progress value={30} color="error" label="Error" />
    </Stack>
  ),
};

export const Sizes: Story = {
  render: () => (
    <Stack spacing={3}>
      <Progress value={50} size="small" label="Small" />
      <Progress value={50} size="medium" label="Medium" />
      <Progress value={50} size="large" label="Large" />
    </Stack>
  ),
};

export const Indeterminate: Story = {
  args: {
    variant: 'indeterminate',
    label: 'Loading...',
  },
};

export const Buffer: Story = {
  args: {
    variant: 'buffer',
    value: 60,
    label: 'Buffering...',
  },
};

export const CircularIndeterminate: StoryObj = {
  render: () => (
    <Stack direction="row" spacing={3} alignItems="center">
      <CircularProgress size={24} />
      <CircularProgress size={40} />
      <CircularProgress size={56} />
    </Stack>
  ),
};

export const CircularDeterminate: StoryObj = {
  render: () => (
    <Stack direction="row" spacing={3} alignItems="center">
      <CircularProgress value={25} showValue size={56} />
      <CircularProgress value={50} showValue size={56} />
      <CircularProgress value={75} showValue size={56} />
      <CircularProgress value={100} showValue size={56} color="success" />
    </Stack>
  ),
};

export const CircularColors: StoryObj = {
  render: () => (
    <Stack direction="row" spacing={3} alignItems="center">
      <CircularProgress value={60} showValue color="primary" />
      <CircularProgress value={60} showValue color="success" />
      <CircularProgress value={60} showValue color="warning" />
      <CircularProgress value={60} showValue color="error" />
    </Stack>
  ),
};

export const ProgressStepsDefault: StoryObj = {
  render: () => (
    <ProgressSteps
      currentStep={1}
      steps={[
        { label: 'Account', completed: true },
        { label: 'Profile', active: true },
        { label: 'Preferences' },
        { label: 'Review' },
      ]}
    />
  ),
};

export const ProgressStepsComplete: StoryObj = {
  render: () => (
    <ProgressSteps
      currentStep={4}
      steps={[
        { label: 'Account', completed: true },
        { label: 'Profile', completed: true },
        { label: 'Preferences', completed: true },
        { label: 'Review', completed: true },
      ]}
    />
  ),
};

export const ProgressStepsWithError: StoryObj = {
  render: () => (
    <ProgressSteps
      currentStep={2}
      steps={[
        { label: 'Upload', completed: true },
        { label: 'Process', error: true },
        { label: 'Complete' },
      ]}
    />
  ),
};

export const FileUpload: StoryObj = {
  render: () => (
    <Box sx={{ p: 4, maxWidth: 500 }}>
      <Progress
        value={78}
        label="Uploading document.pdf"
        showValue
        size="large"
        color="primary"
      />
      <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between' }}>
        <span style={{ fontSize: '0.875rem', color: '#6B7280' }}>
          156 MB of 200 MB
        </span>
        <span style={{ fontSize: '0.875rem', color: '#6B7280' }}>
          2 min remaining
        </span>
      </Box>
    </Box>
  ),
};

export const LoadingStates: StoryObj = {
  render: () => (
    <Stack spacing={4} sx={{ p: 4 }}>
      <Box>
        <Box sx={{ mb: 2 }}>Processing data...</Box>
        <Progress variant="indeterminate" color="primary" />
      </Box>
      <Box>
        <Box sx={{ mb: 2 }}>Uploading files...</Box>
        <Progress variant="buffer" value={45} color="success" />
      </Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 3 }}>
        <CircularProgress />
        <span>Loading...</span>
      </Box>
    </Stack>
  ),
};
