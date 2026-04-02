import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Container } from './Container';

describe('Container Component', () => {
  it('renders children content', () => {
    render(<Container>Content</Container>);
    expect(screen.getByText('Content')).toBeInTheDocument();
  });

  it('applies size styles', () => {
    render(<Container size="small">Small</Container>);
    const container = screen.getByText('Small').closest('.MuiBox-root');
    expect(container).toHaveStyle({ maxWidth: '640px' });
  });

  it('applies centered alignment', () => {
    render(<Container centered>Centered</Container>);
    const container = screen.getByText('Centered').closest('.MuiBox-root');
    expect(container).toHaveStyle({ margin: '0 auto' });
  });

  it('applies no padding when noPadding is true', () => {
    render(<Container noPadding>No Padding</Container>);
    const container = screen.getByText('No Padding').closest('.MuiBox-root');
    expect(container).toHaveStyle({ padding: '0px' });
  });
});
