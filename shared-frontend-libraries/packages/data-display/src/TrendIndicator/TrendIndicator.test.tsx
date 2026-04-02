import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { TrendIndicator } from './TrendIndicator';

describe('TrendIndicator', () => {
  it('renders positive trend', () => {
    render(<TrendIndicator value={15} />);

    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('renders negative trend', () => {
    render(<TrendIndicator value={-10} />);

    expect(screen.getByText('-10')).toBeInTheDocument();
  });

  it('renders flat trend', () => {
    render(<TrendIndicator value={0} />);

    expect(screen.getByText('0')).toBeInTheDocument();
  });

  it('renders with label', () => {
    render(<TrendIndicator value={15} label="vs last month" />);

    expect(screen.getByText('15')).toBeInTheDocument();
    expect(screen.getByText('vs last month')).toBeInTheDocument();
  });

  it('hides icon when showIcon is false', () => {
    render(<TrendIndicator value={15} showIcon={false} />);

    const icon = document.querySelector('.MuiSvgIcon-root');
    expect(icon).not.toBeInTheDocument();
  });

  it('renders with chip variant', () => {
    render(<TrendIndicator value={15} variant="chip" />);

    const chip = document.querySelector('.MuiChip-root');
    expect(chip).toBeInTheDocument();
  });

  it('renders with compact variant', () => {
    render(<TrendIndicator value={15} variant="compact" />);

    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('renders in small size', () => {
    render(<TrendIndicator value={15} size="small" />);

    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('renders in large size', () => {
    render(<TrendIndicator value={15} size="large" />);

    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('calculates percentage from previousValue', () => {
    render(<TrendIndicator value={120} previousValue={100} />);

    expect(screen.getByText('20%')).toBeInTheDocument();
  });

  it('hides percentage when showPercentage is false', () => {
    render(
      <TrendIndicator value={120} previousValue={100} showPercentage={false} />
    );

    expect(screen.queryByText('20%')).not.toBeInTheDocument();
  });

  it('uses custom precision', () => {
    render(<TrendIndicator value={15.345} precision={2} />);

    expect(screen.getByText('15.35')).toBeInTheDocument();
  });

  it('uses custom format function', () => {
    render(<TrendIndicator value={15} format={(v) => `$${v}`} />);

    expect(screen.getByText('$15')).toBeInTheDocument();
  });

  it('uses explicit color', () => {
    render(<TrendIndicator value={15} color="success" />);

    expect(screen.getByText('15')).toBeInTheDocument();
  });

  it('uses explicit direction', () => {
    render(<TrendIndicator value={15} direction="up" />);

    const upIcon = document.querySelector('.TrendingUpIcon');
    expect(upIcon).toBeInTheDocument();
  });

  it('renders with custom className', () => {
    render(<TrendIndicator value={15} className="custom-trend" />);

    const element = document.querySelector('.custom-trend');
    expect(element).toBeInTheDocument();
  });

  it('applies custom sx props', () => {
    render(<TrendIndicator value={15} sx={{ margin: 2 }} />);

    const element = screen.getByText('15').closest('div');
    expect(element).toHaveStyle({ margin: '8px' });
  });
});
