import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllTestDrives, getTestDrivesByCustomer } from '../../api/testDriveApi'
import { useAuth } from '../../hooks/useAuth'
import { Link } from 'react-router-dom'
import { Calendar, Plus, List, CalendarIcon } from 'lucide-react'
import Button from '../../components/common/Button'
import Table from '../../components/common/Table'
import Badge from '../../components/common/Badge'
import { formatDateTime } from '../../utils/formatters'

export default function TestDriveDashboard() {
  const { user } = useAuth()
  const isAdminOrManager = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const isCustomer = user?.role === 'CUSTOMER'

  const { data: allDrives, isLoading: loadingAll } = useQuery({
    queryKey: ['testDrives', 'all'],
    queryFn: getAllTestDrives,
    enabled: isAdminOrManager
  })

  const { data: customerDrives, isLoading: loadingCustomer } = useQuery({
    queryKey: ['testDrives', 'customer', user?.userId],
    queryFn: () => getTestDrivesByCustomer(user?.userId),
    enabled: isCustomer && !!user?.userId
  })

  const drives = isAdminOrManager ? (allDrives || []) : (customerDrives || [])
  const isLoading = isAdminOrManager ? loadingAll : (isCustomer ? loadingCustomer : false)

  const requested = drives.filter(d => d.status === 'REQUESTED').length
  const approved = drives.filter(d => d.status === 'APPROVED').length
  const completed = drives.filter(d => d.status === 'COMPLETED').length

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/test-drives/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'customerName', header: 'Customer' },
    { key: 'vehicleName', header: 'Vehicle' },
    { key: 'scheduledDate', header: 'Scheduled For', render: (row) => formatDateTime(row.scheduledDate) },
    { key: 'status', header: 'Status', render: (row) => <Badge status={row.status} /> }
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Test Drives</h1>
          <p className="text-sm text-gray-500">Manage and track vehicle test drives.</p>
        </div>
        <div className="flex gap-2">
          {isAdminOrManager && (
            <>
              <Link to="/test-drives/calendar">
                <Button variant="secondary" icon={CalendarIcon}>Calendar</Button>
              </Link>
              <Link to="/test-drives/list">
                <Button variant="secondary" icon={List}>View List</Button>
              </Link>
            </>
          )}
          <Link to="/test-drives/book">
            <Button icon={Plus}>Book Test Drive</Button>
          </Link>
        </div>
      </div>

      {user?.role === 'SALESPERSON' && (
        <div className="text-sm text-blue-600 bg-blue-50 p-4 rounded-lg border border-blue-200">
          <p>Salespersons can book test drives for customers, but cannot view a global list of all test drives.</p>
          <p className="mt-1">To view your own personal test drives (if you are also a customer), you must have a linked customer profile.</p>
        </div>
      )}

      {(isAdminOrManager || isCustomer) && (
        <>
          <div className="grid gap-4 sm:grid-cols-3">
            <div className="stat-card">
              <span className="text-sm font-medium text-gray-500">Requested</span>
              <span className="text-2xl font-bold">{requested}</span>
            </div>
            <div className="stat-card">
              <span className="text-sm font-medium text-gray-500">Approved</span>
              <span className="text-2xl font-bold">{approved}</span>
            </div>
            <div className="stat-card">
              <span className="text-sm font-medium text-gray-500">Completed</span>
              <span className="text-2xl font-bold">{completed}</span>
            </div>
          </div>

          <div className="card p-4">
            <h2 className="text-lg font-semibold mb-4">Recent Bookings</h2>
            <Table 
              columns={columns} 
              data={drives.slice(0, 5)} 
              isLoading={isLoading} 
              emptyLabel="No recent test drives." 
            />
          </div>
        </>
      )}
    </div>
  )
}
