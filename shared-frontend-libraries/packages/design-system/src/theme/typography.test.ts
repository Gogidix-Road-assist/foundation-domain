import { describe, it, expect } from 'vitest';
import { TYPOGRAPHY_SCALE, getTypographyScale } from './typography';

describe('Typography System', () => {
  it('should have all typography scales', () => {
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h1');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h2');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h3');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h4');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h5');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('h6');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('body1');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('body2');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('button');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('caption');
    expect(TYPOGRAPHY_SCALE).toHaveProperty('data');
  });

  it('should have correct h1 typography', () => {
    expect(TYPOGRAPHY_SCALE.h1.fontSize).toBe('2.5rem');
    expect(TYPOGRAPHY_SCALE.h1.fontWeight).toBe(700);
    expect(TYPOGRAPHY_SCALE.h1.lineHeight).toBe(1.2);
  });

  it('should have correct body1 typography', () => {
    expect(TYPOGRAPHY_SCALE.body1.fontSize).toBe('1rem');
    expect(TYPOGRAPHY_SCALE.body1.fontWeight).toBe(400);
    expect(TYPOGRAPHY_SCALE.body1.lineHeight).toBe(1.5);
  });

  it('should get typography scale for data displays', () => {
    const dataTypography = getTypographyScale('data');
    expect(dataTypography.fontSize).toBe('1rem');
    expect(dataTypography.fontWeight).toBe(600);
    expect(dataTypography.lineHeight).toBe(1.2);
  });
});
