import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowRight, Briefcase, Info, PlusCircle, ShieldCheck, Users } from 'lucide-react'
import { getAllEmployees } from '../../api/employeeApi'
import { useAuth } from '../../hooks/useAuth'
import { formatCurrency } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Badge from '../../components/common/Badge'
import Button from '../../components/common/Button'

function StatCard({ icon: Icon, label, value, hint, accent = 'primary' }) {
  const accents = {
    primary: 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300',
    green: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300',
    amber: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
    blue: 'bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
  }
  return (
    <div className="card p-5 flex items-start gap-4">
      <div className={`rounded-xl p-3 ${accents[accent]}`}>
        <Icon className="h-5 w-5" />
      </div>
      <div className="min-w-0">
        <p className="text-sm text-gray-500 dark:text-gray-400">{label}</p>
        <p className="text-2xl font-semibold truncate">{value}</p>
        {hint && <p className="text-xs text-gray-400 mt-0.5">{hint}</p>}
      </div>
    </div>
  )
}

// GET /api/employees (ADMIN, MANAGER only) has no backend aggregation
// endpoint, so every stat here -- headcount, role breakdown, payroll total --
// is derived client-side from the one full list, same approach as
// CustomerDashboard.
export default function EmployeeDashboard() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['employees', 'all'],
    queryFn: getAllEmployees,
  })
  const { user } = useAuth()
  const canCreate = user?.role === 'ADMIN'

  const stats = useMemo(() => {
    const employees = data || []
    const byRole = employees.reduce((acc, e) => {
      acc[e.role] = (acc[e.role] || 0) + 1
      return acc
    }, {})
    const totalSalary = employees.reduce((sum, e) => sum + (Number(e.salary) || 0), 0)
    // Higher id generally means a more recently created record (auto-
    // increment primary key) -- EmployeeResponse has no createdAt to sort by.
    const recent = [...employees].sort((a, b) => b.id - a.id).slice(0, 5)

    return { total: employees.length, byRole, totalSalary, recent }
  }, [data])

  if (isLoading) return <Loader label="Loading employee overview..." />
  if (error) {
    return <ErrorMessage message={error.message || 'Could not load employee data.'} onRetry={refetch} />
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Employee Overview</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Staff headcount, roles and payroll at a glance.</p>
        </div>
        <div className="flex items-center gap-2">
          {canCreate && (
            <Link to="/employees/add">
              <Button icon={PlusCircle}>Add Employee</Button>
            </Link>
          )}
          <Link to="/employees/list">
            <Button icon={ArrowRight} variant="secondary">
              View all employees
            </Button>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard icon={Users} label="Total Employees" value={stats.total} hint="Records on file" accent="primary" />
        <StatCard
          icon={ShieldCheck}
          label="Admins & Managers"
          value={(stats.byRole.ADMIN || 0) + (stats.byRole.MANAGER || 0)}
          hint="Combined ADMIN + MANAGER"
          accent="blue"
        />
        <StatCard
          icon={Briefcase}
          label="Total Monthly Salary"
          value={formatCurrency(stats.totalSalary)}
          hint="Sum across all employees"
          accent="green"
        />
      </div>

      <div className="card p-5">
        <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200 mb-4">Roles Breakdown</h2>
        {stats.total === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">No employee records yet.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {Object.entries(stats.byRole).map(([role, count]) => (
              <div
                key={role}
                className="flex items-center gap-2 rounded-lg border border-gray-200 dark:border-gray-800 px-3 py-1.5"
              >
                <Badge status={role} />
                <span className="text-sm font-medium">{count}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="card p-5">
        <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200 mb-4">Most Recently Added</h2>
        {stats.recent.length === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">No employee records yet.</p>
        ) : (
          <ul className="divide-y divide-gray-100 dark:divide-gray-800">
            {stats.recent.map((e) => (
              <li key={e.id}>
                <Link
                  to={`/employees/${e.id}`}
                  state={{ employee: e }}
                  className="flex items-center justify-between py-3 text-sm hover:bg-gray-50 dark:hover:bg-gray-900/60 -mx-2 px-2 rounded-lg"
                >
                  <div>
                    <p className="font-medium">{e.fullName}</p>
                    <p className="text-gray-500 dark:text-gray-400">{e.employeeCode}</p>
                  </div>
                  <Badge status={e.role} />
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          These figures come from one full fetch of every employee record (the backend has no summary/aggregation
          endpoint), so they're only as fresh as the last time this page loaded. Salary is assumed to already be a
          monthly figure -- the backend stores a single BigDecimal with no period field.
        </p>
      </div>
    </div>
  )
}
