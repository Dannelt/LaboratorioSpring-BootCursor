import type { Account } from '../types'
import { formatMoney } from '../utils'

interface Props { account: Account; selected: boolean; onSelect: () => void }
export function AccountCard({ account, selected, onSelect }: Props) {
  return <button className={`account-card ${selected ? 'selected' : ''}`} onClick={onSelect}
    aria-pressed={selected} aria-label={`Ver historial de ${account.accountNumber}`}>
    <span className="card-top"><span className="card-symbol">↗</span>
      <span>{selected ? 'Cuenta seleccionada' : 'Cuenta bancaria'}</span><span className="card-dot" /></span>
    <span className="balance-label">Saldo disponible</span>
    <strong className="balance">{formatMoney(account.balance)}</strong>
    <span className="card-bottom"><span>{account.accountNumber}</span><span>Ver movimientos ↗</span></span>
  </button>
}
