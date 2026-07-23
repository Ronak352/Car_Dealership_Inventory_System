import React from 'react'
import { useForm } from 'react-hook-form'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { createPurchase } from '../../api/purchaseApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import { PAYMENT_METHODS, PURCHASE_STATUSES } from '../../utils/constants'

export default function AddPurchase() {
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: { purchaseStatus: 'BOOKED', paymentMethod: 'CASH' }
  })
  const navigate = useNavigate()
  const { toast } = useToast()
  const queryClient = useQueryClient()

  const { mutate: save, isPending } = useMutation({
    mutationFn: createPurchase,
    onSuccess: (data) => {
      toast.success('Purchase created successfully.')
      queryClient.invalidateQueries({ queryKey: ['purchases'] })
      navigate(`/purchases/${data.id}`)
    },
    onError: (err) => toast.error(err.message || 'Failed to create purchase.')
  })

  const onSubmit = (data) => {
    save({
      ...data,
      customerId: Number(data.customerId),
      vehicleId: Number(data.vehicleId),
      salespersonId: Number(data.salespersonId),
      sellingPrice: Number(data.sellingPrice)
    })
  }

  return (
    <div className="max-w-2xl space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Add Purchase</h1>
        <p className="text-sm text-gray-500">Record a new vehicle purchase.</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-5 space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input label="Customer ID" type="number" required {...register('customerId', { required: 'Required' })} error={errors.customerId?.message} />
          <Input label="Vehicle ID" type="number" required {...register('vehicleId', { required: 'Required' })} error={errors.vehicleId?.message} />
          <Input label="Salesperson ID" type="number" required {...register('salespersonId', { required: 'Required' })} error={errors.salespersonId?.message} />
          <Input label="Selling Price" type="number" required {...register('sellingPrice', { required: 'Required' })} error={errors.sellingPrice?.message} />
          <Input label="Purchase Date" type="date" required {...register('purchaseDate', { required: 'Required' })} error={errors.purchaseDate?.message} />
          <Input label="Delivery Date" type="date" required {...register('deliveryDate', { required: 'Required' })} error={errors.deliveryDate?.message} />
          <Select label="Payment Method" required {...register('paymentMethod', { required: 'Required' })} error={errors.paymentMethod?.message} options={PAYMENT_METHODS} />
          <Select label="Status" required {...register('purchaseStatus', { required: 'Required' })} error={errors.purchaseStatus?.message} options={PURCHASE_STATUSES} />
        </div>
        <div className="pt-4 flex justify-end">
          <Button type="submit" loading={isPending}>Save Purchase</Button>
        </div>
      </form>
    </div>
  )
}
