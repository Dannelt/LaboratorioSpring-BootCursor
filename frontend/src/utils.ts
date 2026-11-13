// The API does not declare a currency; do not invent a currency denomination.
export function formatMoney(value: number): string {
  return new Intl.NumberFormat('es-CO', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value)
}
export function formatDate(value: string): string {
  return new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))
}
export function validateTransfer(source: string, destination: string, amount: string): string | null {
  if (!source || !destination) return 'Selecciona una cuenta de origen y una de destino.'
  if (source === destination) return 'Las cuentas de origen y destino deben ser diferentes.'
  if (!/^(0|[1-9]\d{0,16})(\.\d{1,2})?$/.test(amount) || Number(amount) <= 0)
    return 'Ingresa un importe positivo con máximo dos decimales.'
  // JSON numeric DTOs use JavaScript numbers. Refuse values that cannot safely retain cents.
  const [integer, fraction = ''] = amount.split('.')
  const cents = BigInt(integer) * 100n + BigInt(fraction.padEnd(2, '0'))
  if (cents > BigInt(Number.MAX_SAFE_INTEGER) || BigInt(Math.round(Number(amount) * 100)) !== cents)
    return 'El importe excede la precisión segura de esta interfaz.'
  return null
}
