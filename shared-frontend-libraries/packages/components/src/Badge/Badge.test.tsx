import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Badge } from './Badge';

describe('Badge Component', () => {
  it('renders badge with content', () => {
    render(<Badge count={5}><div>Content</div></Badge>);
    expect(screen.getByText('5')).toBeInTheDocument();
  });

  it('renders badge with dot variant', () => {
    render(<Badge dot><div>Content</div></Badge>);
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  it('hides badge when count is zero and showZero is false', () => {
    render(<Badge count={0} showZero={false}><div>Content</div></Badge>);
    expect(screen.queryByText('0')).not.toBeInTheDocument();
  });
});
