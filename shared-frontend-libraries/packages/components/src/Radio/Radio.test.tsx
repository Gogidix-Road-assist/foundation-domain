import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Radio } from './Radio';

describe('Radio Component', () => {
  it('renders radio button', () => {
    render(<Radio label="Option 1" />);
    expect(screen.getByRole('radio')).toBeInTheDocument();
    expect(screen.getByText('Option 1')).toBeInTheDocument();
  });

  it('can be selected', async () => {
    render(<Radio label="Select me" />);
    const radio = screen.getByRole('radio');
    await userEvent.click(radio);
    expect(radio).toBeChecked();
  });

  it('is disabled when disabled prop is true', () => {
    render(<Radio disabled />);
    expect(screen.getByRole('radio')).toBeDisabled();
  });
});
