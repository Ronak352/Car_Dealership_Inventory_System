import React, { forwardRef } from 'react'

const Input = forwardRef(function Input(
  { label, error, hint, className = '', containerClassName = '', ...props },
  ref
) {
  return (
    <div className={containerClassName}>
      {label && <label className="label-base">{label}{props.required && <span className="text-red-500"> *</span>}</label>}
      <input
        ref={ref}
        className={`input-base ${error ? 'border-red-400 focus:border-red-500 focus:ring-red-500/30' : ''} ${className}`}
        {...props}
      />
      {hint && !error && <p className="mt-1 text-xs text-gray-500">{hint}</p>}
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  )
})

export default Input
