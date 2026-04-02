import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Drawer } from './Drawer';

describe('Drawer Component', () => {
  it('renders drawer when open', () => {
    render(
      <Drawer open onClose={() => {}} title="Test Drawer">
        <div>Drawer content</div>
      </Drawer>
    );
    expect(screen.getByText('Test Drawer')).toBeInTheDocument();
    expect(screen.getByText('Drawer content')).toBeInTheDocument();
  });

  it('renders navigation items', () => {
    render(
      <Drawer
        open
        onClose={() => {}}
        title="Navigation"
        items={[
          { label: 'Dashboard', onClick: () => {} },
          { label: 'Settings', onClick: () => {} },
        ]}
      />
    );
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Settings')).toBeInTheDocument();
  });

  it('applies active state to items', () => {
    render(
      <Drawer
        open
        onClose={() => {}}
        items={[
          { label: 'Active Item', onClick: () => {}, active: true },
          { label: 'Inactive Item', onClick: () => {}, active: false },
        ]}
      />
    );
    const activeButton = screen.getByText('Active Item').closest('button');
    expect(activeButton).toHaveStyle({ backgroundColor: '#0066CC' });
  });

  it('renders on different anchors', () => {
    const { rerender } = render(
      <Drawer open onClose={() => {}} anchor="left">
        <div>Content</div>
      </Drawer>
    );
    expect(screen.getByText('Content')).toBeInTheDocument();

    rerender(
      <Drawer open onClose={() => {}} anchor="right">
        <div>Content</div>
      </Drawer>
    );
    expect(screen.getByText('Content')).toBeInTheDocument();
  });
});
