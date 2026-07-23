import React from 'react'
import { useAuth } from '../../hooks/useAuth'
import { useQuery } from '@tanstack/react-query'
import { getCustomerByUserId } from '../../api/customerApi'
import Loader from '../../components/common/Loader'
import { Info } from 'lucide-react'

export default function MyProfile() {
  const { user } = useAuth()
  const isCustomer = user?.role === 'CUSTOMER'

  const { data: customerData, isLoading: customerLoading } = useQuery({
    queryKey: ['customers', 'user', user?.userId],
    queryFn: () => getCustomerByUserId(user?.userId),
    enabled: isCustomer && !!user?.userId
  })

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h1 className="text-xl font-semibold">My Profile</h1>
        <p className="text-sm text-gray-500">View your account information.</p>
      </div>

      <div className="card p-5 space-y-4">
        <div>
          <span className="text-sm text-gray-500">Email</span>
          <p className="font-medium text-lg">{user?.email}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">System Role</span>
          <p className="font-medium text-lg">{user?.role}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">User ID</span>
          <p className="font-medium">{user?.userId}</p>
        </div>
      </div>

      {isCustomer && (
        <div className="card p-5 space-y-4">
          <h2 className="text-lg font-semibold border-b border-gray-200 dark:border-gray-800 pb-2">Customer Details</h2>
          {customerLoading ? (
            <Loader size="sm" />
          ) : customerData ? (
            <div className="grid grid-cols-2 gap-4">
              <div>
                <span className="text-sm text-gray-500">Full Name</span>
                <p className="font-medium">{customerData.fullName}</p>
              </div>
              <div>
                <span className="text-sm text-gray-500">Phone</span>
                <p className="font-medium">{customerData.phone}</p>
              </div>
              <div className="col-span-2">
                <span className="text-sm text-gray-500">Address</span>
                <p className="font-medium">{customerData.address}</p>
              </div>
            </div>
          ) : (
            <p className="text-gray-500 text-sm">No customer profile linked to this account.</p>
          )}
        </div>
      )}

      {!isCustomer && (
        <div className="flex items-start gap-3 rounded-xl border border-blue-200 bg-blue-50 dark:bg-blue-900/30 p-4 text-sm text-blue-800 dark:text-blue-200">
          <Info className="h-5 w-5 shrink-0 mt-0.5" />
          <p>You are logged in as an employee ({user?.role}). Employee records are managed by Administrators. The backend does not provide a self-service endpoint for employees to view or edit their own HR records.</p>
        </div>
      )}
    </div>
  )
}
