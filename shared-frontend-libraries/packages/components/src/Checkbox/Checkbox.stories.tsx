import type { Meta, StoryObj } from '@storybook/react';
import { Checkbox } from './Checkbox';

const meta: Meta<typeof Checkbox> = {
  title: 'Components/Checkbox',
  component: Checkbox,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium'],
    },
    color: {
      control: 'select',
      options: ['primary', 'secondary', 'error'],
    },
    disabled: { control: 'boolean' },
    indeterminate: { control: 'boolean' },
    checked: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Checkbox>;

export const Default: Story = {
  args: {
    label: 'Accept terms and conditions',
  },
};

export const Checked: Story = {
  args: {
    label: 'Checked state',
    checked: true,
  },
};

export const Small: Story = {
  args: {
    label: 'Small checkbox',
    size: 'small',
  },
};

export const Error: Story = {
  args: {
    label: 'Error color',
    color: 'error',
  },
};

export const Indeterminate: Story = {
  args: {
    label: 'Indeterminate state',
    indeterminate: true,
  },
};

export const Disabled: Story = {
  args: {
    label: 'Disabled checkbox',
    disabled: true,
  },
};

export const WithoutLabel: Story = {
  args: {
    checked: true,
  },
};
EOF && cat > "C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-frontend-libraries/packages/components/src/Checkbox/index.ts" << 'EOF'
export * from './Checkbox';
