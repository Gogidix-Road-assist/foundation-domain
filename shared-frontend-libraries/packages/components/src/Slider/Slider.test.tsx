import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Slider } from './Slider';

describe('Slider Component', () => {
  it('renders slider', () => {
    render(<Slider />);
    const slider = screen.getByRole('slider');
    expect(slider).toBeInTheDocument();
  });

  it('applies min and max values', () => {
    render(<Slider min={0} max={100} />);
    const slider = screen.getByRole('slider');
    expect(slider).toHaveAttribute('aria-valuemin', '0');
    expect(slider).toHaveAttribute('aria-valuemax', '100');
  });

  it('is disabled when disabled prop is true', () => {
    render(<Slider disabled />);
    expect(screen.getByRole('slider')).toBeDisabled();
  });
});
