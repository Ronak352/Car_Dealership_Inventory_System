import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { Link, useNavigate } from 'react-router-dom'
import { ArrowLeft, Info, PlusCircle } from 'lucide-react'
import { createEmployee } from '../../api/employeeApi'
import { useToast } from '../../hooks/useToast'
import { EMPLOYEE_ROLES } from '../../utils/constants'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'

// Mirrors com.dealership.dto.request.EmployeeRequest against POST
// /api/employees, which is ADMIN only.
//
// Just like AddCustomer, there is no backend endpoint to list/search user
// accounts -- only /api/auth/register creates one (returning its userId
// once, at registration time). So the target user's id has to be entered
// by hand here. This is a real backend limitation, surfaced below rather
// than worked around.
//
// Also worth knowing: EmployeeServiceImpl.createEmployee calls
// user.setRole(request.getRole()) on the linked account, so the role picked
// here immediately becomes that person's account role everywhere in the
// app -- not just a label on this employee record.
export default function AddEmployee() {
  const navigate = useNavigate()
  const { toast } = useToast()
  const [submitting, setSubmitting] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      userId: '',
      employeeCode: '',
      joiningDate: '',
      salary: '',
      role: '',
    },
  })

  const onSubmit = async (data) => {
    setSubmitting(true)
    try {
      const payload = {
        userId: Number(data.userId),
        employeeCode: data.employeeCode.trim(),
        joiningDate: data.joiningDate,
        salary: Number(data.salary),
        role: data.role,
      }
      const created = await createEmployee(payload)
      toast.success('Employee record created.')
      navigate(`/employees/${created.id}`, { state: { employee: created } })
    } catch (err) {
      // 404 -> no user with that id; 409 -> employee code already in use.
      // Both are already normalized as err.message by the axios interceptor.
      toast.error(err.message || 'Could not create employee.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to="/employees/list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Add Employee</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Attach a staff record to an existing user account.</p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          There's no backend API to search or list user accounts, so you'll need to know the target user's id
          already (for example from when they registered). If the id doesn't exist, this will show an error rather
          than create a record. Also, the role you pick here immediately becomes that user's account role
          everywhere -- it isn't just a label on this employee record.
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="card p-6 space-y-5">
        <Input
          label="User ID"
          type="number"
          required
          hint="The id of the already-registered user account this employee record links to"
          error={errors.userId?.message}
          {...register('userId', {
            required: 'User id is required',
            min: { value: 1, message: 'Must be a valid id' },
          })}
        />

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
          hint="This becomes the linked user's account role"
          options={EMPLOYEE_ROLES}
          error={errors.role?.message}
          {...register('role', { required: 'Role is required' })}
        />

        <div className="flex justify-end gap-3 pt-2">
          <Link to="/employees/list">
            <Button type="button" variant="secondary">
              Cancel
            </Button>
          </Link>
          <Button type="submit" icon={PlusCircle} loading={submitting}>
            Save Employee
          </Button>
        </div>
      </form>
    </div>
  )
}
