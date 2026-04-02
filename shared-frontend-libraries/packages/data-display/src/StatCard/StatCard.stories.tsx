import type { Meta, StoryObj } from '@storybook/react';
import { StatCard } from './StatCard';
import PeopleIcon from '@mui/icons-material/People';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import ErrorIcon from '@mui/icons-material/Error';

const meta = {
  title: 'Data Display/StatCard',
  component: StatCard,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof StatCard>;

export default meta;
type Story = StoryObj<typeof StatCard>;

export const Default: Story = {
  args: {
    title: 'Total Users',
    value: 2453,
    trend: { value: 12, label: 'vs last month' },
  },
};

export const WithIcon: Story = {
  args: {
    title: 'Total Orders',
    value: 892,
    icon: <ShoppingBagIcon />,
    color: 'primary',
  },
};

export const WithSubtitle: Story = {
  args: {
    title: 'Active Sessions',
    value: 432,
    subtitle: 'Currently online',
    icon: <AccessTimeIcon />,
    color: 'info',
  },
};

export const PositiveTrend: Story = {
  args: {
    title: 'Total Revenue',
    value: 45231,
    icon: <AttachMoneyIcon />,
    color: 'success',
    trend: { value: 23, label: 'vs last month' },
  },
};

export const NegativeTrend: Story = {
  args: {
    title: 'Errors',
    value: 12,
    icon: <ErrorIcon />,
    color: 'error',
    trend: { value: -15, label: 'vs last week' },
  },
};

export const GoodNegativeTrend: Story = {
  args: {
    title: 'Response Time',
    value: '245ms',
    icon: <AccessTimeIcon />,
    color: 'warning',
    trend: { value: -18, label: 'vs last week' },
  },
};

export const Small: Story = {
  args: {
    title: 'Users',
    value: 1234,
    size: 'small',
    trend: { value: 5, label: 'vs yesterday' },
  },
};

export const Large: Story = {
  args: {
    title: 'Total Revenue',
    value: 1234567,
    icon: <AttachMoneyIcon />,
    color: 'success',
    size: 'large',
    trend: { value: 12, label: 'vs last quarter' },
  },
};

export const Loading: Story = {
  args: {
    title: 'Total Users',
    value: 2453,
    icon: <PeopleIcon />,
    loading: true,
  },
};

export const NoTrend: Story = {
  args: {
    title: 'Total Users',
    value: 2453,
    icon: <PeopleIcon />,
  },
};

export const DifferentColors: Story = {
  render: () => (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '16px' }}>
      <StatCard
        title="Primary"
        value={1234}
        icon={<TrendingUpIcon />}
        color="primary"
        trend={{ value: 12 }}
      />
      <StatCard
        title="Success"
        value={5678}
        icon={<PeopleIcon />}
        color="success"
        trend={{ value: 8 }}
      />
      <StatCard
        title="Warning"
        value={89}
        icon={<ErrorIcon />}
        color="warning"
        trend={{ value: -5 }}
      />
      <StatCard
        title="Error"
        value={12}
        icon={<ErrorIcon />}
        color="error"
        trend={{ value: -3 }}
      />
      <StatCard
        title="Info"
        value={456}
        icon={<AccessTimeIcon />}
        color="info"
        trend={{ value: 2 }}
      />
    </div>
  ),
};

export const Clickable: Story = {
  args: {
    title: 'Click for Details',
    value: 1234,
    onClick: () => alert('Card clicked!'),
    trend: { value: 5 },
  },
};

export const FlatTrend: Story = {
  args: {
    title: 'Stable Metric',
    value: 5000,
    icon: <TrendingUpIcon />,
    color: 'secondary',
    trend: { value: 0, label: 'no change' },
  },
};

export const LargeValue: Story = {
  args: {
    title: 'Market Cap',
    value: 1234567890,
    icon: <AttachMoneyIcon />,
    color: 'success',
    trend: { value: 5, label: 'vs last year' },
  },
};

export const StringValue: Story = {
  args: {
    title: 'Status',
    value: 'Operational',
    color: 'success',
    icon: <TrendingUpIcon />,
  },
};

export const WithoutTrendIcon: Story = {
  args: {
    title: 'Total Users',
    value: 2453,
    icon: <PeopleIcon />,
    trend: { value: 12, label: 'vs last month' },
    showTrendIcon: false,
  },
};
