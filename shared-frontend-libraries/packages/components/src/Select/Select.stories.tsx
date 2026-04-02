import type { Meta, StoryObj } from '@storybook/react';
import { Select } from './Select';

const meta: Meta<typeof Select> = {
  title: 'Components/Select',
  component: Select,
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['small', 'medium'],
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
type Story = StoryObj<typeof Select>;

export const Default: Story = {
  args: {
    label: 'Select an option',
    options: [
      { value: 'option1', label: 'Option 1' },
      { value: 'option2', label: 'Option 2' },
      { value: 'option3', label: 'Option 3' },
    ],
  },
};

export const Small: Story = {
  args: {
    label: 'Small Select',
    size: 'small',
    options: [
      { value: 'a', label: 'Option A' },
      { value: 'b', label: 'Option B' },
    ],
  },
};

export const Disabled: Story = {
  args: {
    label: 'Disabled Select',
    disabled: true,
    options: [
      { value: '1', label: 'Option 1' },
      { value: '2', label: 'Option 2' },
    ],
  },
};

export const Error: Story = {
  args: {
    label: 'Error State',
    error: true,
    helperText: 'Please select an option',
    options: [
      { value: '1', label: 'Option 1' },
    ],
  },
};
EOF && cat > "C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-frontend-libraries/packages/components/src/Select/index.ts" << 'EOF'
export * from './Select';
