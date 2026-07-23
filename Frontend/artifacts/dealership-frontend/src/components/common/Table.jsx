import React from 'react'
import Loader from './Loader'
import ErrorMessage from './ErrorMessage'
import { Inbox } from 'lucide-react'

// Generic table. columns: [{ key, header, render?(row) }]
export default function Table({ columns, data = [], isLoading, error, onRetry, emptyLabel = 'No records found', keyField = 'id' }) {
  if (isLoading) return <Loader label="Loading data..." />
  if (error) return <ErrorMessage message={error} onRetry={onRetry} />
  if (!data.length) {
    return (
      <div className="flex flex-col items-center justify-center gap-2 py-14 text-gray-400">
        <Inbox className="h-8 w-8" />
        <p className="text-sm">{emptyLabel}</p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto rounded-lg border border-gray-200 dark:border-gray-800">
      <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-800 text-sm">
        <thead className="bg-gray-50 dark:bg-gray-900">
          <tr>
            {columns.map((col) => (
              <th key={col.key} className="px-4 py-3 text-left font-semibold text-gray-600 dark:text-gray-300 whitespace-nowrap">
                {col.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100 dark:divide-gray-800 bg-white dark:bg-gray-950">
          {data.map((row) => (
            <tr key={row[keyField]} className="hover:bg-gray-50 dark:hover:bg-gray-900/60">
              {columns.map((col) => (
                <td key={col.key} className="px-4 py-3 whitespace-nowrap">
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
