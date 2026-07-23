import React from 'react'
import { Loader2 } from 'lucide-react'

export default function Loader({ label = 'Loading...', fullScreen = false, size = 'md' }) {
  const sizeClass = size === 'lg' ? 'h-10 w-10' : size === 'sm' ? 'h-4 w-4' : 'h-6 w-6'
  const content = (
    <div className="flex flex-col items-center justify-center gap-2 text-gray-500">
      <Loader2 className={`${sizeClass} animate-spin text-primary-600`} />
      {label && <span className="text-sm">{label}</span>}
    </div>
  )

  if (fullScreen) {
    return <div className="fixed inset-0 z-50 flex items-center justify-center bg-white/70 dark:bg-gray-950/70">{content}</div>
  }
  return <div className="flex items-center justify-center py-10">{content}</div>
}
