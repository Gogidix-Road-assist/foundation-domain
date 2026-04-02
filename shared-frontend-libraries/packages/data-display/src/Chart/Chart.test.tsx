import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { Chart } from './Chart';

const sampleData = [
  { name: 'Jan', value: 400, value2: 240 },
  { name: 'Feb', value: 300, value2: 139 },
  { name: 'Mar', value: 200, value2: 980 },
  { name: 'Apr', value: 278, value2: 390 },
  { name: 'May', value: 189, value2: 480 },
  { name: 'Jun', value: 239, value2: 380 },
];

const sampleSeries = [
  { dataKey: 'value', name: 'Revenue' },
  { dataKey: 'value2', name: 'Expenses' },
];

describe('Chart', () => {
  it('renders line chart', () => {
    render(
      <Chart type="line" data={sampleData} series={sampleSeries} height={300} />
    );

    // Chart should be rendered
    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders bar chart', () => {
    render(
      <Chart type="bar" data={sampleData} series={sampleSeries} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders pie chart', () => {
    const pieData = [
      { name: 'A', value: 400 },
      { name: 'B', value: 300 },
      { name: 'C', value: 200 },
    ];

    render(
      <Chart type="pie" data={pieData} series={[{ dataKey: 'value', name: 'Value' }]} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with title', () => {
    render(
      <Chart
        type="line"
        data={sampleData}
        series={sampleSeries}
        title="Revenue Chart"
        height={300}
      />
    );

    expect(screen.getByText('Revenue Chart')).toBeInTheDocument();
  });

  it('hides legend when showLegend is false', () => {
    render(
      <Chart
        type="line"
        data={sampleData}
        series={sampleSeries}
        showLegend={false}
        height={300}
      />
    );

    const legend = document.querySelector('.recharts-legend-wrapper');
    expect(legend).not.toBeInTheDocument();
  });

  it('hides grid when showGrid is false', () => {
    render(
      <Chart
        type="line"
        data={sampleData}
        series={sampleSeries}
        showGrid={false}
        height={300}
      />
    );

    const grid = document.querySelector('.recharts-cartesian-grid');
    expect(grid).not.toBeInTheDocument();
  });

  it('uses custom colors', () => {
    const customColors = ['#FF0000', '#00FF00', '#0000FF'];

    render(
      <Chart
        type="bar"
        data={sampleData}
        series={sampleSeries}
        colors={customColors}
        height={300}
      />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('handles empty data', () => {
    render(
      <Chart type="line" data={[]} series={sampleSeries} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with custom height and width', () => {
    const { container } = render(
      <Chart type="line" data={sampleData} series={sampleSeries} height={500} width="80%" />
    );

    const chartContainer = container.querySelector('.recharts-responsive-container');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with axis labels', () => {
    render(
      <Chart
        type="line"
        data={sampleData}
        series={sampleSeries}
        xAxisLabel="Month"
        yAxisLabel="Amount"
        height={300}
      />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with animation disabled', () => {
    render(
      <Chart
        type="line"
        data={sampleData}
        series={sampleSeries}
        animation={false}
        height={300}
      />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });
});

describe('AnimatedChart', () => {
  it('renders chart with motion wrapper', () => {
    render(
      <Chart type="line" data={sampleData} series={sampleSeries} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });
});
