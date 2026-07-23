import React, { useMemo, useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowDown, ArrowUp, ArrowUpDown, Eye, Info, Pencil, Plus, Trash2, X } from 'lucide-react'
import { deleteVehicle, getAllVehicles } from '../../api/vehicleApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'
import { formatCurrency } from '../../utils/formatters'

const PAGE_SIZE_OPTIONS = [10, 25, 50]

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

export default function VehicleList() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { toast } = useToast()
  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const canDelete = user?.role === 'ADMIN'

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
  })

  const [searchText, setSearchText] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [sortBy, setSortBy] = useState('brand')
  const [sortDir, setSortDir] = useState('asc')
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [pendingDelete, setPendingDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const vehicles = data || []

  const filtered = useMemo(() => {
    const text = searchText.trim().toLowerCase()
    const min = minPrice !== '' ? Number(minPrice) : null
    const max = maxPrice !== '' ? Number(maxPrice) : null

    return vehicles.filter((v) => {
      const matchesText =
        !text ||
        [v.brand, v.model, v.variant, v.vinNumber].some((field) =>
          String(field || '').toLowerCase().includes(text)
        )
      const price = Number(v.price) || 0
      const matchesMin = min === null || price >= min
      const matchesMax = max === null || price <= max
      return matchesText && matchesMin && matchesMax
    })
  }, [vehicles, searchText, minPrice, maxPrice])

  const sorted = useMemo(() => {
    const list = [...filtered]
    list.sort((a, b) => {
      let av = a[sortBy]
      let bv = b[sortBy]
      if (sortBy === 'price' || sortBy === 'quantity') {
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
    setMinPrice('')
    setMaxPrice('')
    setPage(1)
  }

  async function handleDelete() {
    if (!pendingDelete) return
    setDeleting(true)
    try {
      await deleteVehicle(pendingDelete.id)
      toast.success(`${pendingDelete.brand} ${pendingDelete.model} was deleted.`)
      queryClient.invalidateQueries({ queryKey: ['vehicles'] })
    } catch (err) {
      toast.error(err.message || 'Could not delete vehicle.')
    } finally {
      setDeleting(false)
      setPendingDelete(null)
    }
  }

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
      key: 'model',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('model')}>
          Model <SortIcon active={sortBy === 'model'} direction={sortDir} />
        </button>
      ),
      render: (row) => (
        <span>
          {row.model}
          {row.variant ? <span className="text-gray-400"> · {row.variant}</span> : null}
        </span>
      ),
    },
    { key: 'vinNumber', header: 'VIN Number' },
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
          Qty <SortIcon active={sortBy === 'quantity'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'actions',
      header: 'Actions',
      render: (row) => (
        <div className="flex items-center gap-1">
          <button
            title="View details"
            className="rounded-lg p-1.5 text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800"
            onClick={() => navigate(`/vehicles/${row.id}`, { state: { vehicle: row } })}
          >
            <Eye className="h-4 w-4" />
          </button>
          {canManage && (
            <button
              title="Edit vehicle"
              className="rounded-lg p-1.5 text-gray-500 hover:bg-gray-100 dark:hover:bg-gray-800"
              onClick={() => navigate(`/vehicles/${row.id}/edit`, { state: { vehicle: row } })}
            >
              <Pencil className="h-4 w-4" />
            </button>
          )}
          {canDelete && (
            <button
              title="Delete vehicle"
              className="rounded-lg p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-950/40"
              onClick={() => setPendingDelete(row)}
            >
              <Trash2 className="h-4 w-4" />
            </button>
          )}
        </div>
      ),
    },
  ]

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Vehicle Inventory</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Full list of vehicles across every status.</p>
        </div>
        {canManage && (
          <Link to="/vehicles/add">
            <Button icon={Plus}>Add Vehicle</Button>
          </Link>
        )}
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
        <Input
          containerClassName="w-36"
          label="Min price"
          type="number"
          min="0"
          value={minPrice}
          onChange={(e) => {
            setMinPrice(e.target.value)
            setPage(1)
          }}
        />
        <Input
          containerClassName="w-36"
          label="Max price"
          type="number"
          min="0"
          value={maxPrice}
          onChange={(e) => {
            setMaxPrice(e.target.value)
            setPage(1)
          }}
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
        {(searchText || minPrice || maxPrice) && (
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
          emptyLabel={
            vehicles.length === 0
              ? 'No vehicles in inventory yet.'
              : 'No vehicles match your search or filters.'
          }
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
          Category, status, fuel type, transmission and a few other vehicle fields aren't shown: the backend's
          vehicle endpoints don't return them on any response, so displaying or filtering by them here would show
          incorrect or always-empty data. Search on the backend also matches brand/model exactly rather than by
          keyword, so this page fetches the full list once and searches/sorts/paginates the results locally
          instead.
        </p>
      </div>

      <Modal
        open={Boolean(pendingDelete)}
        onClose={() => setPendingDelete(null)}
        title="Delete this vehicle?"
        footer={
          <>
            <Button variant="secondary" onClick={() => setPendingDelete(null)}>
              Cancel
            </Button>
            <Button variant="danger" loading={deleting} onClick={handleDelete}>
              Delete
            </Button>
          </>
        }
      >
        {pendingDelete && (
          <p className="text-sm text-gray-600 dark:text-gray-300">
            This will permanently remove <strong>{pendingDelete.brand} {pendingDelete.model}</strong> (VIN{' '}
            {pendingDelete.vinNumber}) from the inventory. This cannot be undone.
          </p>
        )}
      </Modal>
    </div>
  )
}
