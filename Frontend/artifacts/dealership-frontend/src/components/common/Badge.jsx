import React from 'react'
import { titleCase } from '../../utils/formatters'

const colorMap = {
  AVAILABLE: 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300',
  SOLD: 'bg-gray-200 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
  RESERVED: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300',
  SERVICE: 'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300',
  SUCCESS: 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300',
  COMPLETED: 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300',
  PENDING: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300',
  BOOKED: 'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300',
  APPROVED: 'bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300',
  REQUESTED: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300',
  FAILED: 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300',
  CANCELLED: 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300',
  REJECTED: 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300',
  // Client-computed stock-level badges (Inventory module) -- not a backend
  // enum, just quantity compared against a threshold, so these are separate
  // from VehicleStatus (AVAILABLE/SOLD/RESERVED/SERVICE above).
  IN_STOCK: 'bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-300',
  LOW_STOCK: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300',
  OUT_OF_STOCK: 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300',
  DEFAULT: 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
}

export default function Badge({ status }) {
  const cls = colorMap[status] || colorMap.DEFAULT
  return <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${cls}`}>{titleCase(status)}</span>
}
