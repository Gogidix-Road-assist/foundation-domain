import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { LineChart } from './LineChart';

const sampleData = [
  { name: 'Jan', value: 400, value2: 240 },
  { name: 'Feb', value: 300, value2: 139 },
  { name: 'Mar', value: 200, value2: 980 },
];

const sampleSeries = [
  { dataKey: 'value', name: 'Revenue' },
  { dataKey: 'value2', name: 'Expenses' },
];

describe('LineChart', () => {
  it('renders line chart', () => {
    render(<LineChart data={sampleData} series={sampleSeries} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with title', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} title="Revenue Chart" height={300} />
    );

    expect(screen.getByText('Revenue Chart')).toBeInTheDocument();
  });

  it('renders with subtitle', () => {
    render(
      <LineChart
        data={sampleData}
        series={sampleSeries}
        title="Revenue"
        subtitle="Monthly revenue data"
        height={300}
      />
    );

    expect(screen.getByText('Revenue')).toBeInTheDocument();
    expect(screen.getByText('Monthly revenue data')).toBeInTheDocument();
  });

  it('hides legend when showLegend is false', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} showLegend={false} height={300} />
    );

    const legend = document.querySelector('.recharts-legend-wrapper');
    expect(legend).not.toBeInTheDocument();
  });

  it('hides grid when showGrid is false', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} showGrid={false} height={300} />
    );

    const grid = document.querySelector('.recharts-cartesian-grid');
    expect(grid).not.toBeInTheDocument();
  });

  it('hides dots when showDots is false', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} showDots={false} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders area chart when showArea is true', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} showArea={true} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with axis labels', () => {
    render(
      <LineChart
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

  it('handles empty data', () => {
    render(<LineChart data={[]} series={sampleSeries} height={300} />);

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('uses custom colors', () => {
    const customColors = ['#FF0000', '#00FF00'];

    render(
      <LineChart data={sampleData} series={sampleSeries} colors={customColors} height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });

  it('renders with custom curve type', () => {
    render(
      <LineChart data={sampleData} series={sampleSeries} curveType="step" height={300} />
    );

    const chartContainer = document.querySelector('.recharts-wrapper');
    expect(chartContainer).toBeInTheDocument();
  });
});
