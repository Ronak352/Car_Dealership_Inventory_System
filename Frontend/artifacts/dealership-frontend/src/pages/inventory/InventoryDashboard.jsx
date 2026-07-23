import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { AlertTriangle, ArrowRight, Boxes, Info, PackageX, Warehouse } from 'lucide-react'
import { getAllVehicles } from '../../api/vehicleApi'
import { getLowStockVehicles } from '../../api/inventoryApi'
import { getStockLevel } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'
import Badge from '../../components/common/Badge'

function StatCard({ icon: Icon, label, value, hint, accent = 'primary' }) {
  const accents = {
    primary: 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300',
    green: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300',
    amber: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
    red: 'bg-red-50 text-red-700 dark:bg-red-900/30 dark:text-red-300',
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

const LOW_STOCK_THRESHOLD = 5

// There is no dedicated "inventory summary" endpoint on the backend --
// vehicle+quantity data comes from vehicleApi.getAllVehicles() (the same
// search-with-no-filters call VehicleList uses), and the low-stock preview
// comes from the real GET /inventory/low-stock endpoint (ADMIN, MANAGER
// only). Everything shown here is derived/aggregated client-side from
// those two calls.
export default function InventoryDashboard() {
  const {
    data: vehicles,
    isLoading: vehiclesLoading,
    error: vehiclesError,
    refetch: refetchVehicles,
  } = useQuery({ queryKey: ['vehicles', 'all'], queryFn: getAllVehicles })

  const {
    data: lowStock,
    isLoading: lowStockLoading,
    error: lowStockError,
    refetch: refetchLowStock,
  } = useQuery({
    queryKey: ['inventory', 'low-stock', LOW_STOCK_THRESHOLD],
    queryFn: () => getLowStockVehicles(LOW_STOCK_THRESHOLD),
  })

  const stats = useMemo(() => {
    const list = vehicles || []
    const totalUnits = list.reduce((sum, v) => sum + (Number(v.quantity) || 0), 0)
    const outOfStock = list.filter((v) => getStockLevel(v.quantity, LOW_STOCK_THRESHOLD) === 'OUT_OF_STOCK').length
    return { totalVehicles: list.length, totalUnits, outOfStock }
  }, [vehicles])

  const isLoading = vehiclesLoading || lowStockLoading
  const error = vehiclesError || lowStockError

  if (isLoading) return <Loader label="Loading inventory overview..." />
  if (error) {
    return (
      <ErrorMessage
        message={error.message || 'Could not load inventory data.'}
        onRetry={() => {
          refetchVehicles()
          refetchLowStock()
        }}
      />
    )
  }

  const lowStockList = lowStock || []

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Inventory Overview</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Stock levels across the vehicle catalog.</p>
        </div>
        <div className="flex items-center gap-2">
          <Link to="/inventory/overview">
            <Button icon={ArrowRight} variant="secondary">
              Stock Overview
            </Button>
          </Link>
          <Link to="/inventory/low-stock-alerts">
            <Button icon={AlertTriangle} variant="secondary">
              Low Stock Alerts
            </Button>
          </Link>
          <Link to="/inventory/stock-list">
            <Button icon={ArrowRight}>Vehicle Stock List</Button>
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          icon={Warehouse}
          label="Vehicles Tracked"
          value={stats.totalVehicles}
          hint="Distinct vehicle listings"
          accent="primary"
        />
        <StatCard
          icon={Boxes}
          label="Total Units In Stock"
          value={stats.totalUnits}
          hint="Sum of quantity across all vehicles"
          accent="green"
        />
        <StatCard
          icon={PackageX}
          label="Out of Stock"
          value={stats.outOfStock}
          hint="Quantity is 0"
          accent="red"
        />
      </div>

      <div className="card p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
            Low Stock (≤ {LOW_STOCK_THRESHOLD} units)
          </h2>
          <Link to="/inventory/low-stock-alerts" className="text-sm text-primary-600 hover:underline flex items-center gap-1">
            View all <ArrowRight className="h-3.5 w-3.5" />
          </Link>
        </div>
        {lowStockList.length === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">Nothing is running low right now.</p>
        ) : (
          <ul className="divide-y divide-gray-100 dark:divide-gray-800">
            {lowStockList.slice(0, 5).map((v) => (
              <li key={v.id} className="flex items-center justify-between py-3 text-sm">
                <Link to={`/inventory/stock/${v.id}`} state={{ vehicle: v }} className="hover:underline">
                  <p className="font-medium">
                    {v.brand} {v.model}
                  </p>
                  <p className="text-gray-500 dark:text-gray-400">{v.vinNumber}</p>
                </Link>
                <div className="flex items-center gap-2">
                  <Badge status={getStockLevel(v.quantity, LOW_STOCK_THRESHOLD)} />
                  <span className="text-gray-500 dark:text-gray-400 w-14 text-right">{v.quantity} left</span>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      {stats.outOfStock > 0 && (
        <div className="flex items-start gap-3 rounded-xl border border-red-200 dark:border-red-900 bg-red-50 dark:bg-red-950/30 p-4 text-sm text-red-800 dark:text-red-300">
          <AlertTriangle className="h-5 w-5 shrink-0 mt-0.5" />
          <p>
            {stats.outOfStock} vehicle{stats.outOfStock === 1 ? ' is' : 's are'} completely out of stock (quantity
            0). Check{' '}
            <Link to="/inventory/low-stock-alerts" className="underline">
              Low Stock Alerts
            </Link>{' '}
            for details.
          </p>
        </div>
      )}

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          There's no dedicated inventory-summary endpoint on the backend -- these totals are computed here from the
          full vehicle list, and the low-stock preview comes from the real <code>/inventory/low-stock</code>{' '}
          endpoint. Open a vehicle's stock console from the Stock List or an alert row to restock, reduce, set an
          exact quantity, or view its full movement history.
        </p>
      </div>
    </div>
  )
}
