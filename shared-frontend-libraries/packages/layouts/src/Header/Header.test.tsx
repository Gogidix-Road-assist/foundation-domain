import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Header } from './Header';

describe('Header Component', () => {
  it('renders title', () => {
    render(<Header title="App Title" />);
    expect(screen.getByText('App Title')).toBeInTheDocument();
  });

  it('renders menu button', () => {
    render(<Header title="Test" onMenuClick={() => {}} />);
    const menuButton = screen.getByRole('button');
    expect(menuButton).toBeInTheDocument();
  });

  it('calls onMenuClick when menu button is clicked', async () => {
    const handleMenuClick = () => {};
    render(<Header title="Test" onMenuClick={handleMenuClick} />);
    const menuButton = screen.getByRole('button');
    await userEvent.click(menuButton);
    expect(handleMenuClick).toHaveBeenCalled();
  });

  it('renders actions when provided', () => {
    render(<Header title="Test" actions={<button>Action</button>} />);
    expect(screen.getByText('Action')).toBeInTheDocument();
  });
});
