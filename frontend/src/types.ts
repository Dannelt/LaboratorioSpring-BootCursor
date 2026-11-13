export interface Account {
  id: number
  accountNumber: string
  balance: number
}
export type TransferStatus = 'PENDING' | 'COMPLETED' | 'FAILED'
export interface TransferRequest {
  sourceAccountId: number
  destinationAccountId: number
  amount: number
}
export interface Transfer {
  reference: string
  sourceAccountNumber: string
  destinationAccountNumber: string
  amount: number
  createdAt: string
  status: TransferStatus
}
export interface ApiError {
  status: number
  message: string
  path: string
  timestamp: string
}
