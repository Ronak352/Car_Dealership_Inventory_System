import React, { useEffect, useMemo, useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import {
  ArrowLeft,
  History,
  Info,
  MinusCircle,
  PlusCircle,
  RotateCcw,
} from 'lucide-react'
import { getAllVehicles } from '../../api/vehicleApi'
import {
  decreaseStock,
  getAvailableQuantity,
  getInventoryHistory,
  increaseStock,
  setStock,
} from '../../api/inventoryApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import { formatCurrency, formatDateTime, getStockLevel } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'
import Badge from '../../components/common/Badge'
import Modal from '../../components/common/Modal'

const LOW_STOCK_THRESHOLD = 5

// Stock adjustment console for a single vehicle. Wraps the three mutation
// endpoints on InventoryController (increase/decrease/update), all of which
// are ADMIN, MANAGER only server-side (matching this page's route guard),
// and require a `performedByUserId` -- there's no user-lookup endpoint on
// the backend, so per inventoryApi.js this always defaults to the signed-in
// user's own id rather than asking them to type someone else's.
export default function StockDetails() {
  const { vehicleId } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { toast } = useToast()
  const queryClient = useQueryClient()

  const stateVehicle = location.state?.vehicle
  const needsFallbackFetch = !stateVehicle

  const {
    data: vehicleList,
    isLoading: vehicleLoading,
    error: vehicleError,
    refetch: refetchVehicle,
  } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
    enabled: needsFallbackFetch,
  })

  const vehicle = useMemo(() => {
    if (stateVehicle) return stateVehicle
    return (vehicleList || []).find((v) => String(v.id) === String(vehicleId)) || null
  }, [stateVehicle, vehicleList, vehicleId])

  const {
    data: quantity,
    isLoading: quantityLoading,
    error: quantityError,
    refetch: refetchQuantity,
  } = useQuery({
    queryKey: ['inventory', 'quantity', vehicleId],
    queryFn: () => getAvailableQuantity(vehicleId),
  })

  const {
    data: history,
    isLoading: historyLoading,
    error: historyError,
    refetch: refetchHistory,
  } = useQuery({
    queryKey: ['inventory', 'history', vehicleId],
    queryFn: () => getInventoryHistory(vehicleId),
  })

  const [increaseQty, setIncreaseQty] = useState(1)
  const [decreaseQty, setDecreaseQty] = useState(1)
  const [setQty, setSetQty] = useState('')
  const [busyAction, setBusyAction] = useState(null) // 'increase' | 'decrease' | 'set' | null
  const [confirmDecrease, setConfirmDecrease] = useState(false)

  useEffect(() => {
    if (quantity !== undefined && quantity !== null) setSetQty(String(quantity))
  }, [quantity])

  function afterMutation(message) {
    toast.success(message)
    refetchQuantity()
    refetchHistory()
    queryClient.invalidateQueries({ queryKey: ['vehicles'] })
    queryClient.invalidateQueries({ queryKey: ['inventory', 'low-stock'] })
  }

  async function handleIncrease() {
    if (!increaseQty || increaseQty <= 0) {
      toast.error('Enter a quantity greater than 0.')
      return
    }
    setBusyAction('increase')
    try {
      await increaseStock(vehicleId, Number(increaseQty), user?.userId)
      afterMutation(`Added ${increaseQty} unit${increaseQty === 1 ? '' : 's'} to stock.`)
      setIncreaseQty(1)
    } catch (err) {
      toast.error(err.message || 'Could not increase stock.')
    } finally {
      setBusyAction(null)
    }
  }

  async function handleDecrease() {
    setConfirmDecrease(false)
    if (!decreaseQty || decreaseQty <= 0) {
      toast.error('Enter a quantity greater than 0.')
      return
    }
    setBusyAction('decrease')
    try {
      await decreaseStock(vehicleId, Number(decreaseQty), user?.userId)
      afterMutation(`Removed ${decreaseQty} unit${decreaseQty === 1 ? '' : 's'} from stock.`)
      setDecreaseQty(1)
    } catch (err) {
      toast.error(err.message || 'Could not decrease stock -- it may exceed what is currently available.')
    } finally {
      setBusyAction(null)
    }
  }

  async function handleSet() {
    if (setQty === '' || Number(setQty) < 0) {
      toast.error('Enter a valid quantity (0 or more).')
      return
    }
    setBusyAction('set')
    try {
      await setStock(vehicleId, Number(setQty), user?.userId)
      afterMutation(`Stock set to ${setQty} unit${Number(setQty) === 1 ? '' : 's'}.`)
    } catch (err) {
      toast.error(err.message || 'Could not update stock.')
    } finally {
      setBusyAction(null)
    }
  }

  if (needsFallbackFetch && vehicleLoading) return <Loader label="Loading vehicle..." />
  if (needsFallbackFetch && vehicleError) {
    return <ErrorMessage message={vehicleError.message || 'Could not load vehicle.'} onRetry={refetchVehicle} />
  }
  if (!vehicle) {
    return <ErrorMessage message={`No vehicle found with id ${vehicleId}.`} />
  }

  const recentHistory = (history || []).slice(0, 5)

  return (
    <div className="space-y-4 max-w-4xl">
      <div className="flex items-center gap-3">
        <Link to="/inventory/stock-list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div className="min-w-0">
          <h1 className="text-xl font-semibold truncate">
            {vehicle.brand} {vehicle.model}
            {vehicle.variant ? <span className="text-gray-400"> · {vehicle.variant}</span> : null}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            VIN {vehicle.vinNumber} · {formatCurrency(vehicle.price)}
          </p>
        </div>
      </div>

      <div className="card p-5 flex flex-wrap items-center justify-between gap-4">
        <div>
          <p className="text-sm text-gray-500 dark:text-gray-400">Current available quantity</p>
          {quantityLoading ? (
            <p className="text-2xl font-semibold">…</p>
          ) : quantityError ? (
            <p className="text-sm text-red-600">Could not load quantity.</p>
          ) : (
            <p className="text-3xl font-semibold">{quantity}</p>
          )}
        </div>
        {!quantityLoading && !quantityError && <Badge status={getStockLevel(quantity, LOW_STOCK_THRESHOLD)} />}
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="card p-5 space-y-3">
          <div className="flex items-center gap-2 text-green-700 dark:text-green-300">
            <PlusCircle className="h-4 w-4" />
            <h2 className="text-sm font-semibold">Restock</h2>
          </div>
          <Input
            label="Quantity to add"
            type="number"
            min="1"
            value={increaseQty}
            onChange={(e) => setIncreaseQty(e.target.value)}
          />
          <Button
            variant="primary"
            className="w-full justify-center"
            loading={busyAction === 'increase'}
            onClick={handleIncrease}
          >
            Add to Stock
          </Button>
        </div>

        <div className="card p-5 space-y-3">
          <div className="flex items-center gap-2 text-red-700 dark:text-red-300">
            <MinusCircle className="h-4 w-4" />
            <h2 className="text-sm font-semibold">Remove Stock</h2>
          </div>
          <Input
            label="Quantity to remove"
            type="number"
            min="1"
            value={decreaseQty}
            onChange={(e) => setDecreaseQty(e.target.value)}
            hint="Rejected if it exceeds what's currently available"
          />
          <Button
            variant="danger"
            className="w-full justify-center"
            loading={busyAction === 'decrease'}
            onClick={() => setConfirmDecrease(true)}
          >
            Remove from Stock
          </Button>
        </div>

        <div className="card p-5 space-y-3">
          <div className="flex items-center gap-2 text-primary-700 dark:text-primary-300">
            <RotateCcw className="h-4 w-4" />
            <h2 className="text-sm font-semibold">Set Exact Quantity</h2>
          </div>
          <Input
            label="New quantity"
            type="number"
            min="0"
            value={setQty}
            onChange={(e) => setSetQty(e.target.value)}
            hint="Overwrites current stock, not a delta"
          />
          <Button
            variant="secondary"
            className="w-full justify-center"
            loading={busyAction === 'set'}
            onClick={handleSet}
          >
            Set Stock
          </Button>
        </div>
      </div>

      <div className="card p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-sm font-semibold text-gray-700 dark:text-gray-200">Recent Movement</h2>
          <button
            onClick={() => navigate(`/inventory/stock/${vehicleId}/history`, { state: { vehicle } })}
            className="text-sm text-primary-600 hover:underline flex items-center gap-1"
          >
            <History className="h-3.5 w-3.5" /> Full history
          </button>
        </div>
        {historyLoading ? (
          <Loader label="Loading history..." />
        ) : historyError ? (
          <ErrorMessage message={historyError.message || 'Could not load history.'} onRetry={refetchHistory} />
        ) : recentHistory.length === 0 ? (
          <p className="text-sm text-gray-400 py-6 text-center">No stock movements recorded for this vehicle yet.</p>
        ) : (
          <ul className="divide-y divide-gray-100 dark:divide-gray-800">
            {recentHistory.map((entry) => (
              <li key={entry.id} className="flex items-center justify-between py-3 text-sm gap-3">
                <div className="flex items-center gap-2">
                  <Badge status={entry.operationType} />
                  <span>
                    {entry.operationType === 'REMOVE' ? '-' : entry.operationType === 'ADD' ? '+' : ''}
                    {Math.abs(entry.quantity)} units
                  </span>
                </div>
                <div className="text-right text-gray-500 dark:text-gray-400">
                  <p>{formatDateTime(entry.date)}</p>
                  <p className="text-xs">{entry.performedByName || '—'}</p>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          There's no user-lookup endpoint on the backend, so every action here is recorded against your own signed-in
          account ({user?.email || 'current user'}) as <code>performedByUserId</code> -- matching how
          <code> increase</code>/<code>decrease</code>/<code>update</code> require that field.
        </p>
      </div>

      <Modal
        open={confirmDecrease}
        onClose={() => setConfirmDecrease(false)}
        title="Remove stock?"
        footer={
          <>
            <Button variant="secondary" onClick={() => setConfirmDecrease(false)}>
              Cancel
            </Button>
            <Button variant="danger" loading={busyAction === 'decrease'} onClick={handleDecrease}>
              Remove {decreaseQty || 0} unit{Number(decreaseQty) === 1 ? '' : 's'}
            </Button>
          </>
        }
      >
        <p className="text-sm text-gray-600 dark:text-gray-300">
          This will remove <strong>{decreaseQty || 0}</strong> unit{Number(decreaseQty) === 1 ? '' : 's'} from{' '}
          {vehicle.brand} {vehicle.model}'s available stock. The backend rejects this if it exceeds what's currently
          on hand.
        </p>
      </Modal>
    </div>
  )
}
