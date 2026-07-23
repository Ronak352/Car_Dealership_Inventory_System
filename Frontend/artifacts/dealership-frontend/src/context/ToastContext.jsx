import React, { createContext, useCallback, useState } from 'react'
import { CheckCircle2, XCircle, Info, X } from 'lucide-react'

export const ToastContext = createContext(null)

let idCounter = 0

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const showToast = useCallback(
    (message, type = 'info', duration = 4000) => {
      const id = ++idCounter
      setToasts((prev) => [...prev, { id, message, type }])
      if (duration > 0) {
        setTimeout(() => removeToast(id), duration)
      }
      return id
    },
    [removeToast]
  )

  const toast = {
    success: (msg) => showToast(msg, 'success'),
    error: (msg) => showToast(msg, 'error'),
    info: (msg) => showToast(msg, 'info'),
  }

  const icons = {
    success: <CheckCircle2 className="h-5 w-5 text-green-500" />,
    error: <XCircle className="h-5 w-5 text-red-500" />,
    info: <Info className="h-5 w-5 text-blue-500" />,
  }

  const styles = {
    success: 'border-green-200 bg-green-50 dark:bg-green-950/40 dark:border-green-900',
    error: 'border-red-200 bg-red-50 dark:bg-red-950/40 dark:border-red-900',
    info: 'border-blue-200 bg-blue-50 dark:bg-blue-950/40 dark:border-blue-900',
  }

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      <div className="fixed top-4 right-4 z-[100] flex flex-col gap-2 w-80 max-w-[90vw]">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`flex items-start gap-2 rounded-lg border p-3 shadow-lg animate-in fade-in slide-in-from-top-2 ${styles[t.type]}`}
            role="alert"
          >
            {icons[t.type]}
            <p className="flex-1 text-sm text-gray-800 dark:text-gray-100">{t.message}</p>
            <button onClick={() => removeToast(t.id)} className="text-gray-400 hover:text-gray-600">
              <X className="h-4 w-4" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}
