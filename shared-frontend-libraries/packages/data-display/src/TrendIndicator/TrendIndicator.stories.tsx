import type { Meta, StoryObj } from '@storybook/react';
import { TrendIndicator } from './TrendIndicator';

const meta = {
  title: 'Data Display/TrendIndicator',
  component: TrendIndicator,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof TrendIndicator>;

export default meta;
type Story = StoryObj<typeof TrendIndicator>;

export const Default: Story = {
  args: {
    value: 15,
    label: 'vs last month',
  },
};

export const PositiveTrend: Story = {
  args: {
    value: 25,
    label: 'vs last month',
  },
};

export const NegativeTrend: Story = {
  args: {
    value: -10,
    label: 'vs last month',
  },
};

export const FlatTrend: Story = {
  args: {
    value: 0,
    label: 'no change',
  },
};

export const WithPreviousValue: Story = {
  args: {
    value: 120,
    previousValue: 100,
    label: 'vs last quarter',
  },
};

export const WithoutIcon: Story = {
  args: {
    value: 15,
    showIcon: false,
  },
};

export const ChipVariant: Story = {
  args: {
    value: 15,
    variant: 'chip',
    label: 'vs last month',
  },
};

export const CompactVariant: Story = {
  args: {
    value: 15,
    variant: 'compact',
  },
};

export const Small: Story = {
  args: {
    value: 15,
    size: 'small',
  },
};

export const Large: Story = {
  args: {
    value: 15,
    size: 'large',
  },
};

export const CustomPrecision: Story = {
  args: {
    value: 15.345,
    precision: 2,
  },
};

export const CustomFormat: Story = {
  args: {
    value: 1234.56,
    format: (value) => `$${value.toLocaleString()}`,
  },
};

export const SuccessColor: Story = {
  args: {
    value: 15,
    color: 'success',
  },
};

export const ErrorColor: Story = {
  args: {
    value: -10,
    color: 'error',
  },
};

export const NeutralColor: Story = {
  args: {
    value: 0,
    color: 'neutral',
  },
};

export const DifferentSizes: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
      <TrendIndicator value={15} size="small" />
      <TrendIndicator value={15} size="medium" />
      <TrendIndicator value={15} size="large" />
    </div>
  ),
};

export const DifferentVariants: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
      <TrendIndicator value={15} variant="default" label="Default" />
      <TrendIndicator value={15} variant="chip" label="Chip" />
      <TrendIndicator value={15} variant="compact" />
    </div>
  ),
};

export const MultipleTrends: Story = {
  render: () => (
    <div style={{ display: 'grid', gap: '16px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <span>Revenue:</span>
        <TrendIndicator value={12500} previousValue={10000} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <span>Users:</span>
        <TrendIndicator value={-150} previousValue={5000} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <span>Errors:</span>
        <TrendIndicator value={0} previousValue={12} />
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <span>Uptime:</span>
        <TrendIndicator value={0.5} precision={2} label="%" />
      </div>
    </div>
  ),
};

export const CompactWithPercentage: Story = {
  args: {
    value: 12.5,
    variant: 'compact',
  },
};

export const ChipTrends: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
      <TrendIndicator value={25} variant="chip" />
      <TrendIndicator value={-10} variant="chip" />
      <TrendIndicator value={0} variant="chip" />
    </div>
  ),
};
