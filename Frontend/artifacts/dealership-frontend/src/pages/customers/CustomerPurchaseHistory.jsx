import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { Link, useLocation, useParams } from 'react-router-dom'
import { ArrowLeft, Info } from 'lucide-react'
import { getPurchasesByCustomer } from '../../api/purchaseApi'
import Table from '../../components/common/Table'
import { formatCurrency, formatDate, titleCase } from '../../utils/formatters'

const STATUS_STYLES = {
  BOOKED: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
  COMPLETED: 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-300',
  CANCELLED: 'bg-red-50 text-red-700 dark:bg-red-900/30 dark:text-red-300',
}

function StatusBadge({ status }) {
  return (
    <span className={`inline-flex px-2 py-0.5 rounded-full text-xs font-medium ${STATUS_STYLES[status] || 'bg-gray-100 text-gray-600'}`}>
      {titleCase(status)}
    </span>
  )
}

// GET /api/purchases/customer/{customerId} -- see api/purchaseApi.js. Full
// purchase CRUD (create/edit/detail-by-id) arrives with Phase 5; until then
// this is a read-only history list.
export default function CustomerPurchaseHistory() {
  const { id } = useParams()
  const location = useLocation()
  const customer = location.state?.customer

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['purchases', 'byCustomer', id],
    queryFn: () => getPurchasesByCustomer(id),
  })

  const purchases = data || []
  const backTo = customer ? `/customers/${id}` : '/customers/list'

  const columns = [
    { key: 'vehicleName', header: 'Vehicle' },
    { key: 'purchaseDate', header: 'Purchase Date', render: (row) => formatDate(row.purchaseDate) },
    { key: 'deliveryDate', header: 'Delivery Date', render: (row) => formatDate(row.deliveryDate) },
    { key: 'sellingPrice', header: 'Price', render: (row) => formatCurrency(row.sellingPrice) },
    { key: 'paymentMethod', header: 'Payment', render: (row) => titleCase(row.paymentMethod) },
    { key: 'purchaseStatus', header: 'Status', render: (row) => <StatusBadge status={row.purchaseStatus} /> },
  ]

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3">
        <Link to={backTo} className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
          <ArrowLeft className="h-5 w-5" />
        </Link>
        <div>
          <h1 className="text-xl font-semibold">Purchase History</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">
            {customer ? `Purchases made by ${customer.fullName}.` : 'Every purchase on file for this customer.'}
          </p>
        </div>
      </div>

      <div className="card p-4">
        <Table
          columns={columns}
          data={purchases}
          isLoading={isLoading}
          error={error?.message}
          onRetry={refetch}
          emptyLabel="No purchases on file for this customer yet."
        />
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-amber-200 dark:border-amber-900 bg-amber-50 dark:bg-amber-950/30 p-4 text-sm text-amber-800 dark:text-amber-300">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>
          Rows aren't clickable through to a details page yet: there's a GET /purchases/{'{id}'} endpoint, but no
          PurchaseDetails page exists until the Purchase module lands in Phase 5. And even then it couldn't link
          onward to the vehicle itself -- PurchaseResponse only returns vehicleName as text, never a vehicleId.
        </p>
      </div>
    </div>
  )
}
