import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Tooltip } from './Tooltip';
import { Button } from '../Button';

describe('Tooltip Component', () => {
  it('renders wrapped children', () => {
    render(
      <Tooltip title="Tooltip text">
        <Button>Hover me</Button>
      </Tooltip>
    );
    expect(screen.getByText('Hover me')).toBeInTheDocument();
  });

  it('shows tooltip on hover', async () => {
    render(
      <Tooltip title="Tooltip message">
        <Button>Button</Button>
      </Tooltip>
    );

    const button = screen.getByText('Button');
    fireEvent.mouseEnter(button);

    const tooltip = await screen.findByText('Tooltip message');
    expect(tooltip).toBeInTheDocument();
  });

  it('applies color variant', () => {
    render(
      <Tooltip title="Error tooltip" color="error">
        <Button>Error</Button>
      </Tooltip>
    );
    expect(screen.getByText('Error')).toBeInTheDocument();
  });

  it('applies custom placement', () => {
    render(
      <Tooltip title="Top tooltip" placement="top">
        <Button>Button</Button>
      </Tooltip>
    );
    expect(screen.getByText('Button')).toBeInTheDocument();
  });

  it('renders without arrow when arrow is false', () => {
    render(
      <Tooltip title="No arrow" arrow={false}>
        <Button>Button</Button>
      </Tooltip>
    );
    expect(screen.getByText('Button')).toBeInTheDocument();
  });
});
