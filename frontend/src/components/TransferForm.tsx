import { useRef, useState } from 'react'
import type { FormEvent } from 'react'
import type { Account, TransferRequest } from '../types'
import { formatMoney, validateTransfer } from '../utils'

interface Props {
  accounts: Account[]
  loading: boolean
  onTransfer: (request: TransferRequest) => Promise<boolean>
}
export function TransferForm({ accounts, loading, onTransfer }: Props) {
  const [source, setSource] = useState('')
  const [destination, setDestination] = useState('')
  const [amount, setAmount] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const sending = useRef(false)
  const sourceAccount = accounts.find(account => String(account.id) === source)
  const disabled = loading || submitting || accounts.length < 2
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (sending.current || disabled) return
    const validation = validateTransfer(source, destination, amount)
    if (validation) { setError(validation); return }
    sending.current = true
    setSubmitting(true)
    setError(null)
    try {
      if (await onTransfer({ sourceAccountId: Number(source), destinationAccountId: Number(destination),
        amount: Number(amount) })) setAmount('')
    } finally { sending.current = false; setSubmitting(false) }
  }
  return <section className="panel transfer-panel" aria-labelledby="transfer-title">
    <div className="section-heading"><span className="section-icon">↗</span><div>
      <h2 id="transfer-title">Nueva transferencia</h2><p>Mueve tu dinero entre cuentas.</p>
    </div></div>
    <form onSubmit={submit} noValidate>
      <label htmlFor="source">Cuenta de origen</label>
      <select id="source" value={source} disabled={disabled} onChange={event => {
        setSource(event.target.value)
        if (event.target.value === destination) setDestination('')
      }}>
        <option value="">Selecciona una cuenta</option>
        {accounts.map(account => <option key={account.id} value={account.id}>{account.accountNumber}</option>)}
      </select>
      <p className="field-note">{sourceAccount ? `Disponible: ${formatMoney(sourceAccount.balance)}` : 'Elige la cuenta desde la que enviarás.'}</p>
      <label htmlFor="destination">Cuenta de destino</label>
      <select id="destination" value={destination} disabled={disabled} onChange={event => setDestination(event.target.value)}>
        <option value="">Selecciona una cuenta</option>
        {accounts.filter(account => String(account.id) !== source).map(account =>
          <option key={account.id} value={account.id}>{account.accountNumber}</option>)}
      </select>
      <label htmlFor="amount">Importe a transferir</label>
      <div className="amount-field"><input id="amount" inputMode="decimal" placeholder="0.00"
        value={amount} disabled={disabled} aria-describedby="amount-help transfer-error"
        onChange={event => setAmount(event.target.value.replace(',', '.'))} /><span>Importe</span></div>
      <p className="field-note" id="amount-help">Usa hasta dos decimales. Sin redondeos.</p>
      {error && <p className="form-error" id="transfer-error" role="alert">{error}</p>}
      {accounts.length < 2 && !loading && <p className="field-note">Necesitas al menos dos cuentas para transferir.</p>}
      <button type="submit" className="primary-button" disabled={disabled}>
        {submitting ? <><span className="spinner" /> Procesando transferencia…</> : <>Realizar transferencia <span>↗</span></>}
      </button>
      <div className="form-footer"><span>◈</span> Los saldos se actualizarán al confirmar.</div>
    </form>
  </section>
}
