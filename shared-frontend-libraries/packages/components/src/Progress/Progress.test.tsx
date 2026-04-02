import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Progress, CircularProgress, ProgressSteps } from './Progress';

describe('Progress Component', () => {
  it('renders progress bar with value', () => {
    render(<Progress value={50} />);
    const progress = screen.getByRole('progressbar');
    expect(progress).toBeInTheDocument();
    expect(progress).toHaveAttribute('aria-valuenow', '50');
  });

  it('applies color variant', () => {
    render(<Progress value={75} color="success" />);
    const progress = screen.getByRole('progressbar');
    expect(progress).toBeInTheDocument();
  });

  it('shows label when provided', () => {
    render(<Progress value={30} label="Uploading..." />);
    expect(screen.getByText('Uploading...')).toBeInTheDocument();
  });

  it('shows percentage when showValue is true', () => {
    render(<Progress value={60} showValue />);
    expect(screen.getByText('60%')).toBeInTheDocument();
  });
});

describe('CircularProgress Component', () => {
  it('renders circular progress', () => {
    render(<CircularProgress />);
    const progress = screen.getByRole('progressbar');
    expect(progress).toBeInTheDocument();
  });

  it('shows value when determinate', () => {
    render(<CircularProgress value={75} showValue />);
    expect(screen.getByText('75%')).toBeInTheDocument();
  });
});

describe('ProgressSteps Component', () => {
  it('renders step indicators', () => {
    render(
      <ProgressSteps
        steps={[
          { label: 'Step 1', completed: true },
          { label: 'Step 2', active: true },
          { label: 'Step 3' },
        ]}
        currentStep={1}
      />
    );
    expect(screen.getByText('Step 1')).toBeInTheDocument();
    expect(screen.getByText('Step 2')).toBeInTheDocument();
    expect(screen.getByText('Step 3')).toBeInTheDocument();
  });

  it('shows completed state with checkmark', () => {
    render(
      <ProgressSteps
        steps={[
          { label: 'Done', completed: true },
          { label: 'Pending' },
        ]}
        currentStep={1}
      />
    );
    expect(screen.getByText('✓')).toBeInTheDocument();
  });

  it('shows error state', () => {
    render(
      <ProgressSteps
        steps={[
          { label: 'Error', error: true },
          { label: 'Next' },
        ]}
        currentStep={0}
      />
    );
    expect(screen.getByText('!')).toBeInTheDocument();
  });
});
