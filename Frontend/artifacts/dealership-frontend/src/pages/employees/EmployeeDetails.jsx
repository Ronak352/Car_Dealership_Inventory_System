import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, Pencil, ShieldQuestion, Trash2 } from 'lucide-react'
import { deleteEmployee, getEmployeeById } from '../../api/employeeApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import { formatCurrency, formatDate } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'
import Badge from '../../components/common/Badge'

// GET /api/employees/{id} is ADMIN, MANAGER only, so this page is only
// reachable by those roles (see the RoleRoute wrapping /employees/:id in
// App.jsx). location.state, when present from a row click, is only used as
// initialData for instant paint.
export default function EmployeeDetails() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { toast } = useToast()

  const { data: employee, isLoading, error, refetch } = useQuery({
    queryKey: ['employees', id],
    queryFn: () => getEmployeeById(id),
    initialData: location.state?.employee,
  })

  const [confirmOpen, setConfirmOpen] = useState(false)
  const [deleting, setDeleting] = useState(false)

  const canManage = user?.role === 'ADMIN'

  async function handleDelete() {
    setDeleting(true)
    try {
      await deleteEmployee(employee.id)
      toast.success(`${employee.fullName}'s employee record was deleted.`)
      navigate('/employees/list')
    } catch (err) {
      toast.error(err.message || 'Could not delete employee.')
    } finally {
      setDeleting(false)
      setConfirmOpen(false)
    }
  }

  if (isLoading) return <Loader label="Loading employee..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load employee.'} onRetry={refetch} />
  if (!employee) return <ErrorMessage message={`No employee found with id ${id}.`} />

  const fields = [
    { label: 'Employee Code', value: employee.employeeCode },
    { label: 'Full Name', value: employee.fullName },
    { label: 'Email', value: employee.email },
    { label: 'Phone', value: employee.phone || '—' },
    { label: 'Linked User ID', value: employee.userId },
    { label: 'Joining Date', value: formatDate(employee.joiningDate) },
    { label: 'Salary', value: formatCurrency(employee.salary) },
  ]

  return (
    <div className="max-w-3xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to="/employees/list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">{employee.fullName}</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{employee.email}</p>
        </div>
        <Badge status={employee.role} />
      </div>

      <div className="card p-6">
        <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-6 gap-y-4">
          {fields.map((f) => (
            <div key={f.label}>
              <dt className="text-xs uppercase tracking-wide text-gray-400">{f.label}</dt>
              <dd className="text-sm font-medium mt-0.5">{f.value}</dd>
            </div>
          ))}
        </dl>

        {canManage && (
          <div className="flex flex-wrap justify-end gap-3 pt-6 mt-6 border-t border-gray-100 dark:border-gray-800">
            <Link to={`/employees/${employee.id}/role`} state={{ employee }}>
              <Button variant="secondary" icon={ShieldQuestion}>
                Assign Role
              </Button>
            </Link>
            <Link to={`/employees/${employee.id}/edit`} state={{ employee }}>
              <Button variant="secondary" icon={Pencil}>
                Edit
              </Button>
            </Link>
            <Button variant="danger" icon={Trash2} onClick={() => setConfirmOpen(true)}>
              Delete
            </Button>
          </div>
        )}
      </div>

      <Modal
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        title="Delete this employee record?"
        footer={
          <>
            <Button variant="secondary" onClick={() => setConfirmOpen(false)}>
              Cancel
            </Button>
            <Button variant="danger" loading={deleting} onClick={handleDelete}>
              Delete
            </Button>
          </>
        }
      >
        <p className="text-sm text-gray-600 dark:text-gray-300">
          This will permanently remove <strong>{employee.fullName}</strong>'s employee record ({employee.employeeCode}
          ). This does not delete their underlying user account. This cannot be undone.
        </p>
      </Modal>
    </div>
  )
}
