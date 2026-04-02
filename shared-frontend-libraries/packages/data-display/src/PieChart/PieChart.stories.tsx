import type { Meta, StoryObj } from '@storybook/react';
import { PieChart, DonutChart } from './PieChart';

const meta = {
  title: 'Data Display/PieChart',
  component: PieChart,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
} satisfies Meta<typeof PieChart>;

export default meta;
type Story = StoryObj<typeof PieChart>;

const sampleData = [
  { name: 'Product A', value: 400 },
  { name: 'Product B', value: 300 },
  { name: 'Product C', value: 200 },
  { name: 'Product D', value: 100 },
  { name: 'Product E', value: 80 },
];

export const Default: Story = {
  args: {
    data: sampleData,
    title: 'Product Distribution',
    height: 350,
  },
};

export const WithSubtitle: Story = {
  args: {
    data: sampleData,
    title: 'Products',
    subtitle: 'Sales by product category',
    height: 350,
  },
};

export const Donut: Story = {
  render: (args) => <DonutChart {...args} />,
  args: {
    data: sampleData,
    title: 'Product Distribution (Donut)',
    height: 350,
  },
};

export const WithoutLabels: Story = {
  args: {
    data: sampleData,
    showLabel: false,
    height: 350,
  },
};

export const WithoutLegend: Story = {
  args: {
    data: sampleData,
    showLegend: false,
    height: 350,
  },
};

export const CustomColors: Story = {
  args: {
    data: sampleData,
    colors: ['#8B5CF6', '#EC4899', '#06B6D4', '#F59E0B', '#EF4444'],
    height: 350,
  },
};

export const LargeRadius: Story = {
  args: {
    data: sampleData,
    outerRadius: 100,
    height: 400,
  },
};

export const CustomInnerRadius: Story = {
  args: {
    data: sampleData,
    innerRadius: 40,
    title: 'Thick Donut',
    height: 350,
  },
};

export const FewItems: Story = {
  args: {
    data: [
      { name: 'Complete', value: 65 },
      { name: 'Pending', value: 25 },
      { name: 'Failed', value: 10 },
    ],
    colors: ['#10B981', '#F59E0B', '#EF4444'],
    title: 'Task Status',
    height: 350,
  },
};

export const ManyItems: Story = {
  args: {
    data: Array.from({ length: 12 }, (_, i) => ({
      name: `Item ${i + 1}`,
      value: Math.floor(Math.random() * 100) + 50,
    })),
    title: 'Many Items Distribution',
    height: 400,
    outerRadius: 120,
  },
};
