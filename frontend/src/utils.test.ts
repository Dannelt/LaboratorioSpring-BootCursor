import { describe, expect, it } from 'vitest'
import { validateTransfer, formatMoney } from './utils'

describe('transfer validation', () => {
  it('accepts positive amounts with two decimals', () => {
    expect(validateTransfer('1', '2', '25.10')).toBeNull()
  })
  it.each(['0', '-1', '1.001', '', 'abc', '1e3'])('rejects invalid amount %s', amount => {
    expect(validateTransfer('1', '2', amount)).not.toBeNull()
  })
  it('rejects identical accounts and missing selections', () => {
    expect(validateTransfer('1', '1', '10')).not.toBeNull()
    expect(validateTransfer('', '2', '10')).not.toBeNull()
  })
  it('does not silently round an unsafe monetary value', () => {
    expect(validateTransfer('1', '2', '99999999999999999.99')).not.toBeNull()
  })
  it('formats amounts with two decimals', () => {
    expect(formatMoney(1250)).toContain('250,00')
  })
})
