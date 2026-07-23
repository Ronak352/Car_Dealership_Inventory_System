import React from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getPurchaseById } from '../../api/purchaseApi'
import { getPaymentsByPurchase } from '../../api/paymentApi'
import Badge from '../../components/common/Badge'
import Table from '../../components/common/Table'
import Button from '../../components/common/Button'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import { formatCurrency, formatDate, formatDateTime } from '../../utils/formatters'
import { Plus } from 'lucide-react'

export default function PurchaseDetails() {
  const { id } = useParams()
  
  const { data: purchase, isLoading: loadingPurchase, error: errorPurchase } = useQuery({
    queryKey: ['purchases', id],
    queryFn: () => getPurchaseById(id)
  })

  const { data: payments, isLoading: loadingPayments } = useQuery({
    queryKey: ['payments', 'purchase', id],
    queryFn: () => getPaymentsByPurchase(id)
  })

  if (loadingPurchase) return <Loader />
  if (errorPurchase) return <ErrorMessage message="Failed to load purchase details" />
  if (!purchase) return <ErrorMessage message="Purchase not found" />

  const paymentColumns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/payments/${row.id}`} className="text-primary-600">#{row.id}</Link> },
    { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount) },
    { key: 'paymentMethod', header: 'Method' },
    { key: 'paymentStatus', header: 'Status', render: (row) => <Badge status={row.paymentStatus} /> },
    { key: 'transactionId', header: 'Txn ID' },
    { key: 'paymentDate', header: 'Date', render: (row) => formatDateTime(row.paymentDate) }
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Purchase #{purchase.id}</h1>
          <p className="text-sm text-gray-500">Details for {purchase.vehicleName}</p>
        </div>
        <Badge status={purchase.purchaseStatus} />
      </div>

      <div className="card p-5 grid grid-cols-2 gap-y-4 gap-x-8">
        <div>
          <span className="text-sm text-gray-500">Customer</span>
          <p className="font-medium">{purchase.customerName}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Vehicle</span>
          <p className="font-medium">{purchase.vehicleName}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Purchase Date</span>
          <p className="font-medium">{formatDate(purchase.purchaseDate)}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Delivery Date</span>
          <p className="font-medium">{formatDate(purchase.deliveryDate)}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Selling Price</span>
          <p className="font-medium">{formatCurrency(purchase.sellingPrice)}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Payment Method</span>
          <p className="font-medium">{purchase.paymentMethod}</p>
        </div>
      </div>

      <div>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-lg font-semibold">Payments</h2>
          <Link to={`/payments/add?purchaseId=${purchase.id}`}>
            <Button size="sm" icon={Plus}>Add Payment</Button>
          </Link>
        </div>
        <Table columns={paymentColumns} data={payments || []} isLoading={loadingPayments} emptyLabel="No payments recorded for this purchase." />
      </div>
    </div>
  )
}
