import type { Transfer } from './types'
import { formatDate, formatMoney } from './utils'

export const academicNotice = 'Comprobante académico. No es una firma digital ni una garantía criptográfica de autenticidad.'

export function maskAccount(account: string): string {
  return account.length > 4 ? '****' + account.slice(-4) : '****'
}

/** Deterministic, versioned QR content; only explicitly allowed fields are included. */
export function receiptPayload(transfer: Transfer): string {
  return JSON.stringify({
    application: 'Nexo',
    type: 'academic-transfer-receipt',
    version: 1,
    reference: transfer.reference,
    sourceAccount: maskAccount(transfer.sourceAccountNumber),
    destinationAccount: maskAccount(transfer.destinationAccountNumber),
    amount: String(transfer.amount),
    createdAt: transfer.createdAt,
    status: transfer.status,
  })
}

function escapeXml(value: string): string {
  return value.replace(/[&<>"']/g, character =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&apos;' })[character]!)
}

/** Builds a self-contained picture using the exact QR already rendered in the receipt. */
export function receiptSvg(transfer: Transfer, qrSvg: string): string {
  const rows = [
    ['Referencia UUID', transfer.reference],
    ['Cuenta de origen', maskAccount(transfer.sourceAccountNumber)],
    ['Cuenta de destino', maskAccount(transfer.destinationAccountNumber)],
    ['Fecha y hora', formatDate(transfer.createdAt)],
  ]
  return `<svg xmlns="http://www.w3.org/2000/svg" width="640" height="890" viewBox="0 0 640 890">
  <rect width="640" height="890" rx="24" fill="#f6f7f3"/>
  <rect x="20" y="20" width="600" height="850" rx="18" fill="white" stroke="#dfe6d7"/>
  <g font-family="Arial, sans-serif" fill="#193e32">
  <text x="48" y="72" font-size="32" font-weight="bold">nexo.</text>
  <text x="48" y="108" font-size="18">Comprobante de transferencia</text>
  <text x="48" y="146" font-size="14">Estado: ${escapeXml(transfer.status)}</text>
  <text x="48" y="199" font-size="34" font-weight="bold">${escapeXml(formatMoney(transfer.amount))}</text>
  ${rows.map(([label, value], index) => `<text x="48" y="${242 + index * 52}" font-size="12" fill="#7a877b">${label}</text><text x="48" y="${263 + index * 52}" font-size="15">${escapeXml(value)}</text>`).join('')}
  <svg x="180" y="448" width="280" height="280" viewBox="${escapeXml(qrSvg.match(/viewBox="([^"]+)"/)?.[1] || '0 0 256 256')}">
  ${qrSvg.replace(/^<svg[^>]*>/, '').replace(/<\/svg>$/, '')}
  </svg>
  <text x="320" y="764" text-anchor="middle" font-size="12">Escanea para leer los datos básicos de la operación.</text>
  <text x="48" y="807" font-size="11" fill="#7a877b">Comprobante académico. No es una firma digital</text>
  <text x="48" y="826" font-size="11" fill="#7a877b">ni una garantía criptográfica de autenticidad.</text>
  </g></svg>`
}

export function downloadReceipt(transfer: Transfer, qrSvg: string): void {
  const blob = new Blob([receiptSvg(transfer, qrSvg)], { type: 'image/svg+xml;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = `nexo-comprobante-${transfer.reference}.svg`
  document.body.append(anchor)
  anchor.click()
  anchor.remove()
  // Keep the object URL alive until the browser has started the download.
  window.setTimeout(() => URL.revokeObjectURL(url), 1000)
}
