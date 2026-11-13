import axios from 'axios'
import type { Account, ApiError, Transfer, TransferRequest } from '../types'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
  headers: { Accept: 'application/json' },
})
export const bankingApi = {
  getTransfer: async (reference: string, signal?: AbortSignal) =>
    (await api.get<Transfer>(`/transfers/${encodeURIComponent(reference)}`, { signal })).data,
  getAccounts: async () => (await api.get<Account[]>('/accounts')).data,
  getHistory: async (accountId: number, signal?: AbortSignal) =>
    (await api.get<Transfer[]>(`/accounts/${accountId}/transfers`, { signal })).data,
  transfer: async (request: TransferRequest) =>
    (await api.post<Transfer>('/transfers', request)).data,
}
export function errorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiError>(error)) {
    switch (error.response?.status) {
      case 400: return 'Revisa el importe y las cuentas seleccionadas.'
      case 404: return 'La cuenta o transferencia ya no está disponible. Actualiza las cuentas.'
      case 409: return 'No se pudo completar: saldo insuficiente o conflicto de actualización. Revisa los saldos antes de reintentar.'
      default:
        return error.response
          ? 'El servidor no pudo completar la operación. Inténtalo más tarde.'
          : 'No se pudo confirmar la operación. Revisa la conexión y el historial antes de volver a enviar.'
    }
  }
  return 'Ocurrió un error inesperado. Inténtalo de nuevo.'
}
