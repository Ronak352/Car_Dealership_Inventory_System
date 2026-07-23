import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getPaymentsByPurchase } from '../../api/paymentApi'
import { Link } from 'react-router-dom'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDateTime } from '../../utils/formatters'

export default function TransactionHistory() {
  const [purchaseId, setPurchaseId] = useState('')

  const { data: payments, isLoading } = useQuery({
    queryKey: ['payments', 'purchase', purchaseId],
    queryFn: () => getPaymentsByPurchase(purchaseId),
    enabled: !!purchaseId
  })

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/payments/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount) },
    { key: 'paymentMethod', header: 'Method' },
    { key: 'paymentStatus', header: 'Status', render: (row) => <Badge status={row.paymentStatus} /> },
    { key: 'transactionId', header: 'Txn ID' },
    { key: 'paymentDate', header: 'Date', render: (row) => formatDateTime(row.paymentDate) }
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Transaction History</h1>
        <p className="text-sm text-gray-500">Look up payments for a specific purchase.</p>
      </div>

      <div className="card p-4">
        <Input
          label="Purchase ID"
          type="number"
          value={purchaseId}
          onChange={(e) => setPurchaseId(e.target.value)}
          placeholder="Enter Purchase ID"
          containerClassName="max-w-xs"
        />
      </div>

      <div className="card p-4">
        {!purchaseId ? (
          <div className="text-center py-10 text-gray-500">Enter a Purchase ID to view its payments.</div>
        ) : (
          <Table columns={columns} data={payments || []} isLoading={isLoading} emptyLabel="No payments found for this purchase." />
        )}
      </div>
    </div>
  )
}
