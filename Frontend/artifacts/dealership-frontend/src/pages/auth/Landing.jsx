import React from 'react'
import { Link } from 'react-router-dom'
import { Car, ShieldCheck, Gauge, Users } from 'lucide-react'
import Button from '../../components/common/Button'

export default function Landing() {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      <header className="flex items-center justify-between px-6 py-4 border-b border-gray-200 dark:border-gray-800">
        <div className="flex items-center gap-2 text-xl font-bold text-primary-700 dark:text-primary-400">
          <Car className="h-7 w-7" /> DriveHub
        </div>
        <div className="flex gap-3">
          <Link to="/login"><Button variant="secondary">Login</Button></Link>
          <Link to="/register"><Button>Get Started</Button></Link>
        </div>
      </header>

      <section className="mx-auto max-w-5xl px-6 py-20 text-center">
        <h1 className="text-4xl sm:text-5xl font-extrabold tracking-tight">
          The complete <span className="text-primary-600">Car Dealership</span> Inventory System
        </h1>
        <p className="mt-4 text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
          Manage vehicles, customers, employees, inventory, purchases, payments and test drives
          from a single, secure, role-based dashboard.
        </p>
        <div className="mt-8 flex justify-center gap-4">
          <Link to="/register"><Button size="lg">Create an account</Button></Link>
          <Link to="/login"><Button size="lg" variant="secondary">I already have an account</Button></Link>
        </div>
      </section>

      <section className="mx-auto max-w-5xl grid gap-6 px-6 pb-20 sm:grid-cols-3">
        <div className="card p-6 text-center">
          <ShieldCheck className="mx-auto mb-3 h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Secure by design</h3>
          <p className="mt-1 text-sm text-gray-500">JWT authentication with role-based access for Admins, Managers, Salespeople and Customers.</p>
        </div>
        <div className="card p-6 text-center">
          <Gauge className="mx-auto mb-3 h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Live inventory</h3>
          <p className="mt-1 text-sm text-gray-500">Track stock levels, restocks, sales and low-stock alerts in real time.</p>
        </div>
        <div className="card p-6 text-center">
          <Users className="mx-auto mb-3 h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Built for teams</h3>
          <p className="mt-1 text-sm text-gray-500">Dedicated workflows for customers, employees, purchases, payments and test drives.</p>
        </div>
      </section>
    </div>
  )
}
