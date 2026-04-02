import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Dialog } from './Dialog';

describe('Dialog Component', () => {
  it('renders dialog when open', () => {
    render(
      <Dialog open onClose={() => {}} title="Test Dialog">
        <div>Dialog content</div>
      </Dialog>
    );
    expect(screen.getByText('Test Dialog')).toBeInTheDocument();
    expect(screen.getByText('Dialog content')).toBeInTheDocument();
  });

  it('does not render when closed', () => {
    render(
      <Dialog open={false} onClose={() => {}} title="Hidden">
        <div>Content</div>
      </Dialog>
    );
    expect(screen.queryByText('Hidden')).not.toBeInTheDocument();
  });

  it('calls onClose when close button is clicked', async () => {
    const handleClose = () => {};
    render(
      <Dialog open onClose={handleClose} title="Dialog">
        <div>Content</div>
      </Dialog>
    );
    const closeButton = screen.getByRole('button', { name: /close/i });
    await userEvent.click(closeButton);
    expect(handleClose).toHaveBeenCalled();
  });

  it('renders action buttons when provided', () => {
    render(
      <Dialog
        open
        onClose={() => {}}
        title="Dialog with Actions"
        actions={<button>Action Button</button>}
      >
        <div>Content</div>
      </Dialog>
    );
    expect(screen.getByText('Action Button')).toBeInTheDocument();
  });

  it('hides close button when showCloseButton is false', () => {
    render(
      <Dialog open onClose={() => {}} title="No Close" showCloseButton={false}>
        <div>Content</div>
      </Dialog>
    );
    expect(screen.queryByRole('button', { name: /close/i })).not.toBeInTheDocument();
  });
});
