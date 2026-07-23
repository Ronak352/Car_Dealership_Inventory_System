import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { AlertTriangle, ArrowRight, Info, PackageX, TriangleAlert } from 'lucide-react'
import { getLowStockVehicles } from '../../api/inventoryApi'
import { formatCurrency, getStockLevel } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Input from '../../components/common/Input'
import Badge from '../../components/common/Badge'
import Button from '../../components/common/Button'

// Dedicated alerts view built directly on GET /inventory/low-stock?threshold=
// (ADMIN, MANAGER only), which is the one backend call that actually
// returns quantity, category and status set on VehicleResponse -- see
// inventoryApi.js. Distinct from the "Low stock" filter on
// VehicleStockList: this page is meant to be scanned quickly and acted on,
// sorted worst-first with a direct restock link per row.
export default function LowStockAlert() {
  const [threshold, setThreshold] = useState(5)

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['inventory', 'low-stock', threshold],
    queryFn: () => getLowStockVehicles(threshold),
  })

  const rows = useMemo(() => {
    const list = data || []
    return [...list].sort((a, b) => (Number(a.quantity) || 0) - (Number(b.quantity) || 0))
  }, [data])

  const outOfStockCount = rows.filter((v) => getStockLevel(v.quantity, threshold) === 'OUT_OF_STOCK').length

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold">Low Stock Alerts</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            Vehicles at or below the threshold, worst first.
          </p>
        </div>
        <Input
          containerClassName="w-40"
          label="Threshold"
          type="number"
          min={0}
          value={threshold}
          onChange={(e) => setThreshold(Math.max(0, Number(e.target.value) || 0))}
        />
      </div>

      {isLoading && <Loader label="Loading alerts..." />}
      {!isLoading && error && (
        <ErrorMessage message={error.message || 'Could not load low-stock vehicles.'} onRetry={refetch} />
      )}

      {!isLoading && !error && (
        <>
          {rows.length === 0 ? (
            <div className="card p-10 flex flex-col items-center justify-center gap-2 text-center">
              <TriangleAlert className="h-8 w-8 text-gray-300" />
              <p className="text-sm text-gray-500 dark:text-gray-400">
                Nothing is at or below {threshold} units right now.
              </p>
            </div>
          ) : (
            <div className="space-y-3">
              {outOfStockCount > 0 && (
                <div className="flex items-start gap-3 rounded-xl border border-red-200 dark:border-red-900 bg-red-50 dark:bg-red-950/30 p-4 text-sm text-red-800 dark:text-red-300">
                  <PackageX className="h-5 w-5 shrink-0 mt-0.5" />
                  <p>
                    {outOfStockCount} vehicle{outOfStockCount === 1 ? ' is' : 's are'} completely out of stock and
                    need immediate attention.
                  </p>
                </div>
              )}
              <div className="card divide-y divide-gray-100 dark:divide-gray-800">
                {rows.map((v) => {
                  const level = getStockLevel(v.quantity, threshold)
                  return (
                    <div key={v.id} className="flex flex-wrap items-center justify-between gap-3 p-4">
                      <div className="flex items-start gap-3 min-w-0">
                        <div
                          className={`rounded-xl p-2.5 ${
                            level === 'OUT_OF_STOCK'
                              ? 'bg-red-50 text-red-600 dark:bg-red-900/30 dark:text-red-300'
                              : 'bg-amber-50 text-amber-600 dark:bg-amber-900/30 dark:text-amber-300'
                          }`}
                        >
                          <AlertTriangle className="h-4 w-4" />
                        </div>
                        <div className="min-w-0">
                          <p className="font-medium truncate">
                            {v.brand} {v.model} {v.variant ? <span className="text-gray-400">· {v.variant}</span> : null}
                          </p>
                          <p className="text-xs text-gray-500 dark:text-gray-400">
                            VIN {v.vinNumber} · {formatCurrency(v.price)}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <div className="text-right">
                          <p className="text-sm font-semibold">{v.quantity} left</p>
                          <Badge status={level} />
                        </div>
                        <Link to={`/inventory/stock/${v.id}`} state={{ vehicle: v }}>
                          <Button size="sm" icon={ArrowRight}>
                            Restock
                          </Button>
                        </Link>
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </>
      )}

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Backed directly by <code>GET /inventory/low-stock?threshold=</code>; changing the threshold re-queries the
          backend rather than filtering client-side. "Restock" opens the vehicle's stock console to add, remove, or
          set an exact quantity.
        </p>
      </div>
    </div>
  )
}
