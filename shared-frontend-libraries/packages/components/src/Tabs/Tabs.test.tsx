import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Tabs } from './Tabs';

describe('Tabs Component', () => {
  it('renders all tabs', () => {
    render(
      <Tabs
        value="tab1"
        onChange={() => {}}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2' },
          { label: 'Tab 3', value: 'tab3' },
        ]}
      />
    );
    expect(screen.getByText('Tab 1')).toBeInTheDocument();
    expect(screen.getByText('Tab 2')).toBeInTheDocument();
    expect(screen.getByText('Tab 3')).toBeInTheDocument();
  });

  it('highlights selected tab', () => {
    render(
      <Tabs
        value="tab2"
        onChange={() => {}}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2' },
        ]}
      />
    );
    const selectedTab = screen.getByRole('tab', { selected: true });
    expect(selectedTab).toHaveTextContent('Tab 2');
  });

  it('calls onChange when tab is clicked', async () => {
    const handleChange = () => {};
    render(
      <Tabs
        value="tab1"
        onChange={handleChange}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2' },
        ]}
      />
    );
    const tab2 = screen.getByText('Tab 2');
    await userEvent.click(tab2);
    expect(handleChange).toHaveBeenCalled();
  });

  it('disables tab when disabled prop is true', () => {
    render(
      <Tabs
        value="tab1"
        onChange={() => {}}
        tabs={[
          { label: 'Tab 1', value: 'tab1' },
          { label: 'Tab 2', value: 'tab2', disabled: true },
        ]}
      />
    );
    const disabledTab = screen.getByRole('tab', { name: 'Tab 2' });
    expect(disabledTab).toBeDisabled();
  });
});
