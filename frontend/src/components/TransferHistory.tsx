import type { Account, Transfer } from '../types'
import { formatDate, formatMoney } from '../utils'

interface Props {
  account?: Account; transfers: Transfer[]; loading: boolean; error: string | null; onRetry: () => void
  onReceipt: (reference: string) => void; receiptLoading: boolean
}
const statusLabels = { COMPLETED: 'Completada', PENDING: 'Pendiente', FAILED: 'Fallida' }
export function TransferHistory({ account, transfers, loading, error, onRetry, onReceipt, receiptLoading }: Props) {
  return <section className="panel history-panel" aria-labelledby="history-title">
    <div className="history-heading"><div><h2 id="history-title">Últimos movimientos</h2>
      <p>{account ? `Historial de ${account.accountNumber}` : 'Selecciona una cuenta para consultar su historial.'}</p></div>
      <span className="count-badge">{transfers.length} movimientos</span></div>
    {receiptLoading && <p className="receipt-loading" role="status">Consultando comprobante…</p>}
    {loading ? <div className="empty-state" role="status"><span className="spinner" /> Cargando movimientos…</div>
      : error ? <div className="empty-state"><p role="alert">{error}</p><button className="text-button" onClick={onRetry}>Reintentar</button></div>
      : transfers.length === 0 ? <div className="empty-state"><span className="empty-icon">⇄</span>
        <h3>Aún no hay movimientos</h3><p>Las transferencias de esta cuenta aparecerán aquí.</p></div>
      : <div className="table-scroll"><table><thead><tr><th>Movimiento</th><th>Origen → Destino</th><th>Fecha</th><th>Importe</th><th>Estado</th></tr></thead>
        <tbody>{transfers.map(transfer => {
          const outgoing = transfer.sourceAccountNumber === account?.accountNumber
          return <tr key={transfer.reference}><td><div className="movement"><span className={`movement-icon ${outgoing ? 'outgoing' : ''}`}>{outgoing ? '↗' : '↙'}</span>
            <div><strong>{outgoing ? 'Transferencia enviada' : 'Transferencia recibida'}</strong>
              <button className="receipt-link" disabled={receiptLoading}
                onClick={() => onReceipt(transfer.reference)}
                aria-label={`Ver comprobante ${transfer.reference}`}>
                Ver comprobante · {transfer.reference.slice(0, 8)}
              </button></div></div></td>
            <td><span className="account-route">{transfer.sourceAccountNumber}<br /><span>→ {transfer.destinationAccountNumber}</span></span></td>
            <td className="date-cell">{formatDate(transfer.createdAt)}</td>
            <td className={`transfer-amount ${outgoing ? '' : 'incoming'}`}>{outgoing ? '−' : '+'}{formatMoney(transfer.amount)}</td>
            <td><span className={`status-badge ${transfer.status.toLowerCase()}`}>{statusLabels[transfer.status]}</span></td></tr>
        })}</tbody></table></div>}
  </section>
}
