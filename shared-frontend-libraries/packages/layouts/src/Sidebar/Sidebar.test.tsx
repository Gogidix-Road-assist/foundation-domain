import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Dashboard, Settings } from '@mui/icons-material';
import { Sidebar } from './Sidebar';

describe('Sidebar Component', () => {
  const mockItems = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: <Dashboard />,
      path: '/dashboard',
    },
    {
      id: 'settings',
      label: 'Settings',
      icon: <Settings />,
      path: '/settings',
    },
  ];

  it('renders navigation items', () => {
    render(<Sidebar items={mockItems} />);
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Settings')).toBeInTheDocument();
  });

  it('calls onItemClick when item is clicked', async () => {
    const handleItemClick = () => {};
    render(<Sidebar items={mockItems} onItemClick={handleItemClick} />);
    await userEvent.click(screen.getByText('Dashboard'));
    expect(handleItemClick).toHaveBeenCalled();
  });

  it('shows collapsed state', () => {
    render(<Sidebar items={mockItems} collapsed />);
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument();
  });

  it('expands items with children', async () => {
    const itemWithChildren = {
      id: 'parent',
      label: 'Parent Item',
      icon: <Dashboard />,
      children: [
        { id: 'child1', label: 'Child 1', icon: <Settings /> },
      ],
    };
    render(<Sidebar items={[itemWithChildren]} />);
    await userEvent.click(screen.getByText('Parent Item'));
    expect(screen.getByText('Child 1')).toBeInTheDocument();
  });
});
