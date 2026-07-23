import React, { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllPayments } from '../../api/paymentApi'
import { Link } from 'react-router-dom'
import Table from '../../components/common/Table'
import Select from '../../components/common/Select'
import Input from '../../components/common/Input'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDateTime } from '../../utils/formatters'

export default function PaymentList() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [methodFilter, setMethodFilter] = useState('')

  const { data: payments, isLoading } = useQuery({
    queryKey: ['payments', 'all'],
    queryFn: getAllPayments
  })

  const filtered = useMemo(() => {
    return (payments || []).filter(p => {
      const matchSearch = !search || String(p.id).includes(search) || String(p.transactionId || '').includes(search) || String(p.purchaseId).includes(search)
      const matchStatus = !statusFilter || p.paymentStatus === statusFilter
      const matchMethod = !methodFilter || p.paymentMethod === methodFilter
      return matchSearch && matchStatus && matchMethod
    })
  }, [payments, search, statusFilter, methodFilter])

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/payments/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'purchaseId', header: 'Purchase ID', render: (row) => <Link to={`/purchases/${row.purchaseId}`} className="text-primary-600 hover:underline">#{row.purchaseId}</Link> },
    { key: 'amount', header: 'Amount', render: (row) => formatCurrency(row.amount) },
    { key: 'paymentMethod', header: 'Method' },
    { key: 'transactionId', header: 'Txn ID' },
    { key: 'paymentStatus', header: 'Status', render: (row) => <Badge status={row.paymentStatus} /> },
    { key: 'paymentDate', header: 'Date', render: (row) => formatDateTime(row.paymentDate) }
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">All Payments</h1>
        <p className="text-sm text-gray-500">View and filter all payment transactions.</p>
      </div>

      <div className="card p-4 flex flex-wrap gap-3">
        <Input
          label="Search"
          placeholder="Payment ID, Purchase ID or Txn ID"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <Select
          label="Status"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          options={['PENDING', 'COMPLETED', 'FAILED', 'REFUNDED']}
        />
        <Select
          label="Method"
          value={methodFilter}
          onChange={(e) => setMethodFilter(e.target.value)}
          options={['CASH', 'CARD', 'UPI', 'LOAN']}
        />
      </div>

      <div className="card p-4">
        <Table columns={columns} data={filtered} isLoading={isLoading} />
      </div>
    </div>
  )
}
