import React from 'react'
import { AlertTriangle, RefreshCw } from 'lucide-react'
import Button from './Button'

export default function ErrorMessage({ message = 'Something went wrong.', onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 rounded-xl border border-red-200 bg-red-50 dark:bg-red-950/30 dark:border-red-900 p-8 text-center">
      <AlertTriangle className="h-8 w-8 text-red-500" />
      <p className="text-sm text-red-700 dark:text-red-300 max-w-md">{message}</p>
      {onRetry && (
        <Button variant="danger" size="sm" icon={RefreshCw} onClick={onRetry}>
          Try again
        </Button>
      )}
    </div>
  )
}
