import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Input } from './Input';

describe('Input Component', () => {
  it('renders input element', () => {
    render(<Input placeholder="Enter text" />);
    expect(screen.getByPlaceholderText('Enter text')).toBeInTheDocument();
  });

  it('applies disabled state', () => {
    render(<Input disabled />);
    expect(screen.getByRole('textbox')).toBeDisabled();
  });

  it('updates value on user input', async () => {
    render(<Input />);
    const input = screen.getByRole('textbox');
    await userEvent.type(input, 'test value');
    expect(input).toHaveValue('test value');
  });

  it('shows error state', () => {
    render(<Input error />);
    expect(screen.getByRole('textbox')).toHaveStyle({ borderColor: '#EF4444' });
  });

  it('calls onChange handler', async () => {
    const handleChange = () => {};
    render(<Input onChange={handleChange} />);
    await userEvent.type(screen.getByRole('textbox'), 'a');
    expect(handleChange).toHaveBeenCalled();
  });
});
