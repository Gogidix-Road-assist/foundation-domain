import { describe, it, expect } from 'vitest';
import { LIGHT_THEME } from './light';

describe('Light Theme', () => {
  it('should have light theme background colors', () => {
    expect(LIGHT_THEME.palette.background.default).toBe('#FFFFFF');
    expect(LIGHT_THEME.palette.background.paper).toBe('#F9FAFB');
  });

  it('should have light theme text colors', () => {
    expect(LIGHT_THEME.palette.text.primary).toBe('#1F2937');
    expect(LIGHT_THEME.palette.text.secondary).toBe('#374151');
  });
});
