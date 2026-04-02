import { describe, it, expect } from 'vitest';
import { DARK_THEME } from './dark';

describe('Dark Theme', () => {
  it('should have dark theme background colors', () => {
    expect(DARK_THEME.palette.background.default).toBe('#111827');
    expect(DARK_THEME.palette.background.paper).toBe('#1F2937');
  });

  it('should have dark theme text colors', () => {
    expect(DARK_THEME.palette.text.primary).toBe('#F9FAFB');
    expect(DARK_THEME.palette.text.secondary).toBe('#E5E7EB');
  });
});
