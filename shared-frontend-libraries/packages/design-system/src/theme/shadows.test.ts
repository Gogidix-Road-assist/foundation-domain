import { describe, it, expect } from 'vitest';
import { SHADOWS, getShadow } from './shadows';

describe('Shadow System', () => {
  it('should have all shadow levels', () => {
    expect(SHADOWS).toHaveProperty('none');
    expect(SHADOWS).toHaveProperty('xs');
    expect(SHADOWS).toHaveProperty('sm');
    expect(SHADOWS).toHaveProperty('md');
    expect(SHADOWS).toHaveProperty('lg');
    expect(SHADOWS).toHaveProperty('xl');
  });

  it('should have correct shadow values', () => {
    expect(SHADOWS.xs).toBe('0 1px 2px rgba(0,0,0,0.05)');
    expect(SHADOWS.md).toBe('0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06)');
  });
});
