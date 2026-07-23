import React from 'react'
import { Outlet, Link } from 'react-router-dom'
import { Car } from 'lucide-react'

export default function AuthLayout() {
  return (
    <div className="min-h-screen grid lg:grid-cols-2">
      <div className="hidden lg:flex flex-col justify-between bg-gradient-to-br from-primary-700 to-primary-900 text-white p-10">
        <Link to="/" className="flex items-center gap-2 text-xl font-bold">
          <Car className="h-7 w-7" /> DriveHub
        </Link>
        <div>
          <h2 className="text-3xl font-bold mb-3">Run your dealership from one place.</h2>
          <p className="text-primary-100 max-w-md">
            Manage vehicle inventory, customers, employees, purchases, payments and test drives —
            all backed by a secure, role-based Spring Boot API.
          </p>
        </div>
        <p className="text-xs text-primary-200">© {new Date().getFullYear()} DriveHub Inventory System</p>
      </div>
      <div className="flex items-center justify-center p-6 sm:p-10">
        <div className="w-full max-w-md">
          <Outlet />
        </div>
      </div>
    </div>
  )
}
