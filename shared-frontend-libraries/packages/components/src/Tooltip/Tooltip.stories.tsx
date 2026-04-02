import type { Meta, StoryObj } from '@storybook/react';
import { Button } from '../Button';
import { Tooltip } from './Tooltip';

const meta: Meta<typeof Tooltip> = {
  title: 'Components/Tooltip',
  component: Tooltip,
  tags: ['autodocs'],
  argTypes: {
    placement: {
      control: 'select',
      options: [
        'top',
        'top-start',
        'top-end',
        'right',
        'right-start',
        'right-end',
        'bottom',
        'bottom-start',
        'bottom-end',
        'left',
        'left-start',
        'left-end',
      ],
    },
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'success', 'warning', 'error', 'info', 'default'],
    },
    arrow: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Tooltip>;

export const Default: Story = {
  args: {
    title: 'Hover to see tooltip',
    children: <Button>Hover me</Button>,
  },
};

export const Placements: StoryObj = {
  render: () => (
    <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap', justifyContent: 'center' }}>
      <Tooltip title="Top placement" placement="top">
        <Button>Top</Button>
      </Tooltip>
      <Tooltip title="Bottom placement" placement="bottom">
        <Button>Bottom</Button>
      </Tooltip>
      <Tooltip title="Left placement" placement="left">
        <Button>Left</Button>
      </Tooltip>
      <Tooltip title="Right placement" placement="right">
        <Button>Right</Button>
      </Tooltip>
    </div>
  ),
  name: 'Different Placements',
};

export const Colors: StoryObj = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
      <Tooltip title="Primary tooltip" color="primary">
        <Button>Primary</Button>
      </Tooltip>
      <Tooltip title="Success tooltip" color="success">
        <Button>Success</Button>
      </Tooltip>
      <Tooltip title="Warning tooltip" color="warning">
        <Button>Warning</Button>
      </Tooltip>
      <Tooltip title="Error tooltip" color="error">
        <Button>Error</Button>
      </Tooltip>
      <Tooltip title="Info tooltip" color="info">
        <Button>Info</Button>
      </Tooltip>
    </div>
  ),
  name: 'Color Variants',
};

export const WithoutArrow: Story = {
  args: {
    title: 'No arrow tooltip',
    arrow: false,
    children: <Button>No Arrow</Button>,
  },
};

export const LongText: Story = {
  args: {
    title: 'This is a longer tooltip text that provides additional context or information about the element being hovered.',
    children: <Button>Long Tooltip</Button>,
  },
};

export const CustomDelay: Story = {
  args: {
    title: 'Delayed tooltip (500ms)',
    delay: 500,
    children: <Button>Delay 500ms</Button>,
  },
};

export const WithIcon: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px' }}>
      <Tooltip title="Settings">
        <span style={{ fontSize: '24px', cursor: 'pointer' }}>⚙️</span>
      </Tooltip>
      <Tooltip title="Help">
        <span style={{ fontSize: '24px', cursor: 'pointer' }}>❓</span>
      </Tooltip>
      <Tooltip title="Information">
        <span style={{ fontSize: '24px', cursor: 'pointer' }}>ℹ️</span>
      </Tooltip>
      <Tooltip title="Delete" color="error">
        <span style={{ fontSize: '24px', cursor: 'pointer' }}>🗑️</span>
      </Tooltip>
    </div>
  ),
};

export const OnText: Story = {
  args: {
    title: 'This term has a helpful explanation',
    children: <span style={{ borderBottom: '1px dotted #9CA3AF', cursor: 'help' }}>
      complex terminology
    </span>,
  },
};

export const ActionTooltip: StoryObj = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px' }}>
      <Tooltip title="Download file" color="success">
        <span style={{ fontSize: '20px', cursor: 'pointer' }}>⬇️</span>
      </Tooltip>
      <Tooltip title="Edit item">
        <span style={{ fontSize: '20px', cursor: 'pointer' }}>✏️</span>
      </Tooltip>
      <Tooltip title="Copy to clipboard" color="info">
        <span style={{ fontSize: '20px', cursor: 'pointer' }}>📋</span>
      </Tooltip>
      <Tooltip title="Delete permanently" color="error">
        <span style={{ fontSize: '20px', cursor: 'pointer' }}>🗑️</span>
      </Tooltip>
    </div>
  ),
};

export const FormFieldTooltip: StoryObj = {
  render: () => (
    <div style={{ maxWidth: '400px' }}>
      <label style={{ display: 'block', marginBottom: '8px', fontWeight: 500 }}>
        Email Address{' '}
        <Tooltip title="We'll send important notifications to this email" placement="right">
          <span style={{ cursor: 'help', color: '#9CA3AF', marginLeft: '4px' }}>❓</span>
        </Tooltip>
      </label>
      <input
        type="email"
        placeholder="your@email.com"
        style={{
          width: '100%',
          padding: '10px',
          border: '1px solid #D1D5DB',
          borderRadius: '6px',
        }}
      />
    </div>
  ),
};

export const InteractiveElement: StoryObj = {
  render: () => (
    <Tooltip title="Click to open menu" placement="top">
      <button style={{
        padding: '8px 16px',
        borderRadius: '6px',
        border: 'none',
        backgroundColor: '#F3F4F6',
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
      }}>
        <span>Actions</span>
        <span style={{ fontSize: '12px' }}>▼</span>
      </button>
    </Tooltip>
  ),
};
