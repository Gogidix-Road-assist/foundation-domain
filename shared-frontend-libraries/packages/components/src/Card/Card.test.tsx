import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Card } from './Card';

describe('Card Component', () => {
  it('renders children content', () => {
    render(<Card>Card content</Card>);
    expect(screen.getByText('Card content')).toBeInTheDocument();
  });

  it('applies default elevation styles', () => {
    render(<Card>Default Card</Card>);
    const card = screen.getByText('Default Card').closest('div');
    expect(card).toHaveStyle({ boxShadow: '0 1px 3px rgba(0, 0, 0, 0.12)' });
  });

  it('applies custom elevation', () => {
    render(<Card elevation={3}>Elevated Card</Card>);
    const card = screen.getByText('Elevated Card').closest('div');
    expect(card).toHaveStyle({ boxShadow: '0 10px 15px rgba(0, 0, 0, 0.1)' });
  });

  it('applies hoverable prop for animation', () => {
    const { container } = render(<Card hoverable>Hoverable</Card>);
    const cardWrapper = container.firstChild;
    expect(cardWrapper).toHaveClass('motion-div');
  });

  it('applies custom className', () => {
    render(<Card className="custom-class">Card</Card>);
    const card = screen.getByText('Card').closest('div');
    expect(card).toHaveClass('custom-class');
  });
});
