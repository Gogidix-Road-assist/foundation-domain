import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import { Slider } from './Slider';

const meta: Meta<typeof Slider> = {
  title: 'Components/Slider',
  component: Slider,
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
    showValue: { control: 'boolean' },
  },
};

export default meta;
type Story = StoryObj<typeof Slider>;

export const Default: Story = {
  render: () => {
    const [value, setValue] = useState(50);
    return <Slider value={value} onChange={(_, val) => setValue(val as number)} />;
  },
};

export const WithLabel: Story = {
  render: () => {
    const [value, setValue] = useState(50);
    return (
      <Slider
        label="Volume"
        value={value}
        onChange={(_, val) => setValue(val as number)}
        showValue
      />
    );
  },
};

export const Small: Story = {
  render: () => {
    const [value, setValue] = useState(30);
    return (
      <Slider
        size="small"
        value={value}
        onChange={(_, val) => setValue(val as number)}
      />
    );
  },
};

export const Error: Story = {
  render: () => {
    const [value, setValue] = useState(70);
    return (
      <Slider
        color="error"
        value={value}
        onChange={(_, val) => setValue(val as number)}
      />
    );
  },
};

export const Disabled: Story = {
  args: {
    disabled: true,
    value: 50,
  },
};

export const WithMarks: Story = {
  render: () => {
    const [value, setValue] = useState(50);
    return (
      <Slider
        value={value}
        onChange={(_, val) => setValue(val as number)}
        marks={[
          { value: 0, label: '0%' },
          { value: 25, label: '25%' },
          { value: 50, label: '50%' },
          { value: 75, label: '75%' },
          { value: 100, label: '100%' },
        ]}
      />
    );
  },
};

export const Range: Story = {
  render: () => {
    const [value, setValue] = useState([20, 80]);
    return (
      <Slider
        value={value}
        onChange={(_, val) => setValue(val as number[])}
        valueLabelDisplay="auto"
      />
    );
  },
};
EOF && cat > "C:/Users/HP/Desktop/Gogidix-Road-Assist-Saas/Rapid-Assist/Foundation-Domain/shared-frontend-libraries/packages/components/src/Slider/index.ts" << 'EOF'
export * from './Slider';
