import React, { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllTestDrives } from '../../api/testDriveApi'
import { Link } from 'react-router-dom'
import Table from '../../components/common/Table'
import Select from '../../components/common/Select'
import Input from '../../components/common/Input'
import Badge from '../../components/common/Badge'
import { formatDateTime } from '../../utils/formatters'

export default function BookingList() {
  const [search, setSearch] = useState('')
  const [statusFilter, setStatusFilter] = useState('')

  const { data: drives, isLoading } = useQuery({
    queryKey: ['testDrives', 'all'],
    queryFn: getAllTestDrives
  })

  const filtered = useMemo(() => {
    return (drives || []).filter(d => {
      const matchSearch = !search || 
        String(d.id).includes(search) || 
        String(d.customerName || '').toLowerCase().includes(search.toLowerCase()) ||
        String(d.vehicleName || '').toLowerCase().includes(search.toLowerCase())
      const matchStatus = !statusFilter || d.status === statusFilter
      return matchSearch && matchStatus
    })
  }, [drives, search, statusFilter])

  const columns = [
    { key: 'id', header: 'ID', render: (row) => <Link to={`/test-drives/${row.id}`} className="text-primary-600 hover:underline">#{row.id}</Link> },
    { key: 'customerName', header: 'Customer' },
    { key: 'vehicleName', header: 'Vehicle' },
    { key: 'scheduledDate', header: 'Scheduled For', render: (row) => formatDateTime(row.scheduledDate) },
    { key: 'status', header: 'Status', render: (row) => <Badge status={row.status} /> }
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Test Drive Bookings</h1>
        <p className="text-sm text-gray-500">View all test drive appointments.</p>
      </div>

      <div className="card p-4 flex flex-wrap gap-3">
        <Input
          label="Search"
          placeholder="Customer or Vehicle"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          containerClassName="w-64"
        />
        <Select
          label="Status"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          options={['REQUESTED', 'APPROVED', 'COMPLETED', 'CANCELLED']}
        />
      </div>

      <div className="card p-4">
        <Table columns={columns} data={filtered} isLoading={isLoading} />
      </div>
    </div>
  )
}
