import React, { useEffect, useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowDown, ArrowUp, ArrowUpDown, Eye, Info, Pencil, PlusCircle, ShieldQuestion, Trash2, X } from 'lucide-react'
import { deleteEmployee, getAllEmployees, searchEmployees } from '../../api/employeeApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import { formatCurrency, formatDate } from '../../utils/formatters'
import { ROLES } from '../../utils/constants'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'
import Badge from '../../components/common/Badge'

const PAGE_SIZE_OPTIONS = [10, 25, 50]
const ROLE_FILTERS = [{ value: 'all', label: 'All roles' }, ...Object.values(ROLES).map((r) => ({ value: r, label: r }))]

function SortIcon({ active, direction }) {
  if (!active) return <ArrowUpDown className="h-3.5 w-3.5 text-gray-300" />
  return direction === 'asc' ? (
    <ArrowUp className="h-3.5 w-3.5 text-primary-600" />
  ) : (
    <ArrowDown className="h-3.5 w-3.5 text-primary-600" />
  )
}

// Unlike CustomerList (no backend search at all), EmployeeController exposes
// GET /api/employees/search?keyword=, which matches employeeCode / first
// name / last name / email server-side. So the search box here is debounced
// and hits that endpoint directly instead of filtering client-side; only
// role-filter/sort/pagination happen locally afterwards, since the backend
// has no support for those.
export default function EmployeeList() {
  const { user } = useAuth()
  const { toast } = useToast()

  const canManage = user?.role === 'ADMIN'

  const [searchInput, setSearchInput] = useState('')
  const [debouncedSearch, setDebouncedSearch] = useState('')
  const [roleFilter, setRoleFilter] = useState('all')
  const [sortBy, setSortBy] = useState('fullName')
  const [sortDir, setSortDir] = useState('asc')
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    const t = setTimeout(() => setDebouncedSearch(searchInput.trim()), 300)
    return () => clearTimeout(t)
  }, [searchInput])

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['employees', debouncedSearch || 'all'],
    queryFn: () => (debouncedSearch ? searchEmployees(debouncedSearch) : getAllEmployees()),
  })

  const employees = data || []

  const filtered = useMemo(() => {
    return employees.filter((e) => roleFilter === 'all' || e.role === roleFilter)
  }, [employees, roleFilter])

  const sorted = useMemo(() => {
    const list = [...filtered]
    list.sort((a, b) => {
      let av = a[sortBy]
      let bv = b[sortBy]
      if (sortBy === 'salary') {
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
    setSearchInput('')
    setRoleFilter('all')
    setPage(1)
  }

  async function handleDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    try {
      await deleteEmployee(deleteTarget.id)
      toast.success(`${deleteTarget.fullName}'s employee record was deleted.`)
      refetch()
    } catch (err) {
      toast.error(err.message || 'Could not delete employee.')
    } finally {
      setDeleting(false)
      setDeleteTarget(null)
    }
  }

  const columns = [
    {
      key: 'employeeCode',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('employeeCode')}>
          Code <SortIcon active={sortBy === 'employeeCode'} direction={sortDir} />
        </button>
      ),
      render: (row) => <span className="font-medium">{row.employeeCode}</span>,
    },
    {
      key: 'fullName',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('fullName')}>
          Name <SortIcon active={sortBy === 'fullName'} direction={sortDir} />
        </button>
      ),
    },
    { key: 'email', header: 'Email' },
    {
      key: 'role',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('role')}>
          Role <SortIcon active={sortBy === 'role'} direction={sortDir} />
        </button>
      ),
      render: (row) => <Badge status={row.role} />,
    },
    {
      key: 'joiningDate',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('joiningDate')}>
          Joined <SortIcon active={sortBy === 'joiningDate'} direction={sortDir} />
        </button>
      ),
      render: (row) => formatDate(row.joiningDate),
    },
    {
      key: 'salary',
      header: (
        <button className="flex items-center gap-1" onClick={() => toggleSort('salary')}>
          Salary <SortIcon active={sortBy === 'salary'} direction={sortDir} />
        </button>
      ),
      render: (row) => formatCurrency(row.salary),
    },
    {
      key: 'actions',
      header: '',
      render: (row) => (
        <div className="flex items-center justify-end gap-1">
          <Link
            to={`/employees/${row.id}`}
            state={{ employee: row }}
            className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30"
            title="View"
          >
            <Eye className="h-4 w-4" />
          </Link>
          {canManage && (
            <>
              <Link
                to={`/employees/${row.id}/edit`}
                state={{ employee: row }}
                className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30"
                title="Edit"
              >
                <Pencil className="h-4 w-4" />
              </Link>
              <Link
                to={`/employees/${row.id}/role`}
                state={{ employee: row }}
                className="p-1.5 rounded-lg text-gray-400 hover:text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/30"
                title="Assign Role"
              >
                <ShieldQuestion className="h-4 w-4" />
              </Link>
              <button
                onClick={() => setDeleteTarget(row)}
                className="p-1.5 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30"
                title="Delete"
              >
                <Trash2 className="h-4 w-4" />
              </button>
            </>
          )}
        </div>
      ),
    },
  ]

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Employees</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Every staff record on file.</p>
        </div>
        {canManage && (
          <Link to="/employees/add">
            <Button icon={PlusCircle}>Add Employee</Button>
          </Link>
        )}
      </div>

      <div className="card p-4 flex flex-wrap items-end gap-3">
        <Input
          containerClassName="flex-1 min-w-[220px]"
          label="Search"
          placeholder="Employee code, name or email"
          value={searchInput}
          onChange={(e) => {
            setSearchInput(e.target.value)
            setPage(1)
          }}
        />
        <Select
          containerClassName="w-44"
          label="Role"
          value={roleFilter}
          onChange={(e) => {
            setRoleFilter(e.target.value)
            setPage(1)
          }}
          options={ROLE_FILTERS}
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
        {(searchInput || roleFilter !== 'all') && (
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
            employees.length === 0 ? 'No employee records yet.' : 'No employees match your search or filters.'
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
          Search runs against the backend's <code>/employees/search</code> endpoint (employee code, name and email),
          debounced as you type. Role filtering, sorting and pagination happen locally, since the backend doesn't
          support them.
        </p>
      </div>

      <Modal
        open={Boolean(deleteTarget)}
        onClose={() => setDeleteTarget(null)}
        title="Delete this employee record?"
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
            This will permanently remove <strong>{deleteTarget.fullName}</strong>'s employee record (
            {deleteTarget.employeeCode}). This does not delete their underlying user account, only the employee
            record. This cannot be undone.
          </p>
        )}
      </Modal>
    </div>
  )
}
