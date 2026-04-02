import type { Meta, StoryObj } from '@storybook/react';
import { Input } from './Input';

const meta: Meta<typeof Input> = {
  title: 'Components/Input',
  component: Input,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium', 'large'],
    },
    variant: {
      control: 'select',
      options: ['outlined', 'filled', 'standard'],
    },
    disabled: { control: 'boolean' },
    error: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Input>;

export const Default: Story = {
  args: {
    placeholder: 'Enter text',
    label: 'Input Label',
  },
};

export const Small: Story = {
  args: {
    size: 'small',
    placeholder: 'Small input',
    label: 'Small Input',
  },
};

export const Large: Story = {
  args: {
    size: 'large',
    placeholder: 'Large input',
    label: 'Large Input',
  },
};

export const Disabled: Story = {
  args: {
    disabled: true,
    placeholder: 'Disabled input',
    label: 'Disabled Input',
  },
};

export const Error: Story = {
  args: {
    error: true,
    placeholder: 'Error state',
    label: 'Error Input',
    helperText: 'This field is required',
  },
};

export const WithHelperText: Story = {
  args: {
    placeholder: 'Enter text',
    label: 'Label',
    helperText: 'Additional helper text',
  },
};
