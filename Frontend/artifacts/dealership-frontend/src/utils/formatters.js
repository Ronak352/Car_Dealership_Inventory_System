export function formatCurrency(value) {
  if (value === null || value === undefined || value === '') return '-'
  const num = Number(value)
  if (Number.isNaN(num)) return '-'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(num)
}

export function formatDate(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleDateString('en-IN', { year: 'numeric', month: 'short', day: 'numeric' })
}

export function formatDateTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleString('en-IN', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

export function titleCase(value) {
  if (!value) return ''
  return String(value)
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, (c) => c.toUpperCase())
}

// Client-side classification of a vehicle's stock level against a
// threshold. Not a backend enum -- VehicleResponse only ever carries a raw
// `quantity` integer, so "out of stock" / "low stock" / "in stock" are
// derived here purely for display (badges, filters) across the Inventory
// module pages.
export function getStockLevel(quantity, threshold = 5) {
  const qty = Number(quantity)
  if (Number.isNaN(qty) || qty <= 0) return 'OUT_OF_STOCK'
  if (qty <= threshold) return 'LOW_STOCK'
  return 'IN_STOCK'
}
