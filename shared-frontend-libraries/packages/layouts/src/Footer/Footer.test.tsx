import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Footer } from './Footer';

describe('Footer Component', () => {
  it('renders copyright text', () => {
    render(<Footer />);
    const currentYear = new Date().getFullYear();
    expect(screen.getByText(`© ${currentYear} RapidAssist. All rights reserved.`)).toBeInTheDocument();
  });

  it('renders navigation links', () => {
    const links = [
      { label: 'About', href: '/about' },
      { label: 'Contact', href: '/contact' },
    ];
    render(<Footer links={links} />);
    expect(screen.getByText('About')).toBeInTheDocument();
    expect(screen.getByText('Contact')).toBeInTheDocument();
  });

  it('renders social links', () => {
    render(<Footer />);
    const socialButtons = screen.getAllByRole('link');
    expect(socialButtons.length).toBeGreaterThan(0);
  });
});
