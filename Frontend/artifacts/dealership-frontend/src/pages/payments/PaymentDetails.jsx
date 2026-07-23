import React, { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getPaymentById, updatePaymentStatus } from '../../api/paymentApi'
import { useAuth } from '../../hooks/useAuth'
import Badge from '../../components/common/Badge'
import Button from '../../components/common/Button'
import Select from '../../components/common/Select'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import { formatCurrency, formatDateTime } from '../../utils/formatters'
import { useToast } from '../../hooks/useToast'

export default function PaymentDetails() {
  const { id } = useParams()
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const { toast } = useToast()
  
  const canUpdate = user?.role === 'ADMIN' || user?.role === 'MANAGER'

  const { data: payment, isLoading, error } = useQuery({
    queryKey: ['payments', id],
    queryFn: () => getPaymentById(id)
  })

  const [newStatus, setNewStatus] = useState('')

  const { mutate: updateStatus, isPending } = useMutation({
    mutationFn: (status) => updatePaymentStatus(id, status),
    onSuccess: () => {
      toast.success('Payment status updated.')
      queryClient.invalidateQueries({ queryKey: ['payments'] })
      setNewStatus('')
    },
    onError: (err) => toast.error(err.message || 'Failed to update status.')
  })

  if (isLoading) return <Loader />
  if (error) return <ErrorMessage message="Failed to load payment details" />
  if (!payment) return <ErrorMessage message="Payment not found" />

  return (
    <div className="max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Payment #{payment.id}</h1>
          <p className="text-sm text-gray-500">Transaction details</p>
        </div>
        <Badge status={payment.paymentStatus} />
      </div>

      <div className="card p-5 grid grid-cols-2 gap-y-4 gap-x-8">
        <div>
          <span className="text-sm text-gray-500">Purchase</span>
          <p className="font-medium">
            <Link to={`/purchases/${payment.purchaseId}`} className="text-primary-600 hover:underline">
              View Purchase #{payment.purchaseId}
            </Link>
          </p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Amount</span>
          <p className="font-medium text-lg">{formatCurrency(payment.amount)}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Payment Method</span>
          <p className="font-medium">{payment.paymentMethod}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Transaction ID</span>
          <p className="font-medium">{payment.transactionId || '-'}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Payment Date</span>
          <p className="font-medium">{formatDateTime(payment.paymentDate)}</p>
        </div>
      </div>

      {canUpdate && (
        <div className="card p-5">
          <h2 className="text-lg font-semibold mb-3">Update Status</h2>
          <div className="flex items-end gap-3">
            <Select 
              label="New Status"
              value={newStatus}
              onChange={(e) => setNewStatus(e.target.value)}
              options={['PENDING', 'COMPLETED', 'FAILED', 'REFUNDED']}
              containerClassName="w-48"
            />
            <Button 
              onClick={() => updateStatus(newStatus)}
              disabled={!newStatus || newStatus === payment.paymentStatus}
              loading={isPending}
            >
              Update
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
