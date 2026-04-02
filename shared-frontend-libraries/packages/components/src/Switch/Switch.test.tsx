import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Switch } from './Switch';

describe('Switch Component', () => {
  it('renders switch', () => {
    render(<Switch label="Enable feature" />);
    expect(screen.getByRole('checkbox')).toBeInTheDocument();
    expect(screen.getByText('Enable feature')).toBeInTheDocument();
  });

  it('toggles on click', async () => {
    render(<Switch />);
    const switchEl = screen.getByRole('checkbox');
    await userEvent.click(switchEl);
    expect(switchEl).toBeChecked();
  });

  it('is disabled when disabled prop is true', () => {
    render(<Switch disabled />);
    expect(screen.getByRole('checkbox')).toBeDisabled();
  });
});
