import { describe, it, expect } from 'vitest';
import { CRITICAL_THEME } from './critical';

describe('Critical Theme', () => {
  it('should have critical background', () => {
    expect(CRITICAL_THEME.palette.background.default).toBe('#FEF2F2');
  });

  it('should have critical action color', () => {
    expect(CRITICAL_THEME.criticalAction).toBe('#DC2626');
  });
});
