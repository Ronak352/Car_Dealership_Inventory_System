import React, { useEffect, useMemo, useState } from 'react'
import { useForm } from 'react-hook-form'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { AlertTriangle, ArrowLeft, Save } from 'lucide-react'
import { getAllVehicles, updateVehicle } from '../../api/vehicleApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import {
  VEHICLE_CATEGORIES,
  FUEL_TYPES,
  TRANSMISSIONS,
  VEHICLE_CONDITIONS,
  VEHICLE_STATUSES,
} from '../../utils/constants'

// IMPORTANT backend limitation: VehicleResponse (returned by every GET/list/
// search/add/update call) only ever carries id, brand, model, variant,
// vinNumber, price and quantity. category, fuelType, transmission,
// manufacturingYear, color, engineNumber, discount, condition and status are
// part of VehicleRequest but are never sent back by the backend once a
// vehicle exists -- so this form cannot know a vehicle's current values for
// those fields and cannot prefill them.
//
// PUT /api/vehicles/{id} additionally replaces the ENTIRE vehicle record
// (VehicleServiceImpl.updateVehicle sets every field from the request), so
// submitting this form with those fields left blank will clear them on the
// backend, not just leave them unchanged. We surface that plainly below
// instead of silently prefilling incorrect defaults or hiding the risk.
export default function EditVehicle() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

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

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      brand: '',
      model: '',
      variant: '',
      category: '',
      fuelType: '',
      transmission: '',
      manufacturingYear: '',
      color: '',
      engineNumber: '',
      vinNumber: '',
      price: '',
      discount: '',
      quantity: '',
      condition: '',
      status: '',
    },
  })

  // Prefill only the fields we actually have data for once the vehicle loads.
  useEffect(() => {
    if (!vehicle) return
    reset({
      brand: vehicle.brand || '',
      model: vehicle.model || '',
      variant: vehicle.variant || '',
      category: '',
      fuelType: '',
      transmission: '',
      manufacturingYear: '',
      color: '',
      engineNumber: '',
      vinNumber: vehicle.vinNumber || '',
      price: vehicle.price ?? '',
      discount: '',
      quantity: vehicle.quantity ?? '',
      condition: '',
      status: '',
    })
  }, [vehicle, reset])

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      const payload = {
        brand: data.brand.trim(),
        model: data.model.trim(),
        variant: data.variant.trim() || null,
        category: data.category,
        fuelType: data.fuelType || null,
        transmission: data.transmission || null,
        manufacturingYear: data.manufacturingYear ? Number(data.manufacturingYear) : null,
        color: data.color.trim() || null,
        engineNumber: data.engineNumber.trim() || null,
        vinNumber: data.vinNumber.trim(),
        price: Number(data.price),
        discount: data.discount ? Number(data.discount) : 0,
        quantity: data.quantity ? Number(data.quantity) : 0,
        condition: data.condition,
        status: data.status,
      }
      await updateVehicle(vehicle.id, payload)
      toast.success(`${payload.brand} ${payload.model} was updated.`)
      navigate(`/vehicles/${vehicle.id}`, { state: { vehicle: { ...vehicle, ...payload, id: vehicle.id } } })
    } catch (err) {
      toast.error(err.message || 'Could not update vehicle.')
    } finally {
      setSubmitting(false)
    }
  }

  if (needsFallbackFetch && isLoading) return <Loader label="Loading vehicle..." />
  if (needsFallbackFetch && error) {
    return <ErrorMessage message={error.message || 'Could not load vehicle.'} onRetry={refetch} />
  }
  if (!vehicle) {
    return <ErrorMessage message={`No vehicle found with id ${id}.`} />
  }

  return (
    <div className="max-w-3xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to={`/vehicles/${vehicle.id}`} state={{ vehicle }} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">
            Edit {vehicle.brand} {vehicle.model}
          </h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">VIN {vehicle.vinNumber}</p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <AlertTriangle className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          The backend doesn't return this vehicle's current category, fuel type, transmission, manufacturing
          year, color, engine number, discount, or condition -- so those fields start blank below. Saving this
          form replaces the vehicle's entire record, so please fill in everything you want kept; leaving a field
          blank will clear it rather than leave it unchanged.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Brand"
            required
            error={errors.brand?.message}
            {...register('brand', { required: 'Brand is required' })}
          />
          <Input
            label="Model"
            required
            error={errors.model?.message}
            {...register('model', { required: 'Model is required' })}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input label="Variant" hint="Optional" {...register('variant')} />
          <Input
            label="VIN Number"
            required
            error={errors.vinNumber?.message}
            {...register('vinNumber', { required: 'VIN number is required' })}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Select
            label="Category"
            required
            options={VEHICLE_CATEGORIES}
            hint="Not returned by backend -- please re-select"
            error={errors.category?.message}
            {...register('category', { required: 'Category is required' })}
          />
          <Select label="Fuel Type" options={FUEL_TYPES} hint="Optional" {...register('fuelType')} />
          <Select label="Transmission" options={TRANSMISSIONS} hint="Optional" {...register('transmission')} />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Input
            label="Manufacturing Year"
            type="number"
            hint="Optional"
            error={errors.manufacturingYear?.message}
            {...register('manufacturingYear', {
              min: { value: 1, message: 'Must be a positive year' },
            })}
          />
          <Input label="Color" hint="Optional" {...register('color')} />
          <Input label="Engine Number" hint="Optional" {...register('engineNumber')} />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Input
            label="Price"
            type="number"
            step="0.01"
            min="0"
            required
            error={errors.price?.message}
            {...register('price', {
              required: 'Price is required',
              min: { value: 0.01, message: 'Price must be positive' },
            })}
          />
          <Input
            label="Discount"
            type="number"
            step="0.01"
            min="0"
            hint="Not returned by backend -- please re-enter"
            error={errors.discount?.message}
            {...register('discount', { min: { value: 0, message: 'Discount cannot be negative' } })}
          />
          <Input
            label="Quantity"
            type="number"
            min="0"
            error={errors.quantity?.message}
            {...register('quantity', { min: { value: 0, message: 'Quantity cannot be negative' } })}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Select
            label="Condition"
            required
            options={VEHICLE_CONDITIONS}
            hint="Not returned by backend -- please re-select"
            error={errors.condition?.message}
            {...register('condition', { required: 'Condition is required' })}
          />
          <Select
            label="Status"
            required
            options={VEHICLE_STATUSES}
            hint="Not returned by backend -- please re-select"
            error={errors.status?.message}
            {...register('status', { required: 'Status is required' })}
          />
        </div>

        <div className="flex justify-end gap-3 pt-2">
          <Link to={`/vehicles/${vehicle.id}`} state={{ vehicle }}>
            <Button type="button" variant="secondary">
              Cancel
            </Button>
          </Link>
          <Button type="submit" icon={Save} loading={submitting}>
            Save Changes
          </Button>
        </div>
      </form>
    </div>
  )
}
