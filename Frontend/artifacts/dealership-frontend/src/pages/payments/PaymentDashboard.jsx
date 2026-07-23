import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllPayments } from '../../api/paymentApi'
import { Link } from 'react-router-dom'
import { CreditCard, Plus, List } from 'lucide-react'
import Button from '../../components/common/Button'
import Table from '../../components/common/Table'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDateTime } from '../../utils/formatters'
import { useAuth } from '../../hooks/useAuth'

export default function PaymentDashboard() {
  const { user } = useAuth()
  if (user?.role !== 'ADMIN') {
    return <div className="p-10 text-center">Only ADMIN can view the Payment Dashboard.</div>
  }

  const { data: payments, isLoading } = useQuery({
    queryKey: ['payments', 'all'],
    queryFn: getAllPayments
  })

  const p = payments || []
  const totalAmount = p.reduce((sum, item) => sum + Number(item.amount || 0), 0)
  const pending = p.filter(x => x.paymentStatus === 'PENDING').length
  const completed = p.filter(x => x.paymentStatus === 'COMPLETED').length
  const failed = p.filter(x => x.paymentStatus === 'FAILED').length

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/payments/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'purchaseId', header: 'Purchase', render: (row) => <Link to={`/purchases/${row.purchaseId}`} className="text-primary-600">#{row.purchaseId}</Link> },
    { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount) },
    { key: 'paymentMethod', header: 'Method' },
    { key: 'paymentStatus', header: 'Status', render: (row) => <Badge status={row.paymentStatus} /> },
    { key: 'paymentDate', header: 'Date', render: (row) => formatDateTime(row.paymentDate) }
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Payments</h1>
          <p className="text-sm text-gray-500">Overview of all payment transactions.</p>
        </div>
        <div className="flex gap-2">
          <Link to="/payments/list">
            <Button variant="secondary" icon={List}>All Payments</Button>
          </Link>
          <Link to="/payments/add">
            <Button icon={Plus}>Add Payment</Button>
          </Link>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-4">
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Total Volume</span>
          <span className="text-2xl font-bold">{formatCurrency(totalAmount)}</span>
        </div>
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Pending</span>
          <span className="text-2xl font-bold">{pending}</span>
        </div>
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Completed</span>
          <span className="text-2xl font-bold">{completed}</span>
        </div>
        <div className="stat-card">
          <span className="text-sm font-medium text-gray-500">Failed</span>
          <span className="text-2xl font-bold">{failed}</span>
        </div>
      </div>

      <div className="card p-4">
        <h2 className="text-lg font-semibold mb-4">Recent Payments</h2>
        <Table columns={columns} data={p.slice(0, 10)} isLoading={isLoading} emptyLabel="No payments found." />
      </div>
    </div>
  )
}
