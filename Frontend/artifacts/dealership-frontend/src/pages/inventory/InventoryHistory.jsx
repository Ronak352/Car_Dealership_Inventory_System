import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useParams } from 'react-router-dom'
import { ArrowDown, ArrowLeft, ArrowUp, ArrowUpDown, Info } from 'lucide-react'
import { getAllVehicles } from '../../api/vehicleApi'
import { getInventoryHistory } from '../../api/inventoryApi'
import { formatDateTime } from '../../utils/formatters'
import { INVENTORY_OPERATIONS } from '../../utils/constants'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Table from '../../components/common/Table'
import Select from '../../components/common/Select'
import Badge from '../../components/common/Badge'

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

// Full movement log for a single vehicle, backed directly by
// GET /api/inventory/history/{vehicleId} (ADMIN, MANAGER only -- matching
// this page's route guard). The backend already returns entries newest
// first; sort/filter here just re-orders that same list client-side, since
// there's no dedicated endpoint for filtering by operation type.
export default function InventoryHistory() {
  const { vehicleId } = useParams()
  const location = useLocation()

  const stateVehicle = location.state?.vehicle
  const needsFallbackFetch = !stateVehicle

  const {
    data: vehicleList,
    isLoading: vehicleLoading,
    error: vehicleError,
  } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
    enabled: needsFallbackFetch,
  })

  const vehicle = useMemo(() => {
    if (stateVehicle) return stateVehicle
    return (vehicleList || []).find((v) => String(v.id) === String(vehicleId)) || null
  }, [stateVehicle, vehicleList, vehicleId])

  const {
    data: history,
    isLoading: historyLoading,
    error: historyError,
    refetch,
  } = useQuery({
    queryKey: ['inventory', 'history', vehicleId],
    queryFn: () => getInventoryHistory(vehicleId),
  })

  const [operationFilter, setOperationFilter] = useState('all')
  const [sortBy, setSortBy] = useState('date')
  const [sortDir, setSortDir] = useState('desc')

  const filtered = useMemo(() => {
    const list = history || []
    if (operationFilter === 'all') return list
    return list.filter((entry) => entry.operationType === operationFilter)
  }, [history, operationFilter])

  const sorted = useMemo(() => {
    const list = [...filtered]
    list.sort((a, b) => {
      let av = a[sortBy]
      let bv = b[sortBy]
      if (sortBy === 'date') {
        av = new Date(av).getTime()
        bv = new Date(bv).getTime()
      } else if (sortBy === 'quantity' || sortBy === 'availableQuantity') {
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

  function toggleSort(key) {
    if (sortBy === key) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'))
    } else {
      setSortBy(key)
      setSortDir('desc')
    }
  }

  const isLoading = (needsFallbackFetch && vehicleLoading) || historyLoading
  const loadError = (needsFallbackFetch && vehicleError) || historyError

  const columns = [
    {
      key: 'date',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('date')}>
          Date <SortIcon active={sortBy === 'date'} direction={sortDir} />
        </button>
      ),
      render: (row) => formatDateTime(row.date),
    },
    {
      key: 'operationType',
      header: 'Operation',
      render: (row) => <Badge status={row.operationType} />,
    },
    {
      key: 'quantity',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('quantity')}>
          Change <SortIcon active={sortBy === 'quantity'} direction={sortDir} />
        </button>
      ),
      render: (row) => {
        const sign = row.operationType === 'REMOVE' ? '-' : row.operationType === 'ADD' ? '+' : row.quantity < 0 ? '-' : '+'
        return `${sign}${Math.abs(row.quantity)}`
      },
    },
    {
      key: 'availableQuantity',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('availableQuantity')}>
          Stock After <SortIcon active={sortBy === 'availableQuantity'} direction={sortDir} />
        </button>
      ),
    },
    { key: 'performedByName', header: 'Performed By', render: (row) => row.performedByName || '—' },
  ]

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3">
        <Link
          to={vehicle ? `/inventory/stock/${vehicleId}` : '/inventory/stock-list'}
          state={vehicle ? { vehicle } : undefined}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
        >
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div className="min-w-0">
          <h1 className="text-xl font-semibold truncate">
            {vehicle ? `${vehicle.brand} ${vehicle.model} -- Inventory History` : 'Inventory History'}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {vehicle ? `VIN ${vehicle.vinNumber}` : `Vehicle #${vehicleId}`}
          </p>
        </div>
      </div>

      <div className="card p-4 flex flex-wrap items-end gap-3">
        <Select
          containerClassName="w-48"
          label="Operation type"
          value={operationFilter}
          onChange={(e) => setOperationFilter(e.target.value)}
          options={[{ value: 'all', label: 'All operations' }, ...INVENTORY_OPERATIONS.map((op) => ({ value: op, label: op }))]}
        />
      </div>

      <div className="card p-4">
        <Table
          columns={columns}
          data={sorted}
          isLoading={isLoading}
          error={loadError?.message}
          onRetry={refetch}
          emptyLabel={
            (history || []).length === 0
              ? 'No stock movements recorded for this vehicle yet.'
              : 'No movements match this filter.'
          }
        />
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Sourced directly from <code>GET /inventory/history/{'{vehicleId}'}</code>, which the backend already
          returns newest first. Filtering by operation type and re-sorting happens locally against that same list.
        </p>
      </div>
    </div>
  )
}
