import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Alert } from './Alert';

describe('Alert Component', () => {
  it('renders alert with content', () => {
    render(<Alert>This is an alert</Alert>);
    expect(screen.getByText('This is an alert')).toBeInTheDocument();
  });

  it('applies severity color', () => {
    render(<Alert severity="error">Error message</Alert>);
    const alert = screen.getByText('Error message').closest('.MuiAlert-root');
    expect(alert).toHaveStyle({ color: '#EF4444' });
  });

  it('renders with title', () => {
    render(<Alert title="Important">Alert content</Alert>);
    expect(screen.getByText('Important')).toBeInTheDocument();
    expect(screen.getByText('Alert content')).toBeInTheDocument();
  });

  it('is closable when closable prop is true', () => {
    render(<Alert closable>Closable alert</Alert>);
    const closeButton = screen.getByRole('button', { name: /close/i });
    expect(closeButton).toBeInTheDocument();
  });

  it('dismisses on close button click', async () => {
    render(<Alert closable>Dismissible alert</Alert>);
    const closeButton = screen.getByRole('button', { name: /close/i });
    await userEvent.click(closeButton);
    expect(screen.queryByText('Dismissible alert')).not.toBeInTheDocument();
  });

  it('applies outlined variant', () => {
    render(<Alert variant="outlined" severity="success">Outlined</Alert>);
    const alert = screen.getByText('Outlined').closest('.MuiAlert-root');
    expect(alert).toHaveStyle({ border: '1px solid #10B981' });
  });
});
