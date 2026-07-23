import React from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { Car, Info } from 'lucide-react'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'

// See ForgotPassword.jsx: there is no backend endpoint to actually perform a
// password reset yet (no /api/auth/reset-password). This page is a real,
// styled UI shell wired for a `token` query param and ready to be connected
// once the backend adds the endpoint -- it intentionally does not fake a
// network call.
export default function ResetPassword() {
  const [params] = useSearchParams()
  const token = params.get('token')

  return (
    <div>
      <Link to="/" className="lg:hidden mb-8 flex items-center gap-2 text-xl font-bold text-primary-700">
        <Car className="h-7 w-7" /> DriveHub
      </Link>

      <h1 className="text-2xl font-bold">Reset your password</h1>
      <p className="mt-1 text-sm text-gray-500">Choose a new password for your account.</p>

      <div className="mt-4 flex gap-2 rounded-lg border border-blue-200 bg-blue-50 dark:bg-blue-950/30 dark:border-blue-900 px-3 py-2 text-xs text-blue-700 dark:text-blue-300">
        <Info className="h-4 w-4 shrink-0 mt-0.5" />
        <span>
          This screen is ready to submit to a reset-password API, but the backend
          doesn't provide one yet, so submission is disabled for now.
          {token ? ` Reset token detected in the URL.` : ''}
        </span>
      </div>

      <form className="mt-6 space-y-4" onSubmit={(e) => e.preventDefault()}>
        <Input label="New password" type="password" placeholder="••••••••" disabled />
        <Input label="Confirm new password" type="password" placeholder="••••••••" disabled />
        <Button type="submit" className="w-full" size="lg" disabled>
          Reset password
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-gray-500">
        <Link to="/login" className="text-primary-600 font-medium hover:underline">
          Back to sign in
        </Link>
      </p>
    </div>
  )
}
