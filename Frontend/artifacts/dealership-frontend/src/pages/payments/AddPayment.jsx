import React from 'react'
import { useForm } from 'react-hook-form'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { createPayment } from '../../api/paymentApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import { PAYMENT_METHODS } from '../../utils/constants'

export default function AddPayment() {
  const [searchParams] = useSearchParams()
  const initialPurchaseId = searchParams.get('purchaseId') || ''

  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: { purchaseId: initialPurchaseId, paymentMethod: 'CASH' }
  })
  const navigate = useNavigate()
  const { toast } = useToast()
  const queryClient = useQueryClient()

  const { mutate: save, isPending } = useMutation({
    mutationFn: createPayment,
    onSuccess: (data) => {
      toast.success('Payment recorded successfully.')
      queryClient.invalidateQueries({ queryKey: ['payments'] })
      navigate(`/payments/${data.id}`)
    },
    onError: (err) => toast.error(err.message || 'Failed to record payment.')
  })

  const onSubmit = (data) => {
    save({
      ...data,
      purchaseId: Number(data.purchaseId),
      amount: Number(data.amount)
    })
  }

  return (
    <div className="max-w-xl space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Add Payment</h1>
        <p className="text-sm text-gray-500">Record a payment for a purchase.</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-5 space-y-4">
        <Input label="Purchase ID" type="number" required {...register('purchaseId', { required: 'Required' })} error={errors.purchaseId?.message} />
        <Input label="Amount" type="number" required {...register('amount', { required: 'Required', min: 1 })} error={errors.amount?.message} />
        <Select label="Payment Method" required {...register('paymentMethod', { required: 'Required' })} error={errors.paymentMethod?.message} options={PAYMENT_METHODS} />
        <Input label="Transaction ID" {...register('transactionId')} error={errors.transactionId?.message} hint="Optional reference number" />
        
        <div className="pt-4 flex justify-end">
          <Button type="submit" loading={isPending}>Save Payment</Button>
        </div>
      </form>
    </div>
  )
}
