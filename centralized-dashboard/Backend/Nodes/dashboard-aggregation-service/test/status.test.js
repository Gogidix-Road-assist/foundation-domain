const test = require('node:test')
const assert = require('node:assert/strict')

const { getStatusUseCase } = require('../src/application/usecases/getStatusUseCase')

test.describe('Status Use Case', () => {
  test('getStatusUseCase returns OK', () => {
    assert.equal(getStatusUseCase.getStatus(), 'OK')
  })

  test('getStatusUseCase has getStatus method', () => {
    assert.strictEqual(typeof getStatusUseCase.getStatus, 'function')
  })

  test('getStatusUseCase returns string', () => {
    const status = getStatusUseCase.getStatus()
    assert.strictEqual(typeof status, 'string')
  })

  test('getStatusUseCase returns consistent status', () => {
    const status1 = getStatusUseCase.getStatus()
    const status2 = getStatusUseCase.getStatus()
    assert.strictEqual(status1, status2)
  })
})
