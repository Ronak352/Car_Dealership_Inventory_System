import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, Info, PlusCircle } from 'lucide-react'
import { createCustomer } from '../../api/customerApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'

// Mirrors com.dealership.dto.request.CustomerRequest (address/city/state/
// pincode) against POST /api/customers/{userId}, which is allowed for
// ADMIN, MANAGER and CUSTOMER.
//
// Two ways this page is reached:
//   1. A signed-in CUSTOMER completing their own profile (from
//      CustomerProfile's "no profile yet" state) -- userId comes from their
//      own session (user.userId), so that field is hidden entirely.
//   2. An ADMIN/MANAGER creating a profile on behalf of some other user.
//      There is no backend endpoint to list/search users (only
//      /api/auth/register creates them, and CustomerController takes a bare
//      userId path variable), so the target user's id has to be entered by
//      hand here -- a real backend limitation, surfaced below rather than
//      worked around.
export default function AddCustomer() {
  const navigate = useNavigate()
  const { user } = useAuth()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

  const isSelfService = user?.role === 'CUSTOMER'

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      userId: '',
      address: '',
      city: '',
      state: '',
      pincode: '',
    },
  })

  const onSubmit = async (data) => {
    const targetUserId = isSelfService ? user.userId : Number(data.userId)
    setSubmitting(true)
    try {
      const payload = {
        address: data.address.trim(),
        city: data.city.trim(),
        state: data.state.trim(),
        pincode: data.pincode.trim(),
      }
      const created = await createCustomer(targetUserId, payload)
      toast.success('Customer profile created.')
      if (isSelfService) {
        navigate('/customers/profile')
      } else {
        navigate(`/customers/${created.id}`, { state: { customer: created } })
      }
    } catch (err) {
      // 404 -> no user with that id; 409 -> that user already has a
      // customer profile. Both come through already normalized as
      // err.message by the axios interceptor.
      toast.error(err.message || 'Could not create customer profile.')
    } finally {
      setSubmitting(false)
    }
  }

  const backTo = isSelfService ? '/customers/profile' : '/customers/list'

  return (
    <div className="max-w-xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to={backTo} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">{isSelfService ? 'Complete Your Profile' : 'Add Customer Profile'}</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {isSelfService
              ? 'Add your address details so the dealership can complete purchases and deliveries for you.'
              : 'Attach address details to an existing user account.'}
          </p>
        </div>
      </div>

      {!isSelfService && (
        <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
          <Info className="h-5 w-5 shrink-0 mt-0.5" />
          <p>
            There's no backend API to search or list user accounts, so you'll need to know the target user's id
            already (for example from when they registered). If the id doesn't exist, or that user already has a
            customer profile, this will show an error rather than create a duplicate.
          </p>
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        {!isSelfService && (
          <Input
            label="User ID"
            type="number"
            required
            error={errors.userId?.message}
            {...register('userId', {
              required: 'User id is required',
              min: { value: 1, message: 'Must be a valid id' },
            })}
          />
        )}

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
            error={errors.city?.message}
            {...register('city', { required: 'City is required' })}
          />
          <Input
            label="State"
            required
            error={errors.state?.message}
            {...register('state', { required: 'State is required' })}
          />
        </div>

        <Input
          label="Pincode"
          hint="6 digits"
          error={errors.pincode?.message}
          {...register('pincode', {
            pattern: { value: /^\d{6}$/, message: 'Pincode must contain 6 digits' },
          })}
        />

        <div className="flex justify-end gap-3 pt-2">
          <Link to={backTo}>
            <Button type="button" variant="secondary">
              Cancel
            </Button>
          </Link>
          <Button type="submit" icon={PlusCircle} loading={submitting}>
            Save Profile
          </Button>
        </div>
      </form>
    </div>
  )
}
