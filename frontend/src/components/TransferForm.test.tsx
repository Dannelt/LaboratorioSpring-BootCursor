import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { TransferForm } from './TransferForm'

const accounts = [
  { id: 1, accountNumber: 'TEST-A', balance: 100 },
  { id: 2, accountNumber: 'TEST-B', balance: 50 },
]
afterEach(cleanup)
describe('transfer form', () => {
  it('validates before sending requests', () => {
    const onTransfer = vi.fn()
    render(<TransferForm accounts={accounts} loading={false} onTransfer={onTransfer} />)
    fireEvent.click(screen.getByRole('button', { name: /Realizar transferencia/ }))
    expect(screen.getByRole('alert').textContent).toContain('Selecciona')
    expect(onTransfer).not.toHaveBeenCalled()
  })
  it('blocks duplicate submits and clears a successful amount', async () => {
    let complete!: (result: boolean) => void
    const onTransfer = vi.fn(() => new Promise<boolean>(resolve => { complete = resolve }))
    render(<TransferForm accounts={accounts} loading={false} onTransfer={onTransfer} />)
    fireEvent.change(screen.getByLabelText('Cuenta de origen'), { target: { value: '1' } })
    fireEvent.change(screen.getByLabelText('Cuenta de destino'), { target: { value: '2' } })
    fireEvent.change(screen.getByLabelText('Importe a transferir'), { target: { value: '25.10' } })
    const button = screen.getByRole('button', { name: /Realizar transferencia/ })
    fireEvent.click(button)
    fireEvent.click(button)
    expect(onTransfer).toHaveBeenCalledTimes(1)
    expect(onTransfer).toHaveBeenCalledWith({ sourceAccountId: 1, destinationAccountId: 2, amount: 25.1 })
    complete(true)
    await waitFor(() => expect((screen.getByLabelText('Importe a transferir') as HTMLInputElement).value).toBe(''))
  })
  it('retains the amount after a rejected transfer', async () => {
    render(<TransferForm accounts={accounts} loading={false} onTransfer={vi.fn().mockResolvedValue(false)} />)
    fireEvent.change(screen.getByLabelText('Cuenta de origen'), { target: { value: '1' } })
    fireEvent.change(screen.getByLabelText('Cuenta de destino'), { target: { value: '2' } })
    fireEvent.change(screen.getByLabelText('Importe a transferir'), { target: { value: '10' } })
    fireEvent.click(screen.getByRole('button', { name: /Realizar transferencia/ }))
    await waitFor(() => expect(screen.getByRole('button', { name: /Realizar transferencia/ }).hasAttribute('disabled')).toBe(false))
    expect((screen.getByLabelText('Importe a transferir') as HTMLInputElement).value).toBe('10')
  })
})
