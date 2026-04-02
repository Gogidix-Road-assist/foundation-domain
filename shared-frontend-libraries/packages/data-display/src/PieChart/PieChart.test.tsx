import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { PieChart, DonutChart } from './PieChart';

const sampleData = [
  { name: 'Product A', value: 400 },
  { name: 'Product B', value: 300 },
  { name: 'Product C', value: 200 },
  { name: 'Product D', value: 100 },
];

describe('PieChart', () => {
  it('renders pie chart', () => {
    render(<PieChart data={sampleData} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with title', () => {
    render(<PieChart data={sampleData} title="Product Distribution" height={300} />);

    expect(screen.getByText('Product Distribution')).toBeInTheDocument();
  });

  it('renders with subtitle', () => {
    render(
      <PieChart
        data={sampleData}
        title="Products"
        subtitle="Sales by product category"
        height={300}
      />
    );

    expect(screen.getByText('Products')).toBeInTheDocument();
    expect(screen.getByText('Sales by product category')).toBeInTheDocument();
  });

  it('hides legend when showLegend is false', () => {
    render(<PieChart data={sampleData} showLegend={false} height={300} />);

    const legend = document.querySelector('.recharts-legend-wrapper');
    expect(legend).not.toBeInTheDocument();
  });

  it('hides labels when showLabel is false', () => {
    render(<PieChart data={sampleData} showLabel={false} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with custom innerRadius (donut)', () => {
    render(<PieChart data={sampleData} innerRadius={60} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with custom colors', () => {
    const customColors = ['#FF0000', '#00FF00', '#0000FF', '#FFFF00'];

    render(<PieChart data={sampleData} colors={customColors} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with custom outerRadius', () => {
    render(<PieChart data={sampleData} outerRadius={100} height={400} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('handles empty data', () => {
    render(<PieChart data={[]} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });
});

describe('DonutChart', () => {
  it('renders donut chart', () => {
    render(<DonutChart data={sampleData} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with title', () => {
    render(<DonutChart data={sampleData} title="Donut Chart" height={300} />);

    expect(screen.getByText('Donut Chart')).toBeInTheDocument();
  });
});
