import { useEffect, useRef } from 'react'
import { createPortal } from 'react-dom'
import { QRCodeSVG } from 'qrcode.react'
import type { Transfer } from '../types'
import { academicNotice, downloadReceipt, maskAccount, receiptPayload } from '../receipt'
import { formatDate, formatMoney } from '../utils'

interface Props { transfer: Transfer; onClose: () => void }
const labels = { COMPLETED: 'Completada', PENDING: 'Pendiente', FAILED: 'Fallida' }

export function TransferReceipt({ transfer, onClose }: Props) {
  const dialog = useRef<HTMLDialogElement>(null)
  const qr = useRef<SVGSVGElement>(null)
  useEffect(() => {
    const element = dialog.current!
    const previousFocus = document.activeElement as HTMLElement | null
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    element.showModal()
    return () => {
      element.close()
      document.body.style.overflow = previousOverflow
      previousFocus?.focus()
    }
  }, [])
  return createPortal(<dialog ref={dialog} className="receipt-dialog" aria-labelledby="receipt-title"
    aria-describedby="receipt-notice" onCancel={event => { event.preventDefault(); onClose() }}>
    <div className="receipt-toolbar"><span>COMPROBANTE DE OPERACIÓN</span>
      <button className="receipt-close" onClick={onClose} aria-label="Cerrar comprobante" autoFocus>×</button></div>
    <article className="receipt-content">
      <div className="receipt-brand">nexo<span>.</span></div>
      <span className={`status-badge ${transfer.status.toLowerCase()}`}>{labels[transfer.status]}</span>
      <h2 id="receipt-title">Comprobante de transferencia</h2>
      <p className="receipt-amount">{formatMoney(transfer.amount)}</p>
      <dl className="receipt-details">
        <div className="receipt-reference"><dt>Referencia UUID</dt><dd>{transfer.reference}</dd></div>
        <div><dt>Cuenta de origen</dt><dd>{maskAccount(transfer.sourceAccountNumber)}</dd></div>
        <div><dt>Cuenta de destino</dt><dd>{maskAccount(transfer.destinationAccountNumber)}</dd></div>
        <div className="receipt-reference"><dt>Fecha y hora</dt><dd>{formatDate(transfer.createdAt)}</dd></div>
      </dl>
      <div className="receipt-qr">
        <QRCodeSVG ref={qr} value={receiptPayload(transfer)} size={280} level="M"
          marginSize={4} bgColor="#ffffff" fgColor="#122b2a" title="Código QR del comprobante académico" />
        <p>Escanea para consultar los datos básicos.</p>
      </div>
      <p className="receipt-notice" id="receipt-notice">{academicNotice}</p>
    </article>
    <div className="receipt-actions">
      <button className="refresh-button" onClick={() => {
        if (qr.current) downloadReceipt(transfer, new XMLSerializer().serializeToString(qr.current))
      }}>Descargar SVG</button>
      <button className="primary-button" onClick={() => window.print()}>Imprimir / Guardar PDF</button>
    </div>
  </dialog>, document.body)
}
