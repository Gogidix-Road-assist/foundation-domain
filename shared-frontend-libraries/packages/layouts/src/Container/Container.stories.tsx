import type { Meta, StoryObj } from '@storybook/react';
import { Container } from './Container';

const meta: Meta<typeof Container> = {
  title: 'Layouts/Container',
  component: Container,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium', 'large', 'full'],
    },
    centered: { control: 'boolean' },
    noPadding: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Container>;

export const Default: Story = {
  args: {
    children: <div>Default container content</div>,
  },
};

export const Small: Story = {
  args: {
    size: 'small',
    children: <div>Small container content</div>,
  },
};

export const Medium: Story = {
  args: {
    size: 'medium',
    children: <div>Medium container content</div>,
  },
};

export const Large: Story = {
  args: {
    size: 'large',
    children: <div>Large container content</div>,
  },
};

export const FullWidth: Story = {
  args: {
    size: 'full',
    children: <div>Full width container content</div>,
  },
};

export const Centered: Story = {
  args: {
    centered: true,
    children: <div>Centered content</div>,
  },
};

export const NoPadding: Story = {
  args: {
    noPadding: true,
    children: <div>Content without padding</div>,
  },
};
