import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Menu } from './Menu';

describe('Menu Component', () => {
  it('renders menu items when open', () => {
    const anchorEl = document.createElement('button');
    document.body.appendChild(anchorEl);

    render(
      <Menu
        anchorEl={anchorEl}
        onClose={() => {}}
        items={[
          { label: 'Option 1', value: 'opt1' },
          { label: 'Option 2', value: 'opt2' },
        ]}
      />
    );

    expect(screen.getByText('Option 1')).toBeInTheDocument();
    expect(screen.getByText('Option 2')).toBeInTheDocument();
  });

  it('does not render when closed', () => {
    render(
      <Menu
        anchorEl={null}
        onClose={() => {}}
        items={[{ label: 'Item', value: 'item' }]}
      />
    );

    expect(screen.queryByText('Item')).not.toBeInTheDocument();
  });

  it('calls onSelect when item is clicked', async () => {
    const anchorEl = document.createElement('button');
    document.body.appendChild(anchorEl);

    const handleSelect = () => {};
    render(
      <Menu
        anchorEl={anchorEl}
        onClose={() => {}}
        onSelect={handleSelect}
        items={[{ label: 'Click me', value: 'click' }]}
      />
    );

    const item = screen.getByText('Click me');
    await userEvent.click(item);
    expect(handleSelect).toHaveBeenCalled();
  });

  it('disables item when disabled prop is true', () => {
    const anchorEl = document.createElement('button');
    document.body.appendChild(anchorEl);

    render(
      <Menu
        anchorEl={anchorEl}
        onClose={() => {}}
        items={[
          { label: 'Enabled', value: 'en' },
          { label: 'Disabled', value: 'dis', disabled: true },
        ]}
      />
    );

    const disabledItem = screen.getByText('Disabled');
    expect(disabledItem).toBeDisabled();
  });

  it('applies danger style to items', () => {
    const anchorEl = document.createElement('button');
    document.body.appendChild(anchorEl);

    render(
      <Menu
        anchorEl={anchorEl}
        onClose={() => {}}
        items={[
          { label: 'Safe', value: 'safe' },
          { label: 'Danger', value: 'danger', danger: true },
        ]}
      />
    );

    const dangerItem = screen.getByText('Danger');
    expect(dangerItem).toHaveStyle({ color: '#EF4444' });
  });
});
