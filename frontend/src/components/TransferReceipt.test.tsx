import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import jsQR from 'jsqr'
import type { Transfer } from '../types'
import { receiptPayload, receiptSvg, maskAccount } from '../receipt'
import { TransferReceipt } from './TransferReceipt'

const transfer: Transfer = {
  reference: '550e8400-e29b-41d4-a716-446655440000',
  sourceAccountNumber: 'LAB-DEMO-001', destinationAccountNumber: 'LAB-DEMO-002',
  amount: 25.5, createdAt: '2026-01-01T12:34:56Z', status: 'COMPLETED',
}
afterEach(() => { cleanup(); vi.unstubAllGlobals() })

// Rasterize the SVG's integer-module horizontal runs, then decode with an independent QR reader.
function decodeSvg(svg: SVGSVGElement) {
  const modules = Number(svg.getAttribute('viewBox')!.split(' ')[2])
  const scale = 6
  const width = modules * scale
  const pixels = new Uint8ClampedArray(width * width * 4).fill(255)
  const foreground = svg.querySelectorAll('path')[1].getAttribute('d')!
  for (const match of foreground.matchAll(/M(\d+)[ ,](\d+)\s*h(\d+)v1H\d+z/g)) {
    const [, x, y, run] = match.map(Number)
    for (let dy = 0; dy < scale; dy++) {
      for (let dx = 0; dx < run * scale; dx++) {
        const offset = ((y * scale + dy) * width + x * scale + dx) * 4
        pixels[offset] = pixels[offset + 1] = pixels[offset + 2] = 0
      }
    }
  }
  return jsQR(pixels, width, width)
}

describe('transfer receipt', () => {
  it('shows the actual receipt and closes without any banking action', () => {
    const close = vi.fn()
    render(<TransferReceipt transfer={transfer} onClose={close} />)
    expect(screen.getByRole('dialog').textContent).toContain(transfer.reference)
    expect(screen.getByText('Completada')).toBeTruthy()
    expect(screen.getByText('****-001')).toBeTruthy()
    expect(screen.getByText('****-002')).toBeTruthy()
    expect(screen.getByText('25,50')).toBeTruthy()
    expect(screen.getByText(/No es una firma digital/)).toBeTruthy()
    fireEvent.click(screen.getByRole('button', { name: 'Cerrar comprobante' }))
    expect(close).toHaveBeenCalledOnce()
  })

  it('produces a QR that an independent reader decodes to the expected allowlisted data', () => {
    render(<TransferReceipt transfer={transfer} onClose={vi.fn()} />)
    const result = decodeSvg(document.querySelector('.receipt-qr svg') as SVGSVGElement)
    expect(result?.data).toBe(receiptPayload(transfer))
    expect(JSON.parse(result!.data)).toEqual({
      application: 'Nexo', type: 'academic-transfer-receipt', version: 1,
      reference: transfer.reference, sourceAccount: '****-001', destinationAccount: '****-002',
      amount: '25.5', createdAt: transfer.createdAt, status: 'COMPLETED',
    })
    expect(result!.data).not.toContain('LAB-DEMO')
  })

  it('is deterministic and does not leak additional fields', () => {
    const extra = { ...transfer, balance: 1000, password: 'secret', token: 'secret' }
    expect(receiptPayload(extra)).toBe(receiptPayload({ ...transfer }))
    expect(receiptPayload(extra)).not.toMatch(/secret|balance|password|token/)
    expect(maskAccount('123')).toBe('****')
  })

  it('downloads an independent SVG picture including the same decodable QR', async () => {
    const createUrl = vi.fn((_blob: Blob) => 'blob:receipt')
    vi.stubGlobal('URL', { createObjectURL: createUrl, revokeObjectURL: vi.fn() })
    const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {})
    render(<TransferReceipt transfer={transfer} onClose={vi.fn()} />)
    fireEvent.click(screen.getByRole('button', { name: 'Descargar SVG' }))
    expect(click).toHaveBeenCalledOnce()
    expect((click.mock.instances[0] as HTMLAnchorElement).download).toBe('nexo-comprobante-' + transfer.reference + '.svg')
    const blob = createUrl.mock.calls[0][0] as Blob
    const text = await new Promise<string>(resolve => {
      const reader = new FileReader()
      reader.onload = () => resolve(String(reader.result))
      reader.readAsText(blob)
    })
    const image = new DOMParser().parseFromString(text, 'image/svg+xml')
    expect(image.querySelector('parsererror')).toBeNull()
    expect(image.documentElement.textContent).toContain(transfer.reference)
    const qr = image.querySelector('svg svg') as unknown as SVGSVGElement
    expect(decodeSvg(qr)?.data).toBe(receiptPayload(transfer))
  })

  it('opens native printing for PDF output', () => {
    const print = vi.spyOn(window, 'print').mockImplementation(() => {})
    render(<TransferReceipt transfer={transfer} onClose={vi.fn()} />)
    fireEvent.click(screen.getByRole('button', { name: 'Imprimir / Guardar PDF' }))
    expect(print).toHaveBeenCalledOnce()
  })

  it('keeps the QR square inside the receipt', () => {
    render(<TransferReceipt transfer={transfer} onClose={vi.fn()} />)
    const svg = document.querySelector('.receipt-qr svg')!
    expect(svg.getAttribute('width')).toBe(svg.getAttribute('height'))
  })

  it('escapes values when exporting an SVG picture', () => {
    const image = receiptSvg({ ...transfer, reference: '<script>bad</script>' }, '<svg viewBox="0 0 10 10"></svg>')
    expect(image).toContain('&lt;script&gt;')
    expect(image).not.toContain('<script>')
  })
})
