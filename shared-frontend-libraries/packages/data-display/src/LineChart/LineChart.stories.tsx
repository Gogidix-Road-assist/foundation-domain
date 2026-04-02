import type { Meta, StoryObj } from '@storybook/react';
import { LineChart } from './LineChart';

const meta = {
  title: 'Data Display/LineChart',
  component: LineChart,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof LineChart>;

export default meta;
type Story = StoryObj<typeof LineChart>;

const sampleData = [
  { name: 'Jan', revenue: 4000, expenses: 2400 },
  { name: 'Feb', revenue: 3000, expenses: 1398 },
  { name: 'Mar', revenue: 2000, expenses: 9800 },
  { name: 'Apr', revenue: 2780, expenses: 3908 },
  { name: 'May', revenue: 1890, expenses: 4800 },
  { name: 'Jun', revenue: 2390, expenses: 3800 },
  { name: 'Jul', revenue: 3490, expenses: 4300 },
  { name: 'Aug', revenue: 4000, expenses: 2400 },
  { name: 'Sep', revenue: 3800, expenses: 2100 },
  { name: 'Oct', revenue: 4300, expenses: 2800 },
  { name: 'Nov', revenue: 4500, expenses: 2700 },
  { name: 'Dec', revenue: 5200, expenses: 3200 },
];

const series = [
  { dataKey: 'revenue', name: 'Revenue', color: '#10B981' },
  { dataKey: 'expenses', name: 'Expenses', color: '#EF4444' },
];

export const Default: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Revenue vs Expenses',
    height: 350,
  },
};

export const WithSubtitle: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Revenue vs Expenses',
    subtitle: 'Monthly financial performance',
    height: 350,
  },
};

export const WithoutDots: Story = {
  args: {
    data: sampleData,
    series,
    showDots: false,
    height: 350,
  },
};

export const AreaChart: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Revenue Trend',
    showArea: true,
    height: 350,
  },
};

export const StepChart: Story = {
  args: {
    data: sampleData,
    series,
    title: 'Revenue Step Chart',
    curveType: 'step',
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

export const CustomColors: Story = {
  args: {
    data: sampleData,
    series,
    colors: ['#8B5CF6', '#EC4899'],
    height: 350,
  },
};

export const ThreeSeries: Story = {
  args: {
    data: sampleData.map((d) => ({
      ...d,
      profit: d.revenue - d.expenses,
    })),
    series: [
      { dataKey: 'revenue', name: 'Revenue' },
      { dataKey: 'expenses', name: 'Expenses' },
      { dataKey: 'profit', name: 'Profit' },
    ],
    title: 'Complete Financial View',
    height: 350,
  },
};
