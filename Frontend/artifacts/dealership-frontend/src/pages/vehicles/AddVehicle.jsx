import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, PlusCircle } from 'lucide-react'
import { createVehicle } from '../../api/vehicleApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import {
  VEHICLE_CATEGORIES,
  FUEL_TYPES,
  TRANSMISSIONS,
  VEHICLE_CONDITIONS,
  VEHICLE_STATUSES,
} from '../../utils/constants'

// Mirrors com.dealership.dto.request.VehicleRequest field-for-field so the
// payload this form builds is always valid against the (locked) backend.
export default function AddVehicle() {
  const navigate = useNavigate()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

  const {
    register,
    handleSubmit,
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
      await createVehicle(payload)
      toast.success(`${payload.brand} ${payload.model} added to inventory.`)
      navigate('/vehicles/list')
    } catch (err) {
      // 409 duplicate VIN and 400 validation messages both come through here
      // already normalized by the axios interceptor.
      toast.error(err.message || 'Could not add vehicle.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-3xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to="/vehicles/list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Add Vehicle</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Register a new vehicle in the inventory.</p>
        </div>
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
            hint="Must be unique across all vehicles"
            error={errors.vinNumber?.message}
            {...register('vinNumber', { required: 'VIN number is required' })}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Select
            label="Category"
            required
            options={VEHICLE_CATEGORIES}
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
            hint="Optional, defaults to 0"
            error={errors.discount?.message}
            {...register('discount', { min: { value: 0, message: 'Discount cannot be negative' } })}
          />
          <Input
            label="Quantity"
            type="number"
            min="0"
            hint="Optional, defaults to 0"
            error={errors.quantity?.message}
            {...register('quantity', { min: { value: 0, message: 'Quantity cannot be negative' } })}
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Select
            label="Condition"
            required
            options={VEHICLE_CONDITIONS}
            error={errors.condition?.message}
            {...register('condition', { required: 'Condition is required' })}
          />
          <Select
            label="Status"
            required
            options={VEHICLE_STATUSES}
            error={errors.status?.message}
            {...register('status', { required: 'Status is required' })}
          />
        </div>

        <div className="flex justify-end gap-3 pt-2">
          <Link to="/vehicles/list">
            <Button type="button" variant="secondary">
              Cancel
            </Button>
          </Link>
          <Button type="submit" icon={PlusCircle} loading={submitting}>
            Add Vehicle
          </Button>
        </div>
      </form>
    </div>
  )
}
