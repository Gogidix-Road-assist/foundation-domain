import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Checkbox } from './Checkbox';

describe('Checkbox Component', () => {
  it('renders checkbox', () => {
    render(<Checkbox label="Accept terms" />);
    expect(screen.getByRole('checkbox')).toBeInTheDocument();
    expect(screen.getByText('Accept terms')).toBeInTheDocument();
  });

  it('can be checked', async () => {
    render(<Checkbox />);
    const checkbox = screen.getByRole('checkbox');
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('is disabled when disabled prop is true', () => {
    render(<Checkbox disabled />);
    expect(screen.getByRole('checkbox')).toBeDisabled();
  });

  it('shows indeterminate state', () => {
    render(<Checkbox indeterminate />);
    expect(screen.getByRole('checkbox')).toHaveAttribute('data-indeterminate', 'true');
  });
});
