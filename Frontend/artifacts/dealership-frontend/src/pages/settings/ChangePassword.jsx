import React from 'react'
import Input from '../../components/common/Input'
import Button from '../../components/common/Button'
import { Info } from 'lucide-react'

export default function ChangePassword() {
  return (
    <div className="max-w-xl space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Change Password</h1>
        <p className="text-sm text-gray-500">Update your account credentials.</p>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 bg-amber-50 dark:bg-amber-900/30 p-4 text-sm text-amber-800 dark:text-amber-200">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>Password change functionality is not available. This backend does not expose a password-change endpoint.</p>
      </div>

      <form className="card p-5 space-y-4 opacity-60" onSubmit={(e) => e.preventDefault()}>
        <Input label="Current Password" type="password" disabled value="••••••••" />
        <Input label="New Password" type="password" disabled value="••••••••" />
        <Input label="Confirm New Password" type="password" disabled value="••••••••" />
        <div className="pt-4 flex justify-end">
          <Button disabled>Update Password</Button>
        </div>
      </form>
    </div>
  )
}
