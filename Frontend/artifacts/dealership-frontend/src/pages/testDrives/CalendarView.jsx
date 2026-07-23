import React, { useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { getAllTestDrives } from '../../api/testDriveApi'
import Badge from '../../components/common/Badge'
import { formatDateTime } from '../../utils/formatters'
import Loader from '../../components/common/Loader'
import { Link } from 'react-router-dom'

export default function CalendarView() {
  const { data: drives, isLoading } = useQuery({
    queryKey: ['testDrives', 'all'],
    queryFn: getAllTestDrives
  })

  const grouped = useMemo(() => {
    if (!drives) return {}
    const map = {}
    drives.forEach(d => {
      if (!d.scheduledDate) return
      const dateOnly = d.scheduledDate.split('T')[0]
      if (!map[dateOnly]) map[dateOnly] = []
      map[dateOnly].push(d)
    })
    
    Object.keys(map).forEach(k => {
      map[k].sort((a, b) => new Date(a.scheduledDate) - new Date(b.scheduledDate))
    })
    
    return map
  }, [drives])

  const sortedDates = Object.keys(grouped).sort()

  if (isLoading) return <Loader />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold">Calendar View</h1>
        <p className="text-sm text-gray-500">Upcoming test drives organized by date.</p>
      </div>

      {sortedDates.length === 0 ? (
        <div className="card p-10 text-center text-gray-500">No scheduled test drives found.</div>
      ) : (
        <div className="space-y-8">
          {sortedDates.map(date => {
            const dateObj = new Date(date)
            const dateTitle = dateObj.toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
            return (
              <div key={date}>
                <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-200 border-b border-gray-200 dark:border-gray-800 pb-2 mb-4">{dateTitle}</h2>
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                  {grouped[date].map(drive => (
                    <Link key={drive.id} to={`/test-drives/${drive.id}`} className="card p-4 hover:border-primary-500 transition-colors block">
                      <div className="flex justify-between items-start mb-2">
                        <span className="font-semibold text-primary-700 dark:text-primary-400">{formatDateTime(drive.scheduledDate).split(', ')[1]}</span>
                        <Badge status={drive.status} />
                      </div>
                      <p className="font-medium text-gray-900 dark:text-gray-100 truncate">{drive.customerName}</p>
                      <p className="text-sm text-gray-500 truncate">{drive.vehicleName}</p>
                    </Link>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
