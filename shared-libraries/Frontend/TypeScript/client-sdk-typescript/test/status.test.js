const test = require('node:test');
const assert = require('node:assert/strict');

const { getStatusUseCase } = require('../src/application/usecases/getStatusUseCase');

test('getStatusUseCase returns OK', () => {
  assert.equal(getStatusUseCase.getStatus(), 'OK');
});
