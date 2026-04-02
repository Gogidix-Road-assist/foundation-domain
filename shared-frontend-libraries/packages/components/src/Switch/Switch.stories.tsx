import type { Meta, StoryObj } from '@storybook/react';
import { Switch } from './Switch';

const meta: Meta<typeof Switch> = {
  title: 'Components/Switch',
  component: Switch,
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
    checked: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Switch>;

export const Default: Story = {
  args: {
    label: 'Enable notifications',
  },
};

export const Checked: Story = {
  args: {
    label: 'Feature enabled',
    checked: true,
  },
};

export const Small: Story = {
  args: {
    label: 'Small switch',
    size: 'small',
  },
};

export const Error: Story = {
  args: {
    label: 'Danger zone',
    color: 'error',
  },
};

export const Disabled: Story = {
  args: {
    label: 'Disabled switch',
    disabled: true,
  },
};

export const WithoutLabel: Story = {
  args: {
    checked: true,
  },
};

export const LabelStart: Story = {
  args: {
    label: 'Label on left',
    labelPlacement: 'start',
  },
};
EOF && cat > "C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-frontend-libraries/packages/components/src/Switch/index.ts" << 'EOF'
export * from './Switch';
