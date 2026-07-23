import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { Car, UserPlus } from 'lucide-react'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'

export default function Register() {
  const { register: registerUser } = useAuth()
  const { toast } = useToast()
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({
    defaultValues: { firstName: '', lastName: '', email: '', phone: '', password: '', confirmPassword: '' },
  })

  const password = watch('password')

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      // Public sign-up always creates a CUSTOMER account. ADMIN/MANAGER/
      // SALESPERSON accounts are provisioned internally by an ADMIN via the
      // Employees module (see EmployeeController), not through open registration.
      const payload = {
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        phone: data.phone,
        password: data.password,
        role: 'CUSTOMER',
      }
      const response = await registerUser(payload)
      toast.success('Account created! Welcome to DriveHub.')
      navigate('/dashboard', { replace: true })
    } catch (err) {
      toast.error(err.message || 'Registration failed')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div>
      <Link to="/" className="lg:hidden mb-8 flex items-center gap-2 text-xl font-bold text-primary-700">
        <Car className="h-7 w-7" /> DriveHub
      </Link>

      <h1 className="text-2xl font-bold">Create your account</h1>
      <p className="mt-1 text-sm text-gray-500">Sign up as a customer to browse vehicles and book test drives.</p>

      <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="First name"
            required
            error={errors.firstName?.message}
            {...register('firstName', { required: 'First name is required' })}
          />
          <Input
            label="Last name"
            required
            error={errors.lastName?.message}
            {...register('lastName', { required: 'Last name is required' })}
          />
        </div>

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

        <Input
          label="Phone"
          type="tel"
          required
          error={errors.phone?.message}
          {...register('phone', { required: 'Phone number is required' })}
        />

        <Input
          label="Password"
          type="password"
          required
          hint="At least 8 characters"
          error={errors.password?.message}
          {...register('password', {
            required: 'Password is required',
            minLength: { value: 8, message: 'Password must contain at least 8 characters' },
          })}
        />

        <Input
          label="Confirm password"
          type="password"
          required
          error={errors.confirmPassword?.message}
          {...register('confirmPassword', {
            required: 'Please confirm your password',
            validate: (value) => value === password || 'Passwords do not match',
          })}
        />

        <Button type="submit" className="w-full" size="lg" loading={submitting} icon={UserPlus}>
          Create account
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-gray-500">
        Already have an account?{' '}
        <Link to="/login" className="text-primary-600 font-medium hover:underline">
          Sign in
        </Link>
      </p>
    </div>
  )
}
