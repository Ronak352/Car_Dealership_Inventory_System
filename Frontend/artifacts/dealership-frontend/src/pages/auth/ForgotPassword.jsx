import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link } from 'react-router-dom'
import { Car, KeyRound, Info } from 'lucide-react'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'

// NOTE: The backend (AuthController) currently only exposes /api/auth/register
// and /api/auth/login -- there is no password-reset endpoint. Per project
// rules ("do not create fake APIs"), this page collects the email and
// explains the real limitation instead of pretending to call a working
// endpoint. Wire the `onSubmit` handler up to a real endpoint (e.g.
// POST /api/auth/forgot-password) as soon as the backend adds one.
export default function ForgotPassword() {
  const [submitted, setSubmitted] = useState(false)
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ defaultValues: { email: '' } })

  const onSubmit = () => {
    setSubmitted(true)
  }

  return (
    <div>
      <Link to="/" className="lg:hidden mb-8 flex items-center gap-2 text-xl font-bold text-primary-700">
        <Car className="h-7 w-7" /> DriveHub
      </Link>

      <h1 className="text-2xl font-bold">Forgot your password?</h1>
      <p className="mt-1 text-sm text-gray-500">Enter your email and we'll help you get back in.</p>

      <div className="mt-4 flex gap-2 rounded-lg border border-blue-200 bg-blue-50 dark:bg-blue-950/30 dark:border-blue-900 px-3 py-2 text-xs text-blue-700 dark:text-blue-300">
        <Info className="h-4 w-4 shrink-0 mt-0.5" />
        <span>
          The current backend doesn't expose a password-reset API yet, so this
          request can't be completed automatically. Please contact an administrator
          to reset your password in the meantime.
        </span>
      </div>

      {submitted ? (
        <div className="mt-6 rounded-lg border border-green-200 bg-green-50 dark:bg-green-950/30 dark:border-green-900 px-4 py-3 text-sm text-green-700 dark:text-green-300">
          Thanks — once the backend supports password resets, a reset link will be sent to your email.
        </div>
      ) : (
        <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
          <Input
            label="Email"
            type="email"
            required
            error={errors.email?.message}
            {...register('email', {
              required: 'Email is required',
              pattern: { value: /^\S+@\S+\.\S+$/, message: 'Invalid email format' },
            })}
          />
          <Button type="submit" className="w-full" size="lg" icon={KeyRound}>
            Send reset instructions
          </Button>
        </form>
      )}

      <p className="mt-6 text-center text-sm text-gray-500">
        Remembered it?{' '}
        <Link to="/login" className="text-primary-600 font-medium hover:underline">
          Back to sign in
        </Link>
      </p>
    </div>
  )
}
