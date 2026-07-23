import React from 'react'

export default function Footer() {
  return (
    <footer className="border-t border-gray-200 dark:border-gray-800 px-4 py-3 text-center text-xs text-gray-400">
      © {new Date().getFullYear()} DriveHub Car Dealership Inventory System. Phase 1 build.
    </footer>
  )
}
