import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllVehicles } from '../../api/vehicleApi'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts'
import Table from '../../components/common/Table'
import Badge from '../../components/common/Badge'
import { getStockLevel } from '../../utils/formatters'

export default function InventoryReport() {
  const { data: vehicles, isLoading: loadingV } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles
  })

  const { pieData, tableData } = useMemo(() => {
    if (!vehicles) return { pieData: [], tableData: [] }
    let inStock = 0, lowStock = 0, outStock = 0
    vehicles.forEach(v => {
      const level = getStockLevel(v.quantity)
      if (level === 'IN_STOCK') inStock++
      else if (level === 'LOW_STOCK') lowStock++
      else outStock++
    })
    return {
      pieData: [
        { name: 'In Stock', value: inStock, color: '#22c55e' },
        { name: 'Low Stock', value: lowStock, color: '#f59e0b' },
        { name: 'Out of Stock', value: outStock, color: '#ef4444' }
      ].filter(x => x.value > 0),
      tableData: vehicles
    }
  }, [vehicles])

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'brand', header: 'Brand' },
    { key: 'model', header: 'Model' },
    { key: 'quantity', header: 'Quantity' },
    { key: 'status', header: 'Stock Level', render: (row) => <Badge status={getStockLevel(row.quantity)} /> }
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Inventory Report</h1>
        <p className="text-sm text-gray-500">Comprehensive view of vehicle stock levels.</p>
      </div>

      <div className="card p-5 max-w-lg">
        <h2 className="text-lg font-semibold mb-4">Stock Breakdown</h2>
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                {pieData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="card p-4">
        <h2 className="text-lg font-semibold mb-4">Full Inventory Status</h2>
        <Table columns={columns} data={tableData} isLoading={loadingV} />
      </div>
    </div>
  )
}
