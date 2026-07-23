import React, { useMemo, useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Info, Pencil, Trash2 } from 'lucide-react'
import { deleteVehicle, getAllVehicles } from '../../api/vehicleApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'
import { formatCurrency } from '../../utils/formatters'

// There is no GET /api/vehicles/{id} on the backend -- VehicleController only
// exposes add / list-available / search / update / delete. So this page is
// reached one of two ways:
//   1. Navigated to from VehicleList/VehicleDashboard, which passes the row's
//      data via router state -- no extra request needed.
//   2. Loaded directly (bookmark, refresh, shared link) with no state, in
//      which case we fall back to fetching the full inventory (the same
//      search-with-no-filters call VehicleList uses) and finding the id
//      client-side, since that's the only endpoint that can locate it.
export default function VehicleDetails() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { toast } = useToast()

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const canDelete = user?.role === 'ADMIN'

  const stateVehicle = location.state?.vehicle
  const needsFallbackFetch = !stateVehicle

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['vehicles', 'all'],
    queryFn: getAllVehicles,
    enabled: needsFallbackFetch,
  })

  const vehicle = useMemo(() => {
    if (stateVehicle) return stateVehicle
    return (data || []).find((v) => String(v.id) === String(id)) || null
  }, [stateVehicle, data, id])

  const [confirmOpen, setConfirmOpen] = useState(false)
  const [deleting, setDeleting] = useState(false)

  async function handleDelete() {
    setDeleting(true)
    try {
      await deleteVehicle(vehicle.id)
      toast.success(`${vehicle.brand} ${vehicle.model} was deleted.`)
      navigate('/vehicles/list')
    } catch (err) {
      toast.error(err.message || 'Could not delete vehicle.')
    } finally {
      setDeleting(false)
      setConfirmOpen(false)
    }
  }

  if (needsFallbackFetch && isLoading) return <Loader label="Loading vehicle..." />
  if (needsFallbackFetch && error) {
    return <ErrorMessage message={error.message || 'Could not load vehicle.'} onRetry={refetch} />
  }
  if (!vehicle) {
    return (
      <ErrorMessage message={`No vehicle found with id ${id}.`} />
    )
  }

  const fields = [
    { label: 'Brand', value: vehicle.brand },
    { label: 'Model', value: vehicle.model },
    { label: 'Variant', value: vehicle.variant || '—' },
    { label: 'VIN Number', value: vehicle.vinNumber },
    { label: 'Price', value: formatCurrency(vehicle.price) },
    { label: 'Quantity', value: vehicle.quantity ?? '—' },
  ]

  return (
    <div className="max-w-3xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to="/vehicles/list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">
            {vehicle.brand} {vehicle.model}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{vehicle.vinNumber}</p>
        </div>
      </div>

      <div className="card p-6">
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          {fields.map((f) => (
            <div key={f.label}>
              <dt className="text-xs uppercase tracking-wide text-gray-400">{f.label}</dt>
              <dd className="text-sm font-medium mt-0.5">{f.value}</dd>
            </div>
          ))}
        </dl>

        {(canManage || canDelete) && (
          <div className="flex justify-end gap-3 pt-6 mt-6 border-t border-gray-100 dark:border-gray-800">
            {canManage && (
              <Link to={`/vehicles/${vehicle.id}/edit`} state={{ vehicle }}>
                <Button variant="secondary" icon={Pencil}>
                  Edit
                </Button>
              </Link>
            )}
            {canDelete && (
              <Button variant="danger" icon={Trash2} onClick={() => setConfirmOpen(true)}>
                Delete
              </Button>
            )}
          </div>
        )}
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Category, status, fuel type, transmission, manufacturing year, color, engine number, discount and
          condition aren't shown: the backend's vehicle API never returns these fields on any response, even
          though they were provided when the vehicle was added.
        </p>
      </div>

      <Modal
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        title="Delete this vehicle?"
        footer={
          <>
            <Button variant="secondary" onClick={() => setConfirmOpen(false)}>
              Cancel
            </Button>
            <Button variant="danger" loading={deleting} onClick={handleDelete}>
              Delete
            </Button>
          </>
        }
      >
        <p className="text-sm text-gray-600 dark:text-gray-300">
          This will permanently remove <strong>{vehicle.brand} {vehicle.model}</strong> (VIN {vehicle.vinNumber})
          from the inventory. This cannot be undone.
        </p>
      </Modal>
    </div>
  )
}
