import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllVehicles } from '../../api/vehicleApi'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import Table from '../../components/common/Table'
import Button from '../../components/common/Button'
import { Download } from 'lucide-react'
import { formatCurrency, getStockLevel } from '../../utils/formatters'

export default function VehicleReport() {
  const { data: vehicles, isLoading } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles
  })

  const { barData, pieData, tableData } = useMemo(() => {
    if (!vehicles) return { barData: [], pieData: [], tableData: [] }

    const modelCounts = {}
    let inStock = 0, lowStock = 0, outStock = 0

    vehicles.forEach(v => {
      modelCounts[v.model] = (modelCounts[v.model] || 0) + 1
      const stock = getStockLevel(v.quantity)
      if (stock === 'IN_STOCK') inStock++
      else if (stock === 'LOW_STOCK') lowStock++
      else outStock++
    })

    const bar = Object.entries(modelCounts).map(([name, count]) => ({ name, count }))
    const pie = [
      { name: 'In Stock', value: inStock, color: '#22c55e' },
      { name: 'Low Stock', value: lowStock, color: '#f59e0b' },
      { name: 'Out of Stock', value: outStock, color: '#ef4444' }
    ].filter(x => x.value > 0)

    return { barData: bar, pieData: pie, tableData: vehicles }
  }, [vehicles])

  const exportCSV = () => {
    const header = ['ID,Brand,Model,Price,Quantity']
    const rows = tableData.map(v => `${v.id},${v.brand},${v.model},${v.price},${v.quantity}`)
    const blob = new Blob([header.concat(rows).join('\n')], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'vehicle_report.csv'
    a.click()
    URL.revokeObjectURL(url)
  }

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'brand', header: 'Brand' },
    { key: 'model', header: 'Model' },
    { key: 'price', header: 'Price', render: (row) => formatCurrency(row.price) },
    { key: 'quantity', header: 'Quantity' }
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Vehicle Report</h1>
          <p className="text-sm text-gray-500">Analytics and distribution of the current fleet.</p>
        </div>
        <Button icon={Download} onClick={exportCSV} disabled={isLoading || !tableData.length}>Export CSV</Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card p-5">
          <h2 className="text-lg font-semibold mb-4">Vehicles by Model</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={barData}>
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip cursor={{ fill: 'transparent' }} />
                <Bar dataKey="count" fill="#2563eb" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card p-5">
          <h2 className="text-lg font-semibold mb-4">Stock Distribution</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={pieData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
                  {pieData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="card p-4">
        <Table columns={columns} data={tableData} isLoading={isLoading} />
      </div>
    </div>
  )
}
