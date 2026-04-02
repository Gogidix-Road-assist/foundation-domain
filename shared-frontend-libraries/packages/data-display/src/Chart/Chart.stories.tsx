import type { Meta, StoryObj } from '@storybook/react';
import { Chart, AnimatedChart } from './Chart';

const meta = {
  title: 'Data Display/Chart',
  component: Chart,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof Chart>;

export default meta;
type Story = StoryObj<typeof Chart>;

const sampleData = [
  { name: 'Jan', revenue: 4000, expenses: 2400, profit: 1600 },
  { name: 'Feb', revenue: 3000, expenses: 1398, profit: 1602 },
  { name: 'Mar', revenue: 2000, expenses: 9800, profit: -7800 },
  { name: 'Apr', revenue: 2780, expenses: 3908, profit: -1128 },
  { name: 'May', revenue: 1890, expenses: 4800, profit: -2910 },
  { name: 'Jun', revenue: 2390, expenses: 3800, profit: -1410 },
  { name: 'Jul', revenue: 3490, expenses: 4300, profit: -810 },
  { name: 'Aug', revenue: 4000, expenses: 2400, profit: 1600 },
  { name: 'Sep', revenue: 3800, expenses: 2100, profit: 1700 },
  { name: 'Oct', revenue: 4300, expenses: 2800, profit: 1500 },
  { name: 'Nov', revenue: 4500, expenses: 2700, profit: 1800 },
  { name: 'Dec', revenue: 5200, expenses: 3200, profit: 2000 },
];

const pieData = [
  { name: 'Product A', value: 400 },
  { name: 'Product B', value: 300 },
  { name: 'Product C', value: 200 },
  { name: 'Product D', value: 100 },
  { name: 'Product E', value: 80 },
];

const series = [
  { dataKey: 'revenue', name: 'Revenue' },
  { dataKey: 'expenses', name: 'Expenses' },
  { dataKey: 'profit', name: 'Profit' },
];

export const LineChart: Story = {
  args: {
    type: 'line',
    data: sampleData,
    series: series.slice(0, 2),
    title: 'Revenue vs Expenses',
    height: 400,
  },
};

export const BarChart: Story = {
  args: {
    type: 'bar',
    data: sampleData,
    series: series.slice(0, 2),
    title: 'Monthly Revenue',
    height: 400,
  },
};

export const PieChart: Story = {
  args: {
    type: 'pie',
    data: pieData,
    series: [{ dataKey: 'value', name: 'Sales' }],
    title: 'Product Distribution',
    height: 400,
  },
};

export const Animated: Story = {
  render: (args) => <AnimatedChart {...args} />,
  args: {
    type: 'line',
    data: sampleData,
    series: series.slice(0, 2),
    title: 'Animated Revenue Chart',
    height: 400,
  },
};

export const WithoutLegend: Story = {
  args: {
    type: 'bar',
    data: sampleData,
    series: series.slice(0, 2),
    showLegend: false,
    height: 400,
  },
};

export const WithoutGrid: Story = {
  args: {
    type: 'line',
    data: sampleData,
    series: series.slice(0, 2),
    showGrid: false,
    height: 400,
  },
};

export const CustomColors: Story = {
  args: {
    type: 'bar',
    data: sampleData,
    series: [
      { dataKey: 'revenue', name: 'Revenue' },
      { dataKey: 'expenses', name: 'Expenses' },
    ],
    colors: ['#10B981', '#EF4444'],
    height: 400,
  },
};

export const WithAxisLabels: Story = {
  args: {
    type: 'line',
    data: sampleData,
    series: series.slice(0, 2),
    xAxisLabel: 'Month',
    yAxisLabel: 'Amount ($)',
    height: 400,
  },
};

export const StepLineChart: Story = {
  args: {
    type: 'line',
    data: sampleData,
    series: [
      { dataKey: 'revenue', name: 'Revenue', type: 'step' },
    ],
    title: 'Step Chart',
    height: 400,
  },
};

export const CustomSize: Story = {
  args: {
    type: 'bar',
    data: sampleData,
    series: series.slice(0, 2),
    height: 200,
    width: '80%',
  },
};

export const ThreeSeries: Story = {
  args: {
    type: 'line',
    data: sampleData,
    series,
    title: 'Revenue, Expenses & Profit',
    height: 400,
  },
};

export const DonutChart: Story = {
  args: {
    type: 'pie',
    data: pieData,
    series: [{ dataKey: 'value', name: 'Sales' }],
    title: 'Product Sales',
    height: 400,
    margin: { top: 0, right: 0, left: 0, bottom: 0 },
  },
};
