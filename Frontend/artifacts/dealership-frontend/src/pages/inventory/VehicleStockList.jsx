import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowDown, ArrowUp, ArrowUpDown, Boxes, Eye, Info, X } from 'lucide-react'
import { getAllVehicles } from '../../api/vehicleApi'
import { getLowStockVehicles } from '../../api/inventoryApi'
import { formatCurrency, getStockLevel } from '../../utils/formatters'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Badge from '../../components/common/Badge'

const PAGE_SIZE_OPTIONS = [10, 25, 50]
const STOCK_FILTERS = [
  { value: 'all', label: 'All stock levels' },
  { value: 'IN_STOCK', label: 'In stock' },
  { value: 'LOW_STOCK', label: 'Low stock' },
  { value: 'OUT_OF_STOCK', label: 'Out of stock' },
]

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

// Per-vehicle stock levels. There's no single backend endpoint that lists
// "every vehicle's stock" directly -- when the "Low stock" filter is active
// this calls the real GET /inventory/low-stock?threshold= endpoint (ADMIN,
// MANAGER only), and otherwise falls back to vehicleApi.getAllVehicles()
// (the same search-with-no-filters call VehicleList uses), since that's the
// only place a full catalog with quantity comes from. Everything else
// (text search, sort, pagination) happens client-side, matching the
// approach already used by VehicleList/EmployeeList.
export default function VehicleStockList() {
  const [searchText, setSearchText] = useState('')
  const [stockFilter, setStockFilter] = useState('all')
  const [threshold, setThreshold] = useState(5)
  const [sortBy, setSortBy] = useState('quantity')
  const [sortDir, setSortDir] = useState('asc')
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)

  const wantsLowStockOnly = stockFilter === 'LOW_STOCK'

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: wantsLowStockOnly ? ['inventory', 'low-stock', threshold] : ['vehicles', 'all'],
    queryFn: () => (wantsLowStockOnly ? getLowStockVehicles(threshold) : getAllVehicles()),
  })

  const vehicles = data || []

  const filtered = useMemo(() => {
    const text = searchText.trim().toLowerCase()
    return vehicles.filter((v) => {
      const matchesText =
        !text || [v.brand, v.model, v.variant, v.vinNumber].some((f) => String(f || '').toLowerCase().includes(text))
      // When the low-stock endpoint is already the data source, every row
      // already qualifies -- the client-side level check below is only
      // needed for the OUT_OF_STOCK / IN_STOCK filters against the full list.
      const level = getStockLevel(v.quantity, threshold)
      const matchesStock =
        stockFilter === 'all' || stockFilter === 'LOW_STOCK' || level === stockFilter
      return matchesText && matchesStock
    })
  }, [vehicles, searchText, stockFilter, threshold])

  const sorted = useMemo(() => {
    const list = [...filtered]
    list.sort((a, b) => {
      let av = a[sortBy]
      let bv = b[sortBy]
      if (sortBy === 'quantity' || sortBy === 'price') {
        av = Number(av) || 0
        bv = Number(bv) || 0
      } else {
        av = String(av || '').toLowerCase()
        bv = String(bv || '').toLowerCase()
      }
      if (av < bv) return sortDir === 'asc' ? -1 : 1
      if (av > bv) return sortDir === 'asc' ? 1 : -1
      return 0
    })
    return list
  }, [filtered, sortBy, sortDir])

  const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize))
  const currentPage = Math.min(page, totalPages)
  const pageStart = (currentPage - 1) * pageSize
  const paginated = sorted.slice(pageStart, pageStart + pageSize)

  function toggleSort(key) {
    if (sortBy === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortBy(key)
      setSortDir('asc')
    }
    setPage(1)
  }

  function clearFilters() {
    setSearchText('')
    setStockFilter('all')
    setThreshold(5)
    setPage(1)
  }

  const columns = [
    {
      key: 'brand',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('brand')}>
          Brand / Model <SortIcon active={sortBy === 'brand'} direction={sortDir} />
        </button>
      ),
      render: (row) => (
        <div>
          <p className="font-medium">
            {row.brand} {row.model}
          </p>
          <p className="text-xs text-gray-400">{row.variant || '—'}</p>
        </div>
      ),
    },
    { key: 'vinNumber', header: 'VIN' },
    {
      key: 'price',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('price')}>
          Price <SortIcon active={sortBy === 'price'} direction={sortDir} />
        </button>
      ),
      render: (row) => formatCurrency(row.price),
    },
    {
      key: 'quantity',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('quantity')}>
          Quantity <SortIcon active={sortBy === 'quantity'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'stockLevel',
      header: 'Stock Level',
      render: (row) => <Badge status={getStockLevel(row.quantity, threshold)} />,
    },
    {
      key: 'actions',
      header: '',
      render: (row) => (
        <div className="flex items-center gap-1">
          <Link
            to={`/vehicles/${row.id}`}
            state={{ vehicle: row }}
            className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30 inline-flex"
            title="View vehicle"
          >
            <Eye className="h-4 w-4" />
          </Link>
          <Link
            to={`/inventory/stock/${row.id}`}
            state={{ vehicle: row }}
            className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30 inline-flex"
            title="Manage stock"
          >
            <Boxes className="h-4 w-4" />
          </Link>
        </div>
      ),
    },
  ]

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-semibold">Vehicle Stock List</h1>
        <p className="text-sm text-gray-500 dark:text-gray-400">Current stock quantity for every vehicle.</p>
      </div>

      <div className="card p-4 flex flex-wrap items-end gap-3">
        <Input
          containerClassName="flex-1 min-w-[220px]"
          label="Search"
          placeholder="Brand, model, variant or VIN"
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value)
            setPage(1)
          }}
        />
        <Select
          containerClassName="w-48"
          label="Stock level"
          value={stockFilter}
          onChange={(e) => {
            setStockFilter(e.target.value)
            setPage(1)
          }}
          options={STOCK_FILTERS}
        />
        <Input
          containerClassName="w-32"
          label="Threshold"
          type="number"
          min={0}
          value={threshold}
          onChange={(e) => {
            setThreshold(Math.max(0, Number(e.target.value) || 0))
            setPage(1)
          }}
          hint="Low-stock cutoff"
        />
        <Select
          containerClassName="w-32"
          label="Per page"
          value={pageSize}
          onChange={(e) => {
            setPageSize(Number(e.target.value))
            setPage(1)
          }}
          options={PAGE_SIZE_OPTIONS.map((n) => ({ value: n, label: `${n} / page` }))}
        />
        {(searchText || stockFilter !== 'all' || threshold !== 5) && (
          <button
            onClick={clearFilters}
            className="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 dark:hover:text-gray-300 pb-2"
          >
            <X className="h-4 w-4" /> Clear
          </button>
        )}
      </div>

      <div className="card p-4">
        <Table
          columns={columns}
          data={paginated}
          isLoading={isLoading}
          error={error?.message}
          onRetry={refetch}
          emptyLabel={vehicles.length === 0 ? 'No vehicles in inventory yet.' : 'No vehicles match your search or filters.'}
        />

        {!isLoading && !error && sorted.length > 0 && (
          <div className="flex flex-wrap items-center justify-between gap-3 pt-4 text-sm text-gray-500 dark:text-gray-400">
            <span>
              Showing {pageStart + 1}-{Math.min(pageStart + pageSize, sorted.length)} of {sorted.length}
            </span>
            <div className="flex items-center gap-2">
              <button
                className="btn secondary px-3 py-1.5 rounded-lg border border-gray-300 dark:border-gray-700 disabled:opacity-40"
                disabled={currentPage <= 1}
                onClick={() => setPage((p) => Math.max(1, p - 1))}
              >
                Previous
              </button>
              <span>
                Page {currentPage} of {totalPages}
              </span>
              <button
                className="btn secondary px-3 py-1.5 rounded-lg border border-gray-300 dark:border-gray-700 disabled:opacity-40"
                disabled={currentPage >= totalPages}
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Selecting "Low stock" switches this to the backend's real <code>/inventory/low-stock</code> endpoint using
          the threshold above; "In stock" / "Out of stock" filter the full catalog locally instead, since the
          backend only has a low-stock endpoint, not one for every level. Use the{' '}
          <Boxes className="h-3.5 w-3.5 inline -mt-0.5" /> icon on any row to restock, reduce, or set an exact
          quantity and view its movement history.
        </p>
      </div>
    </div>
  )
}
