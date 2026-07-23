import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Info, ShieldAlert, UserCircle } from 'lucide-react'
import { getAllEmployees } from '../../api/employeeApi'
import { useAuth } from '../../hooks/useAuth'
import { formatCurrency, formatDate } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Badge from '../../components/common/Badge'

// "My employee profile" for the signed-in user, if they have one.
//
// IMPORTANT backend limitation: unlike the customer module (which has
// GET /api/customers/user/{userId} for exactly this self-lookup),
// EmployeeController has no equivalent -- every read endpoint (GET /{id},
// GET / list, GET /search) is restricted to hasAnyRole('ADMIN','MANAGER').
// A SALESPERSON employee has no backend-permitted way to view their own
// employee record at all, so this page can't fetch anything for them and
// says so plainly rather than silently failing or faking data.
//
// For an ADMIN/MANAGER (who CAN call GET /), this page fetches the full
// list once and finds their own record by matching EmployeeResponse.userId
// against the signed-in user's userId from the JWT session.
export default function EmployeeProfile() {
  const { user } = useAuth()
  const canQueryBackend = user?.role === 'ADMIN' || user?.role === 'MANAGER'

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['employees', 'all'],
    queryFn: getAllEmployees,
    enabled: canQueryBackend,
  })

  const myRecord = useMemo(() => {
    if (!data) return null
    return data.find((e) => e.userId === user?.userId) || null
  }, [data, user])

  if (!canQueryBackend) {
    return (
      <div className="max-w-lg space-y-4">
        <div className="flex items-center gap-3">
          <UserCircle className="h-6 w-6 text-gray-400" />
          <h1 className="text-xl font-semibold">My Employee Profile</h1>
        </div>
        <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
          <ShieldAlert className="h-5 w-5 shrink-0 mt-0.5" />
          <p>
            This isn't available for your role yet. Every employee read endpoint on the backend
            (<code>GET /employees/{'{id}'}</code>, the list, and search) is restricted to ADMIN and MANAGER only --
            there's no self-lookup endpoint for a SALESPERSON to view their own record, the way customers can view
            their own profile. This is a backend limitation, not something this page can work around.
          </p>
        </div>
      </div>
    )
  }

  if (isLoading) return <Loader label="Loading your profile..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load your profile.'} onRetry={refetch} />

  if (!myRecord) {
    return (
      <div className="max-w-lg space-y-4">
        <div className="flex items-center gap-3">
          <UserCircle className="h-6 w-6 text-gray-400" />
          <h1 className="text-xl font-semibold">My Employee Profile</h1>
        </div>
        <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
          <Info className="h-5 w-5 shrink-0 mt-0.5" />
          <p>No employee record is linked to your account yet.</p>
        </div>
      </div>
    )
  }

  const fields = [
    { label: 'Employee Code', value: myRecord.employeeCode },
    { label: 'Full Name', value: myRecord.fullName },
    { label: 'Email', value: myRecord.email },
    { label: 'Phone', value: myRecord.phone || '—' },
    { label: 'Joining Date', value: formatDate(myRecord.joiningDate) },
    { label: 'Salary', value: formatCurrency(myRecord.salary) },
  ]

  return (
    <div className="max-w-2xl space-y-4">
      <div className="flex items-center gap-3">
        <UserCircle className="h-6 w-6 text-gray-400" />
        <div>
          <h1 className="text-xl font-semibold">My Employee Profile</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{myRecord.email}</p>
        </div>
        <Badge status={myRecord.role} />
      </div>

      <div className="card p-6">
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          {fields.map((f) => (
            <div key={f.label}>
              <dt className="text-xs uppercase tracking-wide text-gray-400">{f.label}</dt>
              <dd className="text-sm font-medium mt-0.5">{f.value}</dd>
            </div>
          ))}
        </dl>
        <div className="pt-4 mt-4 border-t border-gray-100 dark:border-gray-800">
          <Link to={`/employees/${myRecord.id}`} state={{ employee: myRecord }} className="text-sm text-primary-600 hover:underline">
            View full record
          </Link>
        </div>
      </div>
    </div>
  )
}
