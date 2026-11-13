import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import App from './App'
import { bankingApi } from './services/api'
import { AxiosError } from 'axios'
import toast from 'react-hot-toast'

vi.mock('./services/api', () => ({
  bankingApi: { getAccounts: vi.fn(), getHistory: vi.fn(), transfer: vi.fn(), getTransfer: vi.fn() },
  errorMessage: () => 'No se pudo conectar con el servidor',
}))
vi.mock('react-hot-toast', () => ({
  default: { success: vi.fn(), error: vi.fn() }, Toaster: () => null,
}))
const accounts = [
  { id: 1, accountNumber: 'TEST-A', balance: 100 },
  { id: 2, accountNumber: 'TEST-B', balance: 50 },
]
afterEach(cleanup)
beforeEach(() => {
  vi.mocked(bankingApi.getAccounts).mockReset().mockResolvedValue(accounts)
  vi.mocked(bankingApi.getHistory).mockReset().mockResolvedValue([])
  vi.mocked(bankingApi.transfer).mockReset()
  vi.mocked(bankingApi.getTransfer).mockReset()
})
describe('banking workspace', () => {
  it('loads real API contracts and switches account history', async () => {
    render(<App />)
    const destination = await screen.findByRole('button', { name: 'Ver historial de TEST-B' })
    await waitFor(() => expect(bankingApi.getHistory).toHaveBeenCalledWith(1, expect.any(AbortSignal)))
    fireEvent.click(destination)
    await waitFor(() => expect(bankingApi.getHistory).toHaveBeenCalledWith(2, expect.any(AbortSignal)))
    expect(screen.getByText('Historial de TEST-B')).toBeTruthy()
  })

  it('reloads balances and history after a successful POST', async () => {
    vi.mocked(bankingApi.transfer).mockResolvedValue({
      reference: 'test-reference', sourceAccountNumber: 'TEST-A',
      destinationAccountNumber: 'TEST-B', amount: 25, createdAt: '2026-01-01T12:00:00Z',
      status: 'COMPLETED',
    })
    render(<App />)
    await screen.findByRole('button', { name: 'Ver historial de TEST-A' })
    await waitFor(() => expect(bankingApi.getHistory).toHaveBeenCalledTimes(1))
    fireEvent.change(screen.getByLabelText('Cuenta de origen'), { target: { value: '1' } })
    fireEvent.change(screen.getByLabelText('Cuenta de destino'), { target: { value: '2' } })
    fireEvent.change(screen.getByLabelText('Importe a transferir'), { target: { value: '25' } })
    fireEvent.click(screen.getByRole('button', { name: /Realizar transferencia/ }))
    await waitFor(() => expect(bankingApi.transfer).toHaveBeenCalledTimes(1))
    await waitFor(() => expect(bankingApi.getAccounts).toHaveBeenCalledTimes(2))
    await waitFor(() => expect(bankingApi.getHistory).toHaveBeenCalledTimes(2))
    expect(screen.getByRole('dialog').textContent).toContain('test-reference')
    fireEvent.click(screen.getByRole('button', { name: 'Cerrar comprobante' }))
    expect(screen.queryByRole('dialog')).toBeNull()
    expect(bankingApi.transfer).toHaveBeenCalledTimes(1)
  })

  it('loads a historical receipt by reference without posting another transfer', async () => {
    const historical = {
      reference: '550e8400-e29b-41d4-a716-446655440000',
      sourceAccountNumber: 'TEST-A', destinationAccountNumber: 'TEST-B',
      amount: 25, createdAt: '2026-01-01T12:00:00Z', status: 'COMPLETED' as const,
    }
    vi.mocked(bankingApi.getHistory).mockResolvedValue([historical])
    vi.mocked(bankingApi.getTransfer).mockResolvedValue(historical)
    render(<App />)
    fireEvent.click(await screen.findByRole('button', { name: 'Ver comprobante ' + historical.reference }))
    await screen.findByRole('dialog')
    expect(bankingApi.getTransfer).toHaveBeenCalledWith(historical.reference, expect.any(AbortSignal))
    expect(bankingApi.transfer).not.toHaveBeenCalled()
  })

  it('handles a nonexistent historical receipt without showing a fabricated one', async () => {
    const historical = {
      reference: '550e8400-e29b-41d4-a716-446655440000',
      sourceAccountNumber: 'TEST-A', destinationAccountNumber: 'TEST-B',
      amount: 25, createdAt: '2026-01-01T12:00:00Z', status: 'COMPLETED' as const,
    }
    vi.mocked(bankingApi.getHistory).mockResolvedValue([historical])
    vi.mocked(bankingApi.getTransfer).mockRejectedValue(new AxiosError('not found', undefined,
      undefined, undefined, { status: 404 } as never))
    render(<App />)
    fireEvent.click(await screen.findByRole('button', { name: 'Ver comprobante ' + historical.reference }))
    await waitFor(() => expect(toast.error).toHaveBeenCalledWith('No se encontró el comprobante de esta transferencia.'))
    expect(screen.queryByRole('dialog')).toBeNull()
    expect(bankingApi.transfer).not.toHaveBeenCalled()
  })

  it('shows an actionable account error without inventing sample data', async () => {
    vi.mocked(bankingApi.getAccounts).mockRejectedValue(new Error('network'))
    render(<App />)
    await screen.findByText('No pudimos actualizar tus cuentas')
    expect(screen.queryByRole('button', { name: /Ver historial de/ })).toBeNull()
    expect(screen.getByRole('button', { name: 'Reintentar' })).toBeTruthy()
  })
})
