import { describe, it, expect } from 'vitest';
import { BREAKPOINTS, getBreakpointValue } from './breakpoints';

describe('Breakpoints', () => {
  it('should have all required breakpoints', () => {
    expect(BREAKPOINTS).toHaveProperty('xs');
    expect(BREAKPOINTS).toHaveProperty('sm');
    expect(BREAKPOINTS).toHaveProperty('md');
    expect(BREAKPOINTS).toHaveProperty('lg');
    expect(BREAKPOINTS).toHaveProperty('xl');
    expect(BREAKPOINTS).toHaveProperty('xxl');
  });

  it('should have correct breakpoint values', () => {
    expect(BREAKPOINTS.xs).toBe(0);
    expect(BREAKPOINTS.sm).toBe(600);
    expect(BREAKPOINTS.md).toBe(900);
    expect(BREAKPOINTS.lg).toBe(1200);
    expect(BREAKPOINTS.xl).toBe(1536);
    expect(BREAKPOINTS.xxl).toBe(1920);
  });

  it('should get breakpoint value correctly', () => {
    expect(getBreakpointValue('md')).toBe(900);
    expect(getBreakpointValue('lg')).toBe(1200);
  });
});
