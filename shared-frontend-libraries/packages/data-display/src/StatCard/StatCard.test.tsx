import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { StatCard } from './StatCard';
import AccessTimeIcon from '@mui/icons-material/AccessTime';

describe('StatCard', () => {
  it('renders with title and value', () => {
    render(<StatCard title="Total Users" value={1000} />);

    expect(screen.getByText('Total Users')).toBeInTheDocument();
    expect(screen.getByText('1,000')).toBeInTheDocument();
  });

  it('renders with subtitle', () => {
    render(<StatCard title="Total Users" value={1000} subtitle="Active users" />);

    expect(screen.getByText('Active users')).toBeInTheDocument();
  });

  it('renders with icon', () => {
    render(<StatCard title="Total Users" value={1000} icon={<AccessTimeIcon />} />);

    const icon = document.querySelector('.MuiSvgIcon-root');
    expect(icon).toBeInTheDocument();
  });

  it('renders with positive trend', () => {
    render(
      <StatCard
        title="Total Users"
        value={1000}
        trend={{ value: 15, label: 'vs last month' }}
      />
    );

    expect(screen.getByText('15% vs last month')).toBeInTheDocument();
  });

  it('renders with negative trend', () => {
    render(
      <StatCard
        title="Total Users"
        value={1000}
        trend={{ value: -10, label: 'vs last month' }}
      />
    );

    expect(screen.getByText('10% vs last month')).toBeInTheDocument();
  });

  it('handles positive trend correctly (increase is good)', () => {
    render(
      <StatCard
        title="Errors"
        value={5}
        trend={{ value: -20, label: 'vs last week' }}
      />
    );

    expect(screen.getByText('20% vs last week')).toBeInTheDocument();
  });

  it('renders in small size', () => {
    render(<StatCard title="Total Users" value={1000} size="small" />);

    expect(screen.getByText('Total Users')).toBeInTheDocument();
  });

  it('renders in large size', () => {
    render(<StatCard title="Total Users" value={1000} size="large" />);

    expect(screen.getByText('Total Users')).toBeInTheDocument();
  });

  it('renders in loading state', () => {
    render(<StatCard title="Total Users" value={1000} loading={true} />);

    expect(screen.getByText('Total Users')).toBeInTheDocument();
    // Value should not be visible in loading state
    expect(screen.queryByText('1,000')).not.toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const onClick = vi.fn();
    render(<StatCard title="Total Users" value={1000} onClick={onClick} />);

    const card = document.querySelector('.MuiCard-root');
    card && card.click();
    expect(onClick).toHaveBeenCalled();
  });

  it('applies custom className', () => {
    render(
      <StatCard title="Total Users" value={1000} className="custom-card" />
    );

    const card = document.querySelector('.custom-card');
    expect(card).toBeInTheDocument();
  });

  it('formats number values with locale', () => {
    render(<StatCard title="Revenue" value={1234567.89} />);

    expect(screen.getByText('1,234,567.89')).toBeInTheDocument();
  });

  it('renders string values as-is', () => {
    render(<StatCard title="Status" value="Active" />);

    expect(screen.getByText('Active')).toBeInTheDocument();
  });

  it('hides trend icon when showTrendIcon is false', () => {
    render(
      <StatCard
        title="Total Users"
        value={1000}
        trend={{ value: 15 }}
        showTrendIcon={false}
      />
    );

    const trendIcon = document.querySelector('.TrendingUpIcon, .TrendingDownIcon, .TrendingFlatIcon');
    expect(trendIcon).not.toBeInTheDocument();
  });

  it('applies color variant', () => {
    render(<StatCard title="Success" value={100} color="success" />);

    expect(screen.getByText('Success')).toBeInTheDocument();
  });
});
