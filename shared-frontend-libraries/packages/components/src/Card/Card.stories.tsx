import type { Meta, StoryObj } from '@storybook/react';
import { Card } from './Card';

const meta: Meta<typeof Card> = {
  title: 'Components/Card',
  component: Card,
  tags: ['autodocs'],
  argTypes: {
    elevation: {
      control: 'select',
      options: [0, 1, 2, 3, 4],
    },
    hoverable: {
      control: 'boolean',
    },
  },
};

export default meta;
type Story = StoryObj<typeof Card>;

export const Default: Story = {
  args: {
    children: (
      <div>
        <h3>Card Title</h3>
        <p>This is a default card content.</p>
      </div>
    ),
  },
};

export const Elevations: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
      <Card elevation={0}>Elevation 0</Card>
      <Card elevation={1}>Elevation 1</Card>
      <Card elevation={2}>Elevation 2</Card>
      <Card elevation={3}>Elevation 3</Card>
      <Card elevation={4}>Elevation 4</Card>
    </div>
  ),
};

export const Hoverable: Story = {
  args: {
    hoverable: true,
    children: (
      <div>
        <h3>Hoverable Card</h3>
        <p>Hover over this card to see the animation.</p>
      </div>
    ),
  },
};

export const CustomContent: Story = {
  args: {
    elevation: 2,
    children: (
      <div>
        <h3>Custom Card</h3>
        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
        <p>Sed do eiusmod tempor incididunt ut labore et dolore.</p>
      </div>
    ),
  },
};
