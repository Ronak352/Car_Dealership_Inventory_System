import React, { forwardRef } from 'react'

const Select = forwardRef(function Select(
  { label, error, options = [], placeholder = 'Select...', className = '', containerClassName = '', ...props },
  ref
) {
  return (
    <div className={containerClassName}>
      {label && <label className="label-base">{label}{props.required && <span className="text-red-500"> *</span>}</label>}
      <select
        ref={ref}
        className={`input-base ${error ? 'border-red-400' : ''} ${className}`}
        {...props}
      >
        <option value="">{placeholder}</option>
        {options.map((opt) => {
          const value = typeof opt === 'object' ? opt.value : opt
          const label = typeof opt === 'object' ? opt.label : opt
          return (
            <option key={value} value={value}>
              {label}
            </option>
          )
        })}
      </select>
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </div>
  )
})

export default Select
