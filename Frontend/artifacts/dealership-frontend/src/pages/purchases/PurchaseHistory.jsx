import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getPurchasesByVehicle, getPurchasesBySalesperson } from '../../api/purchaseApi'
import { Link } from 'react-router-dom'
import Table from '../../components/common/Table'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Badge from '../../components/common/Badge'
import { formatCurrency, formatDate } from '../../utils/formatters'

export default function PurchaseHistory() {
  const [searchType, setSearchType] = useState('vehicle')
  const [searchId, setSearchId] = useState('')

  const { data: purchases, isLoading } = useQuery({
    queryKey: ['purchases', searchType, searchId],
    queryFn: () => searchType === 'vehicle' ? getPurchasesByVehicle(searchId) : getPurchasesBySalesperson(searchId),
    enabled: !!searchId
  })

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
        <h1 className="text-xl font-semibold">Purchase History</h1>
        <p className="text-sm text-gray-500">Search purchases by vehicle or salesperson.</p>
      </div>

      <div className="card p-4 flex gap-3 items-end">
        <Select
          label="Search By"
          value={searchType}
          onChange={(e) => { setSearchType(e.target.value); setSearchId(''); }}
          options={[{ value: 'vehicle', label: 'Vehicle ID' }, { value: 'salesperson', label: 'Salesperson ID' }]}
        />
        <Input
          label="ID"
          type="number"
          value={searchId}
          onChange={(e) => setSearchId(e.target.value)}
          placeholder={`Enter ${searchType} ID`}
        />
      </div>

      <div className="card p-4">
        {!searchId ? (
          <div className="text-center py-10 text-gray-500">Enter an ID to search.</div>
        ) : (
          <Table columns={columns} data={purchases || []} isLoading={isLoading} />
        )}
      </div>
    </div>
  )
}
