import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ContentArea } from './ContentArea';

describe('ContentArea Component', () => {
  it('renders children content', () => {
    render(<ContentArea>Main content</ContentArea>);
    expect(screen.getByText('Main content')).toBeInTheDocument();
  });

  it('uses main element', () => {
    render(<ContentArea>Content</ContentArea>);
    const main = screen.getByRole('main');
    expect(main).toBeInTheDocument();
  });

  it('applies overflow style', () => {
    render(<ContentArea overflow="hidden">Content</ContentArea>);
    const content = screen.getByRole('main');
    expect(content).toHaveStyle({ overflow: 'hidden' });
  });
});
