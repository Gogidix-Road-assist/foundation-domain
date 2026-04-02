import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Layout } from './Layout';

describe('Layout Component', () => {
  it('renders children content', () => {
    render(<Layout>Layout content</Layout>);
    expect(screen.getByText('Layout content')).toBeInTheDocument();
  });

  it('applies flex column layout', () => {
    render(<Layout>Content</Layout>);
    const layout = screen.getByText('Content').closest('.MuiBox-root');
    expect(layout).toHaveStyle({ display: 'flex', flexDirection: 'column' });
  });
});
