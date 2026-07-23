import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { AlertTriangle, ArrowLeft, Save } from 'lucide-react'
import { getCustomerById, updateCustomer } from '../../api/customerApi'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'

// Unlike vehicles, GET /api/customers/{id} does exist, so this page always
// fetches fresh data -- location.state (if a row was clicked through from a
// list/details page) is only used as initialData for an instant first paint.
//
// IMPORTANT backend limitation: CustomerResponse only ever carries address
// (never city/state/pincode, even though CustomerRequest requires all four
// and they ARE stored -- CustomerServiceImpl.mapToResponse just never sets
// them). So city/state/pincode can't be prefilled here. And since PUT
// /api/customers/{id} overwrites all four fields with whatever this form
// submits (CustomerServiceImpl.updateCustomer sets every field, no partial
// update), leaving them blank would blank out what's already on file --
// that's surfaced below rather than silently prefilling wrong defaults.
export default function EditCustomer() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

  const { data: customer, isLoading, error, refetch } = useQuery({
    queryKey: ['customers', id],
    queryFn: () => getCustomerById(id),
    initialData: location.state?.customer,
  })

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    values: {
      address: customer?.address || '',
      city: '',
      state: '',
      pincode: '',
    },
  })

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      const payload = {
        address: data.address.trim(),
        city: data.city.trim(),
        state: data.state.trim(),
        pincode: data.pincode.trim(),
      }
      const updated = await updateCustomer(id, payload)
      toast.success('Customer profile updated.')
      navigate(`/customers/${id}`, { state: { customer: updated } })
    } catch (err) {
      toast.error(err.message || 'Could not update customer profile.')
    } finally {
      setSubmitting(false)
    }
  }

  if (isLoading) return <Loader label="Loading customer..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load customer.'} onRetry={refetch} />
  if (!customer) return <ErrorMessage message={`No customer found with id ${id}.`} />

  return (
    <div className="max-w-xl space-y-4">
      <div className="flex items-center gap-3">
        <Link
          to={`/customers/${id}`}
          state={{ customer }}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
        >
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Edit {customer.fullName}</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{customer.email}</p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <AlertTriangle className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          The backend doesn't return this profile's current city, state or pincode, so those fields start blank
          below. Saving this form replaces all four fields at once, so please fill in everything you want kept --
          leaving city, state or pincode blank will clear them rather than leave them unchanged.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        <Input
          label="Address"
          required
          error={errors.address?.message}
          {...register('address', { required: 'Address is required' })}
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="City"
            required
            hint="Not returned by backend -- please re-enter"
            error={errors.city?.message}
            {...register('city', { required: 'City is required' })}
          />
          <Input
            label="State"
            required
            hint="Not returned by backend -- please re-enter"
            error={errors.state?.message}
            {...register('state', { required: 'State is required' })}
          />
        </div>

        <Input
          label="Pincode"
          hint="Not returned by backend -- please re-enter. 6 digits."
          error={errors.pincode?.message}
          {...register('pincode', {
            pattern: { value: /^\d{6}$/, message: 'Pincode must contain 6 digits' },
          })}
        />

        <div className="flex justify-end gap-3 pt-2">
          <Link to={`/customers/${id}`} state={{ customer }}>
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
