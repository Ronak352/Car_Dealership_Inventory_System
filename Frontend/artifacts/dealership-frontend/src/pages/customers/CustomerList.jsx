import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowDown, ArrowUp, ArrowUpDown, Eye, Info, Pencil, PlusCircle, Trash2, X } from 'lucide-react'
import { deleteCustomer, getAllCustomers } from '../../api/customerApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'

const PAGE_SIZE_OPTIONS = [10, 25, 50]
const PROFILE_FILTERS = [
  { value: 'all', label: 'All profiles' },
  { value: 'complete', label: 'Has address on file' },
  { value: 'incomplete', label: 'Missing address' },
]

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

export default function CustomerList() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['customers', 'all'],
    queryFn: getAllCustomers,
  })
  const { user } = useAuth()
  const { toast } = useToast()

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const canDelete = user?.role === 'ADMIN'

  const [searchText, setSearchText] = useState('')
  const [profileFilter, setProfileFilter] = useState('all')
  const [sortBy, setSortBy] = useState('fullName')
  const [sortDir, setSortDir] = useState('asc')
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const customers = data || []

  const filtered = useMemo(() => {
    const text = searchText.trim().toLowerCase()
    return customers.filter((c) => {
      const matchesText =
        !text ||
        [c.fullName, c.email, c.phone, c.address].some((field) =>
          String(field || '').toLowerCase().includes(text)
        )
      const hasAddress = Boolean(c.address && c.address.trim().length > 0)
      const matchesProfile =
        profileFilter === 'all' ||
        (profileFilter === 'complete' && hasAddress) ||
        (profileFilter === 'incomplete' && !hasAddress)
      return matchesText && matchesProfile
    })
  }, [customers, searchText, profileFilter])

  const sorted = useMemo(() => {
    const list = [...filtered]
    list.sort((a, b) => {
      const av = String(a[sortBy] || '').toLowerCase()
      const bv = String(b[sortBy] || '').toLowerCase()
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
    setProfileFilter('all')
    setPage(1)
  }

  async function handleDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    try {
      await deleteCustomer(deleteTarget.id)
      toast.success(`${deleteTarget.fullName}'s customer profile was deleted.`)
      refetch()
    } catch (err) {
      toast.error(err.message || 'Could not delete customer profile.')
    } finally {
      setDeleting(false)
      setDeleteTarget(null)
    }
  }

  const columns = [
    {
      key: 'fullName',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('fullName')}>
          Name <SortIcon active={sortBy === 'fullName'} direction={sortDir} />
        </button>
      ),
      render: (row) => <span className="font-medium">{row.fullName}</span>,
    },
    {
      key: 'email',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('email')}>
          Email <SortIcon active={sortBy === 'email'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'phone',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('phone')}>
          Phone <SortIcon active={sortBy === 'phone'} direction={sortDir} />
        </button>
      ),
    },
    {
      key: 'address',
      header: 'Address',
      render: (row) => row.address || <span className="text-gray-400">Not on file</span>,
    },
    {
      key: 'actions',
      header: '',
      render: (row) => (
        <div className="flex items-center justify-end gap-1">
          <Link
            to={`/customers/${row.id}`}
            state={{ customer: row }}
            className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30"
            title="View"
          >
            <Eye className="h-4 w-4" />
          </Link>
          {canManage && (
            <Link
              to={`/customers/${row.id}/edit`}
              state={{ customer: row }}
              className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30"
              title="Edit"
            >
              <Pencil className="h-4 w-4" />
            </Link>
          )}
          {canDelete && (
            <button
              onClick={() => setDeleteTarget(row)}
              className="p-1.5 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30"
              title="Delete"
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
          <h1 className="text-xl font-semibold">Customers</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Every customer profile on file.</p>
        </div>
        {canManage && (
          <Link to="/customers/add">
            <Button icon={PlusCircle}>Add Customer</Button>
          </Link>
        )}
      </div>

      <div className="card p-4 flex flex-wrap items-end gap-3">
        <Input
          containerClassName="flex-1 min-w-[220px]"
          label="Search"
          placeholder="Name, email, phone or address"
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value)
            setPage(1)
          }}
        />
        <Select
          containerClassName="w-56"
          label="Profile status"
          value={profileFilter}
          onChange={(e) => {
            setProfileFilter(e.target.value)
            setPage(1)
          }}
          options={PROFILE_FILTERS}
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
        {(searchText || profileFilter !== 'all') && (
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
            customers.length === 0 ? 'No customer profiles yet.' : 'No customers match your search or filters.'
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
          City, state and pincode aren't shown or filterable here: the backend accepts them when a profile is
          created but never returns them on any customer response, so this page only has address text to search
          and display. There's also no backend search/pagination for customers, so the full list is fetched once
          and searched/sorted/paginated locally.
        </p>
      </div>

      <Modal
        open={Boolean(deleteTarget)}
        onClose={() => setDeleteTarget(null)}
        title="Delete this customer profile?"
        footer={
          <>
            <Button variant="secondary" onClick={() => setDeleteTarget(null)}>
              Cancel
            </Button>
            <Button variant="danger" loading={deleting} onClick={handleDelete}>
              Delete
            </Button>
          </>
        }
      >
        {deleteTarget && (
          <p className="text-sm text-gray-600 dark:text-gray-300">
            This will permanently remove <strong>{deleteTarget.fullName}</strong>'s customer profile (
            {deleteTarget.email}). This cannot be undone.
          </p>
        )}
      </Modal>
    </div>
  )
}
