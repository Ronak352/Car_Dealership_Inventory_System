import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ArrowRight, Info, MapPinOff, PlusCircle, Users } from 'lucide-react'
import { getAllCustomers } from '../../api/customerApi'
import { useAuth } from '../../hooks/useAuth'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'

function StatCard({ icon: Icon, label, value, hint, accent = 'primary' }) {
  const accents = {
    primary: 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300',
    green: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300',
    amber: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
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

export default function CustomerDashboard() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['customers', 'all'],
    queryFn: getAllCustomers,
  })
  const { user } = useAuth()
  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'

  const stats = useMemo(() => {
    const customers = data || []
    const withAddress = customers.filter((c) => c.address && c.address.trim().length > 0)
    // Higher id generally means a more recently created profile (auto-
    // increment primary key) -- there's no createdAt on CustomerResponse to
    // sort by properly, so this is a reasonable proxy, not a real timestamp.
    const recent = [...customers].sort((a, b) => b.id - a.id).slice(0, 5)

    return {
      total: customers.length,
      withAddress: withAddress.length,
      withoutAddress: customers.length - withAddress.length,
      recent,
    }
  }, [data])

  if (isLoading) return <Loader label="Loading customer overview..." />
  if (error) {
    return <ErrorMessage message={error.message || 'Could not load customer data.'} onRetry={refetch} />
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Customer Overview</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Track customer profiles on file.</p>
        </div>
        <div className="flex items-center gap-2">
          {canManage && (
            <Link to="/customers/add">
              <Button icon={PlusCircle}>Add Customer</Button>
            </Link>
          )}
          <Link to="/customers/list">
            <Button icon={ArrowRight} variant="secondary">
              View all customers
            </Button>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard icon={Users} label="Total Customers" value={stats.total} hint="Profiles on file" accent="primary" />
        <StatCard
          icon={Users}
          label="Complete Profiles"
          value={stats.withAddress}
          hint="Have an address on file"
          accent="green"
        />
        <StatCard
          icon={MapPinOff}
          label="Missing Address"
          value={stats.withoutAddress}
          hint="Profile created without an address"
          accent="amber"
        />
      </div>

      <div className="card p-5">
        <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200 mb-4">Most Recently Added</h2>
        {stats.recent.length === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">No customer profiles yet.</p>
        ) : (
          <ul className="divide-y divide-gray-100 dark:divide-gray-800">
            {stats.recent.map((c) => (
              <li key={c.id}>
                <Link
                  to={`/customers/${c.id}`}
                  state={{ customer: c }}
                  className="flex items-center justify-between py-3 text-sm hover:bg-gray-50 dark:hover:bg-gray-900/60 -mx-2 px-2 rounded-lg"
                >
                  <div>
                    <p className="font-medium">{c.fullName}</p>
                    <p className="text-gray-500 dark:text-gray-400">{c.email}</p>
                  </div>
                  <span className="text-gray-400">{c.phone}</span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          City, state and pincode breakdowns aren't shown: the backend's customer API accepts those fields when a
          profile is created but never returns them on any response, so a location breakdown here would be
          inaccurate. Note also that this only counts customers who've completed a profile (address on file) --
          a registered customer account that hasn't done that yet won't appear here at all.
        </p>
      </div>
    </div>
  )
}
