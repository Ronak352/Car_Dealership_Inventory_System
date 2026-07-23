import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { AlertTriangle, ArrowLeft, ShieldCheck } from 'lucide-react'
import { assignEmployeeRole, getEmployeeById } from '../../api/employeeApi'
import { useToast } from '../../hooks/useToast'
import { EMPLOYEE_ROLES } from '../../utils/constants'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Badge from '../../components/common/Badge'

// Dedicated page for PUT /api/employees/{id}/role?role=ROLE (ADMIN only) --
// kept separate from EditEmployee even though EditEmployee's form can also
// change role, because this maps to its own distinct backend endpoint and
// gives a focused, low-risk place to do a role change alone (matches the
// roadmap's AssignRole.jsx as its own page).
//
// EmployeeServiceImpl.assignRole sets employee.getUser().setRole(role)
// directly -- this changes the linked user's account role application-wide
// the moment it's saved, not just something shown on this employee's record.
export default function AssignRole() {
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
    values: { role: employee?.role || '' },
  })

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      const updated = await assignEmployeeRole(id, data.role)
      toast.success(`Role updated to ${data.role}.`)
      navigate(`/employees/${id}`, { state: { employee: updated } })
    } catch (err) {
      toast.error(err.message || 'Could not assign role.')
    } finally {
      setSubmitting(false)
    }
  }

  if (isLoading) return <Loader label="Loading employee..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load employee.'} onRetry={refetch} />
  if (!employee) return <ErrorMessage message={`No employee found with id ${id}.`} />

  return (
    <div className="max-w-lg space-y-4">
      <div className="flex items-center gap-3">
        <Link
          to={`/employees/${id}`}
          state={{ employee }}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200"
        >
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Assign Role</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {employee.fullName} · Currently <Badge status={employee.role} />
          </p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <AlertTriangle className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          This changes {employee.fullName}'s account role application-wide the moment you save -- what they can log
          in and do everywhere, not just a label on this employee record.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        <Select
          label="New Role"
          required
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
          <Button type="submit" icon={ShieldCheck} loading={submitting}>
            Save Role
          </Button>
        </div>
      </form>
    </div>
  )
}
