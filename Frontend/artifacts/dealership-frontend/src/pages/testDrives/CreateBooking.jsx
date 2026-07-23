import React from 'react'
import { useForm } from 'react-hook-form'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { createTestDrive } from '../../api/testDriveApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'

export default function CreateBooking() {
  const { register, handleSubmit, formState: { errors } } = useForm()
  const navigate = useNavigate()
  const { toast } = useToast()
  const queryClient = useQueryClient()

  const { mutate: save, isPending } = useMutation({
    mutationFn: createTestDrive,
    onSuccess: (data) => {
      toast.success('Test drive booked successfully.')
      queryClient.invalidateQueries({ queryKey: ['testDrives'] })
      navigate(`/test-drives/${data.id}`)
    },
    onError: (err) => toast.error(err.message || 'Failed to book test drive.')
  })

  const onSubmit = (data) => {
    save({
      ...data,
      customerId: Number(data.customerId),
      vehicleId: Number(data.vehicleId)
    })
  }

  return (
    <div className="max-w-xl space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Book Test Drive</h1>
        <p className="text-sm text-gray-500">Schedule a test drive for a vehicle.</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-5 space-y-4">
        <Input label="Customer ID" type="number" required {...register('customerId', { required: 'Required' })} error={errors.customerId?.message} />
        <Input label="Vehicle ID" type="number" required {...register('vehicleId', { required: 'Required' })} error={errors.vehicleId?.message} />
        <Input label="Scheduled Date & Time" type="datetime-local" required {...register('scheduledDate', { required: 'Required' })} error={errors.scheduledDate?.message} />
        
        <div>
          <label className="label-base">Notes</label>
          <textarea
            className="input-base min-h-[100px]"
            {...register('notes')}
          />
        </div>
        
        <div className="pt-4 flex justify-end">
          <Button type="submit" loading={isPending}>Book Test Drive</Button>
        </div>
      </form>
    </div>
  )
}
