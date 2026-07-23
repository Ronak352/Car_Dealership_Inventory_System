import React from 'react'
import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getTestDriveById } from '../../api/testDriveApi'
import Badge from '../../components/common/Badge'
import Loader from '../../components/common/Loader'
import ErrorMessage from '../../components/common/ErrorMessage'
import { formatDateTime } from '../../utils/formatters'
import { Info } from 'lucide-react'

export default function BookingDetails() {
  const { id } = useParams()
  
  const { data: drive, isLoading, error } = useQuery({
    queryKey: ['testDrives', id],
    queryFn: () => getTestDriveById(id)
  })

  if (isLoading) return <Loader />
  if (error) return <ErrorMessage message="Failed to load test drive details" />
  if (!drive) return <ErrorMessage message="Test drive not found" />

  return (
    <div className="max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Test Drive #{drive.id}</h1>
          <p className="text-sm text-gray-500">Booking details</p>
        </div>
        <Badge status={drive.status} />
      </div>

      <div className="card p-5 grid grid-cols-2 gap-y-4 gap-x-8">
        <div>
          <span className="text-sm text-gray-500">Customer</span>
          <p className="font-medium">{drive.customerName}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Vehicle</span>
          <p className="font-medium">{drive.vehicleName}</p>
        </div>
        <div>
          <span className="text-sm text-gray-500">Scheduled For</span>
          <p className="font-medium">{formatDateTime(drive.scheduledDate)}</p>
        </div>
        <div className="col-span-2">
          <span className="text-sm text-gray-500">Notes</span>
          <p className="font-medium whitespace-pre-wrap">{drive.notes || 'None'}</p>
        </div>
      </div>

      <div className="flex items-start gap-3 rounded-xl border border-blue-200 bg-blue-50 dark:bg-blue-900/30 p-4 text-sm text-blue-800 dark:text-blue-200">
        <Info className="h-5 w-5 shrink-0 mt-0.5" />
        <p>Status updates are managed server-side and are not available through this interface. The system updates statuses automatically based on external events.</p>
      </div>
    </div>
  )
}
