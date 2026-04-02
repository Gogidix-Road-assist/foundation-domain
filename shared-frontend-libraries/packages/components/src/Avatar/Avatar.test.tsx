import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Avatar } from './Avatar';

describe('Avatar Component', () => {
  it('renders avatar with fallback text', () => {
    render(<Avatar fallbackText="JD" />);
    expect(screen.getByText('JD')).toBeInTheDocument();
  });

  it('renders avatar with image source', () => {
    render(<Avatar src="https://example.com/avatar.jpg" alt="User" />);
    const img = screen.getByAltText('User');
    expect(img).toHaveAttribute('src', 'https://example.com/avatar.jpg');
  });

  it('applies size styles', () => {
    render(<Avatar size="small" fallbackText="S" />);
    const avatar = screen.getByText('S');
    expect(avatar).toHaveStyle({ width: '24px', height: '24px' });
  });

  it('shows first letter of alt text when no fallback provided', () => {
    render(<Avatar alt="John Doe" />);
    expect(screen.getByText('J')).toBeInTheDocument();
  });

  it('renders with custom color', () => {
    render(<Avatar color="success" fallbackText="OK" />);
    const avatar = screen.getByText('OK');
    expect(avatar).toHaveStyle({ backgroundColor: '#10B981' });
  });
});
