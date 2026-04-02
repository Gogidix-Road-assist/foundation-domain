import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Chip } from './Chip';

describe('Chip Component', () => {
  it('renders chip with text', () => {
    render(<Chip>Tag</Chip>);
    expect(screen.getByText('Tag')).toBeInTheDocument();
  });

  it('applies color variant', () => {
    render(<Chip color="primary">Primary</Chip>);
    const chip = screen.getByText('Primary');
    expect(chip).toHaveStyle({ backgroundColor: '#0066CC' });
  });

  it('applies outlined variant', () => {
    render(<Chip variant="outlined" color="primary">Outlined</Chip>);
    const chip = screen.getByText('Outlined');
    expect(chip).toHaveStyle({ border: '1px solid #0066CC' });
  });

  it('is clickable when onClick is provided', async () => {
    const handleClick = () => {};
    render(<Chip onClick={handleClick}>Clickable</Chip>);
    const chip = screen.getByText('Clickable');
    await userEvent.click(chip);
    expect(handleClick).toHaveBeenCalled();
  });

  it('shows delete icon when onDelete is provided', () => {
    render(<Chip onDelete={() => {}}>Deletable</Chip>);
    const deleteIcon = screen.getByRole('button');
    expect(deleteIcon).toBeInTheDocument();
  });
});
