import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { History, Info, Pencil, PlusCircle, UserCircle } from 'lucide-react'
import { getCustomerByUserId } from '../../api/customerApi'
import { useAuth } from '../../hooks/useAuth'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'

// "My Profile" -- the signed-in user's own customer record, looked up via
// GET /api/customers/user/{userId} (allowed for ADMIN, MANAGER, SALESPERSON,
// CUSTOMER; meaningful mainly for CUSTOMER-role users, since that's who
// actually has a customer profile in this system).
//
// A registered user with role CUSTOMER doesn't automatically get a customer
// profile -- that's a separate step (POST /api/customers/{userId}) -- so a
// fresh account will 404 here until they complete it via AddCustomer.
export default function CustomerProfile() {
  const { user } = useAuth()

  const { data: customer, isLoading, error, refetch } = useQuery({
    queryKey: ['customers', 'byUser', user?.userId],
    queryFn: () => getCustomerByUserId(user.userId),
    enabled: Boolean(user?.userId),
    retry: false,
  })

  if (isLoading) return <Loader label="Loading your profile..." />

  if (error && error.status === 404) {
    return (
      <div className="max-w-xl space-y-4">
        <div>
          <h1 className="text-xl font-semibold">My Profile</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">You haven't completed your customer profile yet.</p>
        </div>
        <div className="card p-8 flex flex-col items-center text-center gap-3">
          <UserCircle className="h-10 w-10 text-gray-300" />
          <p className="text-sm text-gray-500 dark:text-gray-400 max-w-sm">
            Add your address so the dealership can complete purchases and deliveries for you.
          </p>
          <Link to="/customers/add">
            <Button icon={PlusCircle}>Complete Your Profile</Button>
          </Link>
        </div>
      </div>
    )
  }

  if (error) {
    return <ErrorMessage message={error.message || 'Could not load your profile.'} onRetry={refetch} />
  }

  return (
    <div className="max-w-xl space-y-4">
      <div>
        <h1 className="text-xl font-semibold">My Profile</h1>
        <p className="text-sm text-gray-500 dark:text-gray-400">Your customer details on file.</p>
      </div>

      <div className="card p-6">
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          <div>
            <dt className="text-xs uppercase tracking-wide text-gray-400">Full Name</dt>
            <dd className="text-sm font-medium mt-0.5">{customer.fullName}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase tracking-wide text-gray-400">Email</dt>
            <dd className="text-sm font-medium mt-0.5">{customer.email}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase tracking-wide text-gray-400">Phone</dt>
            <dd className="text-sm font-medium mt-0.5">{customer.phone || '—'}</dd>
          </div>
          <div>
            <dt className="text-xs uppercase tracking-wide text-gray-400">Address</dt>
            <dd className="text-sm font-medium mt-0.5">{customer.address || '—'}</dd>
          </div>
        </dl>

        <div className="flex flex-wrap justify-end gap-3 pt-6 mt-6 border-t border-gray-100 dark:border-gray-800">
          <Link to={`/customers/${customer.id}/purchases`} state={{ customer }}>
            <Button variant="secondary" icon={History}>
              My Purchase History
            </Button>
          </Link>
          <Link to={`/customers/${customer.id}/edit`} state={{ customer }}>
            <Button variant="secondary" icon={Pencil}>
              Edit
            </Button>
          </Link>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>City, state and pincode aren't shown: the backend never returns them on any customer response, even though they were saved when this profile was created.</p>
      </div>
    </div>
  )
}
