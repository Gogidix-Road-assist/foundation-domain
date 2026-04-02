import { describe, it, expect } from 'vitest';
import { SPACING_SCALE, getSpacing } from './spacing';

describe('Spacing Scale', () => {
  it('should have all spacing values from 0 to 12', () => {
    expect(SPACING_SCALE).toHaveProperty('0');
    expect(SPACING_SCALE).toHaveProperty('12');
  });

  it('should have base spacing unit of 4px', () => {
    expect(SPACING_SCALE[1]).toBe('4px');
  });

  it('should calculate spacing correctly', () => {
    expect(getSpacing(1)).toBe('4px');
    expect(getSpacing(2)).toBe('8px');
    expect(getSpacing(4)).toBe('16px');
    expect(getSpacing(8)).toBe('32px');
  });
});
