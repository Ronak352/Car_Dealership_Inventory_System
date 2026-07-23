import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { AlertTriangle, ArrowLeft, Save } from 'lucide-react'
import { getEmployeeById, updateEmployee } from '../../api/employeeApi'
import { useToast } from '../../hooks/useToast'
import { EMPLOYEE_ROLES } from '../../utils/constants'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'

// PUT /api/employees/{id} is ADMIN only. Fetches fresh data (location.state,
// when present from a row click, is only used as initialData for an
// instant first paint).
//
// IMPORTANT backend limitation: EmployeeServiceImpl.updateEmployee never
// reads request.getUserId() -- it always keeps the employee's existing
// linked user account, full stop. But EmployeeRequest still declares userId
// as @NotNull, so the payload still needs *a* value or validation fails.
// This form sends the employee's own current userId back (read-only, can't
// be changed here) purely to satisfy that validation -- it has no effect.
//
// Role, on the other hand, IS applied on update (via
// employee.getUser().setRole(...)), so changing it here immediately changes
// that user's account role everywhere in the app, exactly like on create.
export default function EditEmployee() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

  const { data: employee, isLoading, error, refetch } = useQuery({
    queryKey: ['employees', id],
    queryFn: () => getEmployeeById(id),
    initialData: location.state?.employee,
  })

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    values: {
      employeeCode: employee?.employeeCode || '',
      joiningDate: employee?.joiningDate || '',
      salary: employee?.salary ?? '',
      role: employee?.role || '',
    },
  })

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      const payload = {
        userId: employee.userId, // ignored server-side, sent only to satisfy @NotNull
        employeeCode: data.employeeCode.trim(),
        joiningDate: data.joiningDate,
        salary: Number(data.salary),
        role: data.role,
      }
      const updated = await updateEmployee(id, payload)
      toast.success('Employee record updated.')
      navigate(`/employees/${id}`, { state: { employee: updated } })
    } catch (err) {
      toast.error(err.message || 'Could not update employee.')
    } finally {
      setSubmitting(false)
    }
  }

  if (isLoading) return <Loader label="Loading employee..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load employee.'} onRetry={refetch} />
  if (!employee) return <ErrorMessage message={`No employee found with id ${id}.`} />

  return (
    <div className="max-w-xl space-y-4">
      <div className="flex items-center gap-3">
        <Link
          to={`/employees/${id}`}
          state={{ employee }}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
        >
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Edit {employee.fullName}</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{employee.email}</p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <AlertTriangle className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          The linked user account can't be changed here -- the backend ignores that field on update and always
          keeps the employee's existing account. Changing "Role" below, however, does take effect immediately on
          that account, everywhere in the app.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        <Input label="Linked User ID" value={employee.userId} disabled />

        <Input
          label="Employee Code"
          required
          error={errors.employeeCode?.message}
          {...register('employeeCode', { required: 'Employee code is required' })}
        />

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <Input
            label="Joining Date"
            type="date"
            required
            error={errors.joiningDate?.message}
            {...register('joiningDate', { required: 'Joining date is required' })}
          />
          <Input
            label="Salary"
            type="number"
            step="0.01"
            required
            error={errors.salary?.message}
            {...register('salary', {
              required: 'Salary is required',
              min: { value: 0, message: 'Salary cannot be negative' },
            })}
          />
        </div>

        <Select
          label="Role"
          required
          hint="Takes effect on the linked user's account immediately"
          options={EMPLOYEE_ROLES}
          error={errors.role?.message}
          {...register('role', { required: 'Role is required' })}
        />

        <div className="flex justify-end gap-3 pt-2">
          <Link to={`/employees/${id}`} state={{ employee }}>
            <Button type="button" variant="secondary">
              Cancel
            </Button>
          </Link>
          <Button type="submit" icon={Save} loading={submitting}>
            Save Changes
          </Button>
        </div>
      </form>
    </div>
  )
}
