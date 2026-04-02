import type { Meta, StoryObj } from '@storybook/react';
import { Alert } from './Alert';

const meta: Meta<typeof Alert> = {
  title: 'Components/Alert',
  component: Alert,
  tags: ['autodocs'],
  argTypes: {
    severity: {
      control: 'select',
      options: ['success', 'info', 'warning', 'error'],
    },
    variant: {
      control: 'select',
      options: ['standard', 'outlined', 'filled'],
    },
    closable: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Alert>;

export const Success: Story = {
  args: {
    severity: 'success',
    children: 'Operation completed successfully!',
  },
};

export const Info: Story = {
  args: {
    severity: 'info',
    children: 'Please review your information before continuing.',
  },
};

export const Warning: Story = {
  args: {
    severity: 'warning',
    children: 'This action cannot be undone. Proceed with caution.',
  },
};

export const Error: Story = {
  args: {
    severity: 'error',
    children: 'An error occurred while processing your request.',
  },
};

export const WithTitle: Story = {
  args: {
    severity: 'warning',
    title: 'Attention Required',
    children: 'Your account needs verification before you can access all features.',
  },
};

export const Closable: Story = {
  args: {
    severity: 'info',
    closable: true,
    children: 'This alert can be dismissed by clicking the close button.',
  },
};

export const Variants: Story = {
  render: () => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <Alert severity="success" variant="standard">
        Standard Success
      </Alert>
      <Alert severity="success" variant="outlined">
        Outlined Success
      </Alert>
      <Alert severity="success" variant="filled">
        Filled Success
      </Alert>
    </div>
  ),
};

export const AllSeverities: Story = {
  render: () => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <Alert severity="success">
        <strong>Success:</strong> Your changes have been saved.
      </Alert>
      <Alert severity="info">
        <strong>Info:</strong> New features are available in settings.
      </Alert>
      <Alert severity="warning">
        <strong>Warning:</strong> Your session will expire in 5 minutes.
      </Alert>
      <Alert severity="error">
        <strong>Error:</strong> Failed to connect to the server.
      </Alert>
    </div>
  ),
};

export const InlineAlert: Story = {
  args: {
    severity: 'info",
    variant: 'outlined',
    children: 'Tip: Use keyboard shortcuts to navigate faster.',
  },
};

export const FormError: Story = {
  args: {
    severity: 'error',
    title: 'Form Validation Failed',
    children: (
      <ul style={{ margin: 0, paddingLeft: '20px' }}>
        <li>Email address is required</li>
        <li>Password must be at least 8 characters</li>
        <li>Phone number format is invalid</li>
      </ul>
    ),
  },
};
