import type { Meta, StoryObj } from '@storybook/react';
import { Chip } from './Chip';

const meta: Meta<typeof Chip> = {
  title: 'Components/Chip',
  component: Chip,
  tags: ['autodocs'],
  argTypes: {
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'success', 'warning', 'error', 'info', 'default'],
    },
    size: {
      control: 'select',
      options: ['small', 'medium'],
    },
    variant: {
      control: 'select',
      options: ['filled', 'outlined'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Chip>;

export const Default: Story = {
  args: {
    children: 'Default Chip',
  },
};

export const Colors: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip color="primary">Primary</Chip>
      <Chip color="secondary">Secondary</Chip>
      <Chip color="success">Success</Chip>
      <Chip color="warning">Warning</Chip>
      <Chip color="error">Error</Chip>
      <Chip color="info">Info</Chip>
      <Chip color="default">Default</Chip>
    </div>
  ),
};

export const Outlined: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip color="primary" variant="outlined">Primary</Chip>
      <Chip color="success" variant="outlined">Success</Chip>
      <Chip color="error" variant="outlined">Error</Chip>
    </div>
  ),
};

export const Sizes: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
      <Chip size="small">Small</Chip>
      <Chip size="medium">Medium</Chip>
    </div>
  ),
};

export const Clickable: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip onClick={() => console.log('clicked')}>React</Chip>
      <Chip onClick={() => console.log('clicked')}>TypeScript</Chip>
      <Chip onClick={() => console.log('clicked')}>Material UI</Chip>
    </div>
  ),
};

export const Deletable: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip onDelete={() => console.log('deleted')}>Tag 1</Chip>
      <Chip onDelete={() => console.log('deleted')}>Tag 2</Chip>
      <Chip onDelete={() => console.log('deleted')}>Tag 3</Chip>
    </div>
  ),
};

export const StatusChips: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip color="success" variant="outlined">Active</Chip>
      <Chip color="warning" variant="outlined">Pending</Chip>
      <Chip color="error" variant="outlined">Inactive</Chip>
      <Chip color="info" variant="outlined">Review</Chip>
    </div>
  ),
};

export const TechStack: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
      <Chip color="primary" variant="outlined" onClick={() => {}}>
        React
      </Chip>
      <Chip color="primary" variant="outlined" onClick={() => {}}>
        TypeScript
      </Chip>
      <Chip color="success" variant="outlined" onClick={() => {}}>
        Next.js
      </Chip>
      <Chip color="info" variant="outlined" onClick={() => {}}>
        Tailwind
      </Chip>
    </div>
  ),
};
