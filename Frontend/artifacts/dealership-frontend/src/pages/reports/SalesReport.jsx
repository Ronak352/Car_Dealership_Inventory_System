import React from 'react'
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts'
import { Info } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { getAllVehicles } from '../../api/vehicleApi'

const placeholderData = [
  { name: 'Jan', sales: 4000, bookings: 2400 },
  { name: 'Feb', sales: 3000, bookings: 1398 },
  { name: 'Mar', sales: 2000, bookings: 9800 },
  { name: 'Apr', sales: 2780, bookings: 3908 },
  { name: 'May', sales: 1890, bookings: 4800 },
  { name: 'Jun', sales: 2390, bookings: 3800 },
  { name: 'Jul', sales: 3490, bookings: 4300 },
]

export default function SalesReport() {
  const { data: vehicles } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles
  })

  const priceData = React.useMemo(() => {
    if (!vehicles) return []
    const ranges = { '< $20k': 0, '$20k-$40k': 0, '$40k-$60k': 0, '> $60k': 0 }
    vehicles.forEach(v => {
      if (v.price < 20000) ranges['< $20k']++
      else if (v.price < 40000) ranges['$20k-$40k']++
      else if (v.price < 60000) ranges['$40k-$60k']++
      else ranges['> $60k']++
    })
    return Object.entries(ranges).map(([name, count]) => ({ name, count }))
  }, [vehicles])

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Sales Analytics</h1>
        <p className="text-sm text-gray-500">Revenue, bookings, and pricing trends.</p>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-blue-200 bg-blue-50 dark:bg-blue-900/30 p-4 text-sm text-blue-800 dark:text-blue-200">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>Full sales analytics require a list-all-purchases endpoint which is not available in the current backend version. Showing vehicle pricing metrics instead, along with a placeholder revenue chart.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card p-5">
          <h2 className="text-lg font-semibold mb-4">Revenue & Bookings (Simulated)</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={placeholderData}>
                <defs>
                  <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2563eb" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#2563eb" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Area type="monotone" dataKey="sales" stroke="#2563eb" fillOpacity={1} fill="url(#colorSales)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card p-5">
          <h2 className="text-lg font-semibold mb-4">Fleet Pricing Distribution</h2>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={priceData}>
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip cursor={{ fill: 'transparent' }} />
                <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  )
}
