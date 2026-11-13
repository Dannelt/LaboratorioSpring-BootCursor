import { useCallback, useEffect, useRef, useState } from 'react'
import axios from 'axios'
import toast, { Toaster } from 'react-hot-toast'
import type { Account, Transfer, TransferRequest } from './types'
import { bankingApi, errorMessage } from './services/api'
import { AccountCard } from './components/AccountCard'
import { TransferForm } from './components/TransferForm'
import { TransferHistory } from './components/TransferHistory'
import { TransferReceipt } from './components/TransferReceipt'

export default function App() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [accountError, setAccountError] = useState<string | null>(null)
  const [history, setHistory] = useState<Transfer[]>([])
  const [historyLoading, setHistoryLoading] = useState(false)
  const [historyError, setHistoryError] = useState<string | null>(null)
  const [revision, setRevision] = useState(0)
  const loadVersion = useRef(0)
  const [receipt, setReceipt] = useState<Transfer | null>(null)
  const [receiptLoading, setReceiptLoading] = useState(false)
  const receiptRequest = useRef<AbortController | null>(null)
  useEffect(() => () => receiptRequest.current?.abort(), [])
  async function openReceipt(reference: string) {
    receiptRequest.current?.abort()
    const controller = new AbortController()
    receiptRequest.current = controller
    setReceiptLoading(true)
    try {
      const result = await bankingApi.getTransfer(reference, controller.signal)
      if (!controller.signal.aborted) setReceipt(result)
    } catch (error) {
      if (!controller.signal.aborted) {
        toast.error(axios.isAxiosError(error) && error.response?.status === 404
          ? 'No se encontró el comprobante de esta transferencia.' : errorMessage(error))
      }
    } finally {
      if (!controller.signal.aborted) setReceiptLoading(false)
    }
  }
  const loadAccounts = useCallback(async () => {
    const version = ++loadVersion.current
    setLoading(true)
    setAccountError(null)
    try {
      const result = await bankingApi.getAccounts()
      if (version !== loadVersion.current) return false
      setAccounts(result)
      setSelectedId(current => result.some(account => account.id === current) ? current : result[0]?.id ?? null)
      return true
    } catch (error) {
      if (version === loadVersion.current) setAccountError(errorMessage(error))
      return false
    } finally { if (version === loadVersion.current) setLoading(false) }
  }, [])
  useEffect(() => { void loadAccounts(); return () => { loadVersion.current++ } }, [loadAccounts])
  useEffect(() => {
    const controller = new AbortController()
    let active = true
    setHistory([])
    setHistoryError(null)
    if (selectedId === null) { setHistoryLoading(false); return }
    setHistoryLoading(true)
    bankingApi.getHistory(selectedId, controller.signal).then(result => {
      if (active) setHistory(result)
    }).catch(error => {
      if (active && !axios.isCancel(error)) setHistoryError(errorMessage(error))
    }).finally(() => { if (active) setHistoryLoading(false) })
    return () => { active = false; controller.abort() }
  }, [selectedId, revision])
  async function transfer(request: TransferRequest): Promise<boolean> {
    let receipt: Transfer
    try { receipt = await bankingApi.transfer(request) }
    catch (error) {
      toast.error(errorMessage(error))
      // Reconcile after any rejected or uncertain response; never automatically retry a POST.
      await loadAccounts()
      setRevision(value => value + 1)
      return false
    }
    toast.success(`Transferencia completada · ${receipt.reference.slice(0, 8)}`)
    receiptRequest.current?.abort()
    setReceiptLoading(false)
    setReceipt(receipt)
    const refreshed = await loadAccounts()
    setRevision(value => value + 1)
    if (!refreshed) toast.error('La transferencia se confirmó, pero no se pudieron actualizar los saldos. Actualiza antes de continuar.')
    return true
  }
  const selected = accounts.find(account => account.id === selectedId)
  return <div className="app-shell">
    <Toaster position="top-right" toastOptions={{ duration: 6500, style: { borderRadius: '12px', fontSize: '14px' } }} />
    <header className="site-header"><a href="#" className="brand" aria-label="Nexo, inicio"><span className="brand-mark">n.</span><span>nexo<span className="brand-dot">.</span></span></a>
      <div className="header-divider" /><span className="header-label">Laboratorio bancario</span>
      <span className="environment-badge"><span /> Entorno académico</span>
    </header>
    <main>
      <div className="page-heading"><div><div className="eyebrow">TU ESPACIO FINANCIERO</div>
        <h1>Todo en <em>movimiento.</em></h1><p>Consulta tus cuentas, transfiere y sigue cada operación.</p></div>
        <button className="refresh-button" onClick={() => { void loadAccounts(); setRevision(value => value + 1) }} disabled={loading}>
          <span className={loading ? 'rotating' : ''}>↻</span> Actualizar</button></div>
      <section aria-labelledby="accounts-title" className="accounts-section">
        <div className="accounts-heading"><h2 id="accounts-title">Tus cuentas <span>{accounts.length.toString().padStart(2, '0')}</span></h2>
          <p>Selecciona una para ver sus movimientos</p></div>
        {loading && accounts.length === 0 ? <div className="account-grid" role="status" aria-label="Cargando cuentas"><div className="skeleton" /><div className="skeleton" /></div>
          : accountError ? <div className="inline-error" role="alert"><strong>No pudimos actualizar tus cuentas</strong><p>{accountError}</p><button className="text-button" onClick={() => void loadAccounts()}>Reintentar</button></div>
          : accounts.length === 0 ? <div className="panel empty-state"><h3>No hay cuentas disponibles</h3><p>Cuando existan cuentas en el sistema, podrás consultarlas aquí.</p></div>
          : <div className="account-grid">{accounts.map(account => <AccountCard key={account.id} account={account} selected={account.id === selectedId} onSelect={() => setSelectedId(account.id)} />)}</div>}
      </section>
      <div className="workspace-grid"><TransferForm accounts={accounts} loading={loading || !!accountError} onTransfer={transfer} />
        <div className="history-column"><TransferHistory account={selected} transfers={history} loading={historyLoading}
          error={historyError} onRetry={() => setRevision(value => value + 1)}
          onReceipt={openReceipt} receiptLoading={receiptLoading} />
          <div className="connection-note"><span>◈</span><p><strong>Un registro para cada movimiento.</strong> Consulta el origen, destino y estado de tus transferencias en un solo lugar.</p></div>
        </div></div>
      <footer><span>nexo. <span>Laboratorio de transferencias bancarias</span></span>
        <p>Uso académico · Sin autenticación · No exponer públicamente</p></footer>
    </main>
    {receipt && <TransferReceipt transfer={receipt} onClose={() => setReceipt(null)} />}
  </div>
}
