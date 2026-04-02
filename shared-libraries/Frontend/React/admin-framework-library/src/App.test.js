import { describe, it, expect } from 'vitest';
import App from './App.jsx';

describe('App', () => {
  it('is defined', () => {
    expect(App).toBeTypeOf('function');
  });
});
