import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowDown, ArrowUp, ArrowUpDown, Info } from 'lucide-react'
import { getAllVehicles } from '../../api/vehicleApi'
import { formatCurrency, getStockLevel } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Table from '../../components/common/Table'
import Badge from '../../components/common/Badge'

const LOW_STOCK_THRESHOLD = 5

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

// Aggregates stock by BRAND rather than by category: VehicleResponse.category
// is declared on the DTO but VehicleServiceImpl never sets it on any
// endpoint this page can reach (getAvailableVehicles/searchVehicles both
// leave it null -- see vehicleApi.js), so a category breakdown here would
// silently show one big "unknown" bucket. Brand is always populated, so
// that's the grouping that's actually meaningful with this data.
export default function StockOverview() {
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['vehicles', 'all'], queryFn: getAllVehicles })

  const [sortBy, setSortBy] = useState('totalUnits')
  const [sortDir, setSortDir] = useState('desc')

  const rows = useMemo(() => {
    const list = data || []
    const byBrand = new Map()
    for (const v of list) {
      const key = v.brand || 'Unknown'
      if (!byBrand.has(key)) {
        byBrand.set(key, { brand: key, listings: 0, totalUnits: 0, lowStock: 0, outOfStock: 0, totalValue: 0 })
      }
      const entry = byBrand.get(key)
      entry.listings += 1
      entry.totalUnits += Number(v.quantity) || 0
      entry.totalValue += (Number(v.price) || 0) * (Number(v.quantity) || 0)
      const level = getStockLevel(v.quantity, LOW_STOCK_THRESHOLD)
      if (level === 'LOW_STOCK') entry.lowStock += 1
      if (level === 'OUT_OF_STOCK') entry.outOfStock += 1
    }
    return Array.from(byBrand.values())
  }, [data])

  const sorted = useMemo(() => {
    const list = [...rows]
    list.sort((a, b) => {
      let av = a[sortBy]
      let bv = b[sortBy]
      if (typeof av === 'string') {
        av = av.toLowerCase()
        bv = String(bv).toLowerCase()
      }
      if (av < bv) return sortDir === 'asc' ? -1 : 1
      if (av > bv) return sortDir === 'asc' ? 1 : -1
      return 0
    })
    return list
  }, [rows, sortBy, sortDir])

  function toggleSort(key) {
    if (sortBy === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortBy(key)
      setSortDir(key === 'brand' ? 'asc' : 'desc')
    }
  }

  if (isLoading) return <Loader label="Loading stock overview..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load stock data.'} onRetry={refetch} />

  const columns = [
    {
      key: 'brand',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('brand')}>
          Brand <SortIcon active={sortBy === 'brand'} direction={sortDir} />
        </button>
      ),
      render: (row) => <span className="font-medium">{row.brand}</span>,
    },
    {
      key: 'listings',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('listings')}>
          Listings <SortIcon active={sortBy === 'listings'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'totalUnits',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('totalUnits')}>
          Total Units <SortIcon active={sortBy === 'totalUnits'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'totalValue',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('totalValue')}>
          Stock Value <SortIcon active={sortBy === 'totalValue'} direction={sortDir} />
        </button>
      ),
      render: (row) => formatCurrency(row.totalValue),
    },
    {
      key: 'lowStock',
      header: 'Low Stock',
      render: (row) =>
        row.lowStock > 0 ? (
          <span className="flex items-center gap-1.5">
            <Badge status="LOW_STOCK" /> {row.lowStock}
          </span>
        ) : (
          <span className="text-gray-400">—</span>
        ),
    },
    {
      key: 'outOfStock',
      header: 'Out of Stock',
      render: (row) =>
        row.outOfStock > 0 ? (
          <span className="flex items-center gap-1.5">
            <Badge status="OUT_OF_STOCK" /> {row.outOfStock}
          </span>
        ) : (
          <span className="text-gray-400">—</span>
        ),
    },
  ]

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-xl font-semibold">Stock Overview</h1>
        <p className="text-sm text-gray-500 dark:text-gray-400">Stock levels aggregated by brand.</p>
      </div>

      <div className="card p-4">
        <Table
          columns={columns}
          data={sorted}
          isLoading={false}
          keyField="brand"
          emptyLabel="No vehicles in inventory yet."
        />
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Grouped by brand rather than category: the backend accepts a category when a vehicle is added, but never
          returns it on the endpoints this page reads from, so a category breakdown would be inaccurate here. See
          the{' '}
          <Link to="/inventory/stock-list" className="underline">
            Vehicle Stock List
          </Link>{' '}
          for a per-vehicle view.
        </p>
      </div>
    </div>
  )
}
