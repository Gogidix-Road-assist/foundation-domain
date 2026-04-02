import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Radio, RadioGroup } from './Radio';

const meta: Meta<typeof Radio> = {
  title: 'Components/Radio',
  component: Radio,
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
type Story = StoryObj<typeof Radio>;

export const Default: Story = {
  args: {
    label: 'Option 1',
    value: 'option1',
  },
};

export const Checked: Story = {
  args: {
    label: 'Selected option',
    value: 'option1',
    checked: true,
  },
};

export const Small: Story = {
  args: {
    label: 'Small radio',
    size: 'small',
  },
};

export const Error: Story = {
  args: {
    label: 'Error color',
    color: 'error',
  },
};

export const Disabled: Story = {
  args: {
    label: 'Disabled radio',
    disabled: true,
  },
};

export const RadioGroupExample: StoryObj = {
  render: () => {
    const [value, setValue] = useState('option1');
    return (
      <RadioGroup
        name="example"
        value={value}
        onChange={(e, val) => setValue(val)}
        options={[
          { value: 'option1', label: 'Option 1' },
          { value: 'option2', label: 'Option 2' },
          { value: 'option3', label: 'Option 3' },
        ]}
      />
    );
  },
};

export const RadioGroupRow: StoryObj = {
  render: () => {
    const [value, setValue] = useState('option1');
    return (
      <RadioGroup
        name="row-example"
        value={value}
        onChange={(e, val) => setValue(val)}
        options={[
          { value: 'option1', label: 'Option 1' },
          { value: 'option2', label: 'Option 2' },
        ]}
        row
      />
    );
  },
};
EOF && cat > "C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-frontend-libraries/packages/components/src/Radio/index.ts" << 'EOF'
export * from './Radio';
