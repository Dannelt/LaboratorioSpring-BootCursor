import { AxiosError, AxiosHeaders } from 'axios'
import { describe, expect, it } from 'vitest'
import { errorMessage } from './api'

describe('HTTP errors', () => {
  it.each([400, 404, 409])('maps %s without exposing server internals', status => {
    const error = new AxiosError('request failed', undefined, undefined, undefined, {
      status, statusText: '', headers: {}, config: { headers: new AxiosHeaders() },
      data: { message: 'password=private internal trace' },
    })
    expect(errorMessage(error)).not.toContain('password')
    expect(errorMessage(error).length).toBeGreaterThan(10)
  })
  it('warns against blind retries when a response is uncertain', () => {
    expect(errorMessage(new AxiosError('network'))).toContain('historial')
  })
})
