import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Select } from './Select';

describe('Select Component', () => {
  it('renders select with options', () => {
    render(
      <Select label="Choose option">
        <option value="option1">Option 1</option>
        <option value="option2">Option 2</option>
      </Select>
    );
    expect(screen.getByRole('combobox')).toBeInTheDocument();
    expect(screen.getByText('Choose option')).toBeInTheDocument();
  });

  it('applies disabled state', () => {
    render(
      <Select disabled label="Disabled">
        <option value="1">Option</option>
      </Select>
    );
    expect(screen.getByRole('combobox')).toBeDisabled();
  });

  it('shows error state', () => {
    render(
      <Select error label="Error">
        <option value="1">Option</option>
      </Select>
    );
    expect(screen.getByRole('combobox')).toHaveStyle({ borderColor: '#EF4444' });
  });
});
