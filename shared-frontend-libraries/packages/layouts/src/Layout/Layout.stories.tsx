import type { Meta, StoryObj } from '@storybook/react';
import { Layout } from './Layout';

const meta: Meta<typeof Layout> = {
  title: 'Layouts/Layout',
  component: Layout,
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: 'select',
      options: ['default', 'minimal', 'fullscreen', 'sidebar'],
    },
  },
};

export default meta;
type Story = StoryObj<typeof Layout>;

export const Default: Story = {
  args: {
    children: <div>Default layout</div>,
  },
};

export const Minimal: Story = {
  args: {
    type: 'minimal',
    children: <div>Minimal layout</div>,
  },
};

export const Fullscreen: Story = {
  args: {
    type: 'fullscreen',
    children: <div>Fullscreen layout</div>,
  },
};
