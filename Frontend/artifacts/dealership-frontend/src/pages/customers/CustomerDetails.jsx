import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, History, Info, Pencil, Trash2 } from 'lucide-react'
import { deleteCustomer, getCustomerById } from '../../api/customerApi'
import { useAuth } from '../../hooks/useAuth'
import { useToast } from '../../hooks/useToast'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'

// GET /api/customers/{id} is allowed for ADMIN, MANAGER, SALESPERSON and
// CUSTOMER, so this page always fetches by id (location.state, when
// present from a row click, is only used as initialData for instant paint).
export default function CustomerDetails() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { toast } = useToast()

  const { data: customer, isLoading, error, refetch } = useQuery({
    queryKey: ['customers', id],
    queryFn: () => getCustomerById(id),
    initialData: location.state?.customer,
  })

  const [confirmOpen, setConfirmOpen] = useState(false)
  const [deleting, setDeleting] = useState(false)

  const canManage = user?.role === 'ADMIN' || user?.role === 'MANAGER'
  const canDelete = user?.role === 'ADMIN'
  // CustomerResponse has no userId to compare against the signed-in user,
  // so ownership for a CUSTOMER-role viewer is approximated by email match
  // -- the closest identifying field the backend actually returns.
  const isOwnProfile = user?.role === 'CUSTOMER' && customer?.email === user?.email
  const canEdit = canManage || isOwnProfile

  async function handleDelete() {
    setDeleting(true)
    try {
      await deleteCustomer(customer.id)
      toast.success(`${customer.fullName}'s customer profile was deleted.`)
      navigate('/customers/list')
    } catch (err) {
      toast.error(err.message || 'Could not delete customer profile.')
    } finally {
      setDeleting(false)
      setConfirmOpen(false)
    }
  }

  if (isLoading) return <Loader label="Loading customer..." />
  if (error) return <ErrorMessage message={error.message || 'Could not load customer.'} onRetry={refetch} />
  if (!customer) return <ErrorMessage message={`No customer found with id ${id}.`} />

  const fields = [
    { label: 'Full Name', value: customer.fullName },
    { label: 'Email', value: customer.email },
    { label: 'Phone', value: customer.phone || '—' },
    { label: 'Address', value: customer.address || '—' },
  ]

  return (
    <div className="max-w-3xl space-y-4">
      <div className="flex items-center gap-3">
        <Link to="/customers/list" className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">{customer.fullName}</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">{customer.email}</p>
        </div>
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

        <div className="flex flex-wrap justify-end gap-3 pt-6 mt-6 border-t border-gray-100 dark:border-gray-800">
          <Link to={`/customers/${customer.id}/purchases`} state={{ customer }}>
            <Button variant="secondary" icon={History}>
              Purchase History
            </Button>
          </Link>
          {canEdit && (
            <Link to={`/customers/${customer.id}/edit`} state={{ customer }}>
              <Button variant="secondary" icon={Pencil}>
                Edit
              </Button>
            </Link>
          )}
          {canDelete && (
            <Button variant="danger" icon={Trash2} onClick={() => setConfirmOpen(true)}>
              Delete
            </Button>
          )}
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          City, state and pincode aren't shown: the backend accepts them when a profile is created but never
          returns them on any customer response.
        </p>
      </div>

      <Modal
        open={confirmOpen}
        onClose={() => setConfirmOpen(false)}
        title="Delete this customer profile?"
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
          This will permanently remove <strong>{customer.fullName}</strong>'s customer profile ({customer.email}).
          This cannot be undone.
        </p>
      </Modal>
    </div>
  )
}
