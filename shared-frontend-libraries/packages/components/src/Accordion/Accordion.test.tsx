import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Accordion } from './Accordion';

describe('Accordion Component', () => {
  it('renders accordion with title', () => {
    render(
      <Accordion title="Accordion Title">
        <div>Accordion content</div>
      </Accordion>
    );
    expect(screen.getByText('Accordion Title')).toBeInTheDocument();
  });

  it('expands on click', async () => {
    render(
      <Accordion title="Click me">
        <div>Hidden content</div>
      </Accordion>
    );
    const summary = screen.getByText('Click me');
    await userEvent.click(summary);
    expect(screen.getByText('Hidden content')).toBeInTheDocument();
  });

  it('collapses on second click', async () => {
    render(
      <Accordion title="Toggle me" defaultExpanded>
        <div>Visible content</div>
      </Accordion>
    );
    const summary = screen.getByText('Toggle me');
    await userEvent.click(summary);
    expect(screen.getByText('Visible content')).not.toBeVisible();
  });

  it('is disabled when disabled prop is true', () => {
    render(
      <Accordion title="Disabled" disabled>
        <div>Content</div>
      </Accordion>
    );
    const accordion = screen.getByRole('region');
    expect(accordion).toHaveAttribute('aria-disabled', 'true');
  });

  it('shows actions when provided', () => {
    render(
      <Accordion title="With Actions" defaultExpanded actions={<button>Save</button>}>
        <div>Content</div>
      </Accordion>
    );
    expect(screen.getByText('Save')).toBeInTheDocument();
  });
});
