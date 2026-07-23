import React from 'react'
import { useAuth } from '../../hooks/useAuth'
import { useQuery } from '@tanstack/react-query'
import { getPurchasesByCustomer, getPurchasesBySalesperson } from '../../api/purchaseApi'
import { Link } from 'react-router-dom'
import { ShoppingCart, Plus, List } from 'lucide-react'
import Button from '../../components/common/Button'
import Table from '../../components/common/Table'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDate } from '../../utils/formatters'

export default function PurchaseDashboard() {
  const { user } = useAuth()
  const isAdminOrManager = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const isSales = user?.role === 'SALESPERSON'
  const isCustomer = user?.role === 'CUSTOMER'

  const { data: salespersonPurchases, isLoading: loadingSales } = useQuery({
    queryKey: ['purchases', 'salesperson', user?.userId],
    queryFn: () => getPurchasesBySalesperson(user?.userId),
    enabled: isSales && !!user?.userId
  })

  const { data: customerPurchases, isLoading: loadingCustomer } = useQuery({
    queryKey: ['purchases', 'customer', user?.userId],
    queryFn: () => getPurchasesByCustomer(user?.userId),
    enabled: isCustomer && !!user?.userId
  })

  const purchases = isSales ? (salespersonPurchases || []) : isCustomer ? (customerPurchases || []) : []
  const isLoading = isSales ? loadingSales : isCustomer ? loadingCustomer : false

  const booked = purchases.filter(p => p.purchaseStatus === 'BOOKED').length
  const completed = purchases.filter(p => p.purchaseStatus === 'COMPLETED').length
  const cancelled = purchases.filter(p => p.purchaseStatus === 'CANCELLED').length

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/purchases/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'customerName', header: 'Customer' },
    { key: 'vehicleName', header: 'Vehicle' },
    { key: 'purchaseDate', header: 'Date', render: (row) => formatDate(row.purchaseDate) },
    { key: 'sellingPrice', header: 'Price', render: (row) => formatCurrency(row.sellingPrice) },
    { key: 'purchaseStatus', header: 'Status', render: (row) => <Badge status={row.purchaseStatus} /> }
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Purchases</h1>
          <p className="text-sm text-gray-500">Manage and track vehicle purchases.</p>
        </div>
        <div className="flex gap-2">
          {isAdminOrManager && (
            <Link to="/purchases/history">
              <Button variant="secondary" icon={List}>History</Button>
            </Link>
          )}
          <Link to="/purchases/list">
            <Button variant="secondary" icon={List}>View List</Button>
          </Link>
          <Link to="/purchases/add">
            <Button icon={Plus}>Add Purchase</Button>
          </Link>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Booked</span>
          <span className="text-2xl font-bold">{isAdminOrManager ? '--' : booked}</span>
        </div>
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Completed</span>
          <span className="text-2xl font-bold">{isAdminOrManager ? '--' : completed}</span>
        </div>
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Cancelled</span>
          <span className="text-2xl font-bold">{isAdminOrManager ? '--' : cancelled}</span>
        </div>
      </div>

      <div className="card p-4">
        <h2 className="text-lg font-semibold mb-4">Recent Purchases</h2>
        {isAdminOrManager ? (
          <div className="text-center py-10 text-gray-500 text-sm">
            <ShoppingCart className="h-8 w-8 mx-auto mb-2 opacity-50" />
            <p>List-all purchases is not available. Use the list view or history to search by vehicle or salesperson.</p>
          </div>
        ) : (
          <Table 
            columns={columns} 
            data={purchases.slice(0, 5)} 
            isLoading={isLoading} 
            emptyLabel="No recent purchases found." 
          />
        )}
      </div>
    </div>
  )
}
