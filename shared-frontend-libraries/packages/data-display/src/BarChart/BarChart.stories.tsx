import type { Meta, StoryObj } from '@storybook/react';
import { BarChart } from './BarChart';

const meta = {
  title: 'Data Display/BarChart',
  component: BarChart,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof BarChart>;

export default meta;
type Story = StoryObj<typeof BarChart>;

const sampleData = [
  { name: 'Jan', revenue: 4000, expenses: 2400 },
  { name: 'Feb', revenue: 3000, expenses: 1398 },
  { name: 'Mar', revenue: 2000, expenses: 9800 },
  { name: 'Apr', revenue: 2780, expenses: 3908 },
  { name: 'May', revenue: 1890, expenses: 4800 },
  { name: 'Jun', revenue: 2390, expenses: 3800 },
  { name: 'Jul', revenue: 3490, expenses: 4300 },
];

const series = [
  { dataKey: 'revenue', name: 'Revenue', color: '#10B981' },
  { dataKey: 'expenses', name: 'Expenses', color: '#EF4444' },
];

export const Default: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Monthly Revenue',
    height: 350,
  },
};

export const WithSubtitle: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Monthly Revenue',
    subtitle: 'Revenue by month for the current year',
    height: 350,
  },
};

export const Horizontal: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Monthly Revenue (Horizontal)',
    layout: 'horizontal',
    height: 350,
  },
};

export const WithoutGrid: Story = {
  args: {
    data: sampleData,
    series,
    showGrid: false,
    height: 350,
  },
};

export const WithoutLegend: Story = {
  args: {
    data: sampleData,
    series,
    showLegend: false,
    height: 350,
  },
};

export const WithAxisLabels: Story = {
  args: {
    data: sampleData,
    series,
    xAxisLabel: 'Month',
    yAxisLabel: 'Amount ($)',
    height: 350,
  },
};

export const SingleSeries: Story = {
  args: {
    data: sampleData,
    series: [{ dataKey: 'revenue', name: 'Revenue', color: '#3B82F6' }],
    title: 'Revenue Only',
    height: 350,
  },
};

export const CustomColors: Story = {
  args: {
    data: sampleData,
    series,
    colors: ['#8B5CF6', '#EC4899'],
    height: 350,
  },
};

export const CustomSize: Story = {
  args: {
    data: sampleData,
    series,
    height: 250,
    width: '80%',
  },
};
