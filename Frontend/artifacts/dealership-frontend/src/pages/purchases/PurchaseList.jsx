import React, { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useAuth } from '../../hooks/useAuth'
import { getPurchasesByVehicle, getPurchasesBySalesperson } from '../../api/purchaseApi'
import { getAllVehicles } from '../../api/vehicleApi'
import { Link, Navigate } from 'react-router-dom'
import Table from '../../components/common/Table'
import Select from '../../components/common/Select'
import Input from '../../components/common/Input'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDate } from '../../utils/formatters'

export default function PurchaseList() {
  const { user } = useAuth()
  const isAdminOrManager = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const isSales = user?.role === 'SALESPERSON'
  const isCustomer = user?.role === 'CUSTOMER'

  if (isCustomer) {
    return <Navigate to="/customers/profile" replace />
  }

  const [selectedVehicle, setSelectedVehicle] = useState('')
  const [salespersonId, setSalespersonId] = useState(isSales ? String(user.userId || '') : '')
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')

  const { data: vehicles } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
    enabled: isAdminOrManager
  })

  const { data: vehiclePurchases, isLoading: loadingVP } = useQuery({
    queryKey: ['purchases', 'vehicle', selectedVehicle],
    queryFn: () => getPurchasesByVehicle(selectedVehicle),
    enabled: isAdminOrManager && !!selectedVehicle
  })

  const { data: salesPurchases, isLoading: loadingSP } = useQuery({
    queryKey: ['purchases', 'salesperson', salespersonId],
    queryFn: () => getPurchasesBySalesperson(salespersonId),
    enabled: !!salespersonId
  })

  const rawPurchases = isAdminOrManager ? (selectedVehicle ? vehiclePurchases : salesPurchases) : salesPurchases
  const purchases = rawPurchases || []
  const isLoading = loadingVP || loadingSP

  const filtered = useMemo(() => {
    return purchases.filter(p => {
      const matchSearch = !search || 
        String(p.id).includes(search) || 
        String(p.customerName || '').toLowerCase().includes(search.toLowerCase())
      const matchStatus = !statusFilter || p.purchaseStatus === statusFilter
      return matchSearch && matchStatus
    })
  }, [purchases, search, statusFilter])

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
      <div>
        <h1 className="text-xl font-semibold">Purchase List</h1>
        <p className="text-sm text-gray-500">View and filter purchases.</p>
      </div>

      <div className="card p-4 flex flex-wrap items-end gap-3">
        {isAdminOrManager && (
          <Select
            label="Filter by Vehicle"
            containerClassName="w-64"
            value={selectedVehicle}
            onChange={(e) => {
              setSelectedVehicle(e.target.value)
              if (e.target.value) setSalespersonId('')
            }}
            options={(vehicles || []).map(v => ({ value: v.id, label: `${v.brand} ${v.model} (${v.id})` }))}
          />
        )}
        {isAdminOrManager && (
          <Input
            label="Or Salesperson ID"
            containerClassName="w-40"
            type="number"
            value={salespersonId}
            onChange={(e) => {
              setSalespersonId(e.target.value)
              if (e.target.value) setSelectedVehicle('')
            }}
          />
        )}
        <Input
          label="Search"
          placeholder="ID or Customer Name"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <Select
          label="Status"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          options={['BOOKED', 'COMPLETED', 'CANCELLED']}
        />
      </div>

      {isSales && (
        <div className="text-sm text-blue-600 bg-blue-50 p-3 rounded-lg border border-blue-200">
          Showing purchases assigned to your Salesperson ID ({salespersonId || user.userId}). If this is incorrect, update it here:
          <div className="mt-2">
            <Input 
              type="number" 
              containerClassName="w-48"
              value={salespersonId}
              onChange={(e) => setSalespersonId(e.target.value)}
            />
          </div>
        </div>
      )}

      <div className="card p-4">
        {(!selectedVehicle && !salespersonId && isAdminOrManager) ? (
          <div className="text-center py-10 text-gray-500">Please select a vehicle or enter a salesperson ID to view purchases.</div>
        ) : (
          <Table columns={columns} data={filtered} isLoading={isLoading} />
        )}
      </div>
    </div>
  )
}
