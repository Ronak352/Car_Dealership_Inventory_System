import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { Car, CheckCircle2, Layers, IndianRupee, Info, ArrowRight, Plus } from 'lucide-react'
import { getAllVehicles, getAvailableVehicles } from '../../api/vehicleApi'
import { useAuth } from '../../hooks/useAuth'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'
import { formatCurrency } from '../../utils/formatters'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts'

function StatCard({ icon: Icon, label, value, hint, accent = 'primary' }) {
  const accents = {
    primary: 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300',
    green: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300',
    amber: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
    blue: 'bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
  }
  return (
    <div className="card p-5 flex items-start gap-4">
      <div className={`rounded-xl p-3 ${accents[accent]}`}>
        <Icon className="h-5 w-5" />
      </div>
      <div className="min-w-0">
        <p className="text-sm text-gray-500 dark:text-gray-400">{label}</p>
        <p className="text-2xl font-semibold truncate">{value}</p>
        {hint && <p className="text-xs text-gray-400 mt-0.5">{hint}</p>}
      </div>
    </div>
  )
}

export default function VehicleDashboard() {
  const { user } = useAuth()
  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'

  const allVehiclesQuery = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
  })

  const availableVehiclesQuery = useQuery({
    queryKey: ['vehicles', 'available'],
    queryFn: getAvailableVehicles,
  })

  const stats = useMemo(() => {
    const all = allVehiclesQuery.data || []
    const available = availableVehiclesQuery.data || []

    const totalUnits = all.reduce((sum, v) => sum + (Number(v.quantity) || 0), 0)
    const totalValue = all.reduce((sum, v) => sum + (Number(v.price) || 0) * (Number(v.quantity) || 0), 0)
    const avgPrice = all.length ? all.reduce((sum, v) => sum + (Number(v.price) || 0), 0) / all.length : 0

    const brandCounts = all.reduce((acc, v) => {
      const brand = v.brand || 'Unknown'
      acc[brand] = (acc[brand] || 0) + 1
      return acc
    }, {})
    const topBrands = Object.entries(brandCounts)
      .map(([brand, count]) => ({ brand, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 8)

    return {
      totalListings: all.length,
      availableListings: available.length,
      totalUnits,
      totalValue,
      avgPrice,
      topBrands,
    }
  }, [allVehiclesQuery.data, availableVehiclesQuery.data])

  const isLoading = allVehiclesQuery.isLoading || availableVehiclesQuery.isLoading
  const error = allVehiclesQuery.error || availableVehiclesQuery.error

  if (isLoading) return <Loader label="Loading vehicle overview..." />
  if (error) {
    return (
      <ErrorMessage
        message={error.message || 'Could not load vehicle data.'}
        onRetry={() => {
          allVehiclesQuery.refetch()
          availableVehiclesQuery.refetch()
        }}
      />
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Vehicle Overview</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {canManage ? 'Manage and track every vehicle in the dealership.' : 'Browse the current vehicle catalog.'}
          </p>
        </div>
        <div className="flex items-center gap-2">
          {canManage && (
            <Link to="/vehicles/add">
              <Button icon={Plus}>Add Vehicle</Button>
            </Link>
          )}
          <Link to="/vehicles/list">
            <Button icon={ArrowRight} variant="secondary">
              View full inventory
            </Button>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          icon={Car}
          label="Total Listings"
          value={stats.totalListings}
          hint="Across every status"
          accent="primary"
        />
        <StatCard
          icon={CheckCircle2}
          label="Available"
          value={stats.availableListings}
          hint="Ready to sell"
          accent="green"
        />
        <StatCard
          icon={Layers}
          label="Total Stock Units"
          value={stats.totalUnits}
          hint="Sum of quantity across listings"
          accent="blue"
        />
        <StatCard
          icon={IndianRupee}
          label="Inventory Value"
          value={formatCurrency(stats.totalValue)}
          hint={`Avg. price ${formatCurrency(stats.avgPrice)}`}
          accent="amber"
        />
      </div>

      <div className="card p-5">
        <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200 mb-4">Listings by Brand</h2>
        {stats.topBrands.length === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">No vehicles to chart yet.</p>
        ) : (
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={stats.topBrands} margin={{ top: 4, right: 8, left: -12, bottom: 4 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-gray-200 dark:stroke-gray-800" />
                <XAxis dataKey="brand" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                <Tooltip
                  contentStyle={{ fontSize: 12, borderRadius: 8 }}
                  formatter={(value) => [value, 'Listings']}
                />
                <Bar dataKey="count" fill="#4f46e5" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Category and status breakdowns aren't shown here: the backend's vehicle API doesn't currently return
          those fields on any vehicle response, so per-category and per-status charts would be inaccurate. This
          is a backend data limitation, not a missing frontend feature.
        </p>
      </div>
    </div>
  )
}
