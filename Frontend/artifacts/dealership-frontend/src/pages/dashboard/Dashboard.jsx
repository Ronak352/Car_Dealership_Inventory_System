import React from 'react'
import { useAuth } from '../../hooks/useAuth'
import { useQuery } from '@tanstack/react-query'
import { getAllVehicles } from '../../api/vehicleApi'
import { getLowStockVehicles } from '../../api/inventoryApi'
import { getAllCustomers, getCustomerByUserId } from '../../api/customerApi'
import { getAllEmployees } from '../../api/employeeApi'
import { getPurchasesByCustomer, getPurchasesBySalesperson } from '../../api/purchaseApi'
import { getTestDrivesByCustomer } from '../../api/testDriveApi'
import { Car, Users, Briefcase, Warehouse, Calendar, CreditCard, ShoppingCart } from 'lucide-react'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import { Link } from 'react-router-dom'
import Loader from '../../components/common/Loader'

export default function Dashboard() {
  const { user } = useAuth()
  const role = user?.role

  if (role === 'ADMIN') return <AdminDashboard />
  if (role === 'MANAGER') return <ManagerDashboard />
  if (role === 'SALESPERSON') return <SalespersonDashboard />
  return <CustomerDashboard />
}

function AdminDashboard() {
  const { data: v, isLoading: lv } = useQuery({ queryKey: ['vehicles', 'all'], queryFn: getAllVehicles })
  const { data: ls, isLoading: lls } = useQuery({ queryKey: ['inventory', 'low-stock'], queryFn: () => getLowStockVehicles(5) })
  const { data: c, isLoading: lc } = useQuery({ queryKey: ['customers', 'all'], queryFn: getAllCustomers })
  const { data: e, isLoading: le } = useQuery({ queryKey: ['employees', 'all'], queryFn: getAllEmployees })

  const loading = lv || lls || lc || le
  if (loading) return <Loader />

  const placeholderChart = [
    { name: 'Cash', count: 12 },
    { name: 'Card', count: 19 },
    { name: 'UPI', count: 5 },
    { name: 'Loan', count: 8 },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Admin Overview</h1>
        <p className="text-gray-500 mt-1">Full system status and metrics.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Car className="h-4 w-4" /> Vehicles</div>
          <span className="text-2xl font-bold">{v?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Warehouse className="h-4 w-4" /> Low Stock</div>
          <span className="text-2xl font-bold text-amber-600">{ls?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Users className="h-4 w-4" /> Customers</div>
          <span className="text-2xl font-bold">{c?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Briefcase className="h-4 w-4" /> Employees</div>
          <span className="text-2xl font-bold">{e?.length || 0}</span>
        </div>
      </div>

      <div className="card p-5">
        <h2 className="text-lg font-semibold mb-4">Purchases by Method (Simulated)</h2>
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={placeholderChart}>
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip cursor={{ fill: 'transparent' }} />
              <Bar dataKey="count" fill="#2563eb" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}

function ManagerDashboard() {
  const { data: v, isLoading: lv } = useQuery({ queryKey: ['vehicles', 'all'], queryFn: getAllVehicles })
  const { data: ls, isLoading: lls } = useQuery({ queryKey: ['inventory', 'low-stock'], queryFn: () => getLowStockVehicles(5) })
  const { data: c, isLoading: lc } = useQuery({ queryKey: ['customers', 'all'], queryFn: getAllCustomers })

  if (lv || lls || lc) return <Loader />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Manager Overview</h1>
        <p className="text-gray-500 mt-1">Inventory and customer metrics.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Car className="h-4 w-4" /> Vehicles</div>
          <span className="text-2xl font-bold">{v?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Warehouse className="h-4 w-4" /> Low Stock</div>
          <span className="text-2xl font-bold text-amber-600">{ls?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Users className="h-4 w-4" /> Customers</div>
          <span className="text-2xl font-bold">{c?.length || 0}</span>
        </div>
      </div>
      
      <div className="grid sm:grid-cols-2 gap-4">
        <Link to="/purchases" className="card p-5 hover:border-primary-500 transition-colors flex items-center gap-4">
          <div className="p-3 bg-primary-100 text-primary-600 rounded-lg dark:bg-primary-900/40 dark:text-primary-300"><ShoppingCart /></div>
          <div><h3 className="font-semibold text-lg">Purchases</h3><p className="text-sm text-gray-500">Manage all transactions</p></div>
        </Link>
        <Link to="/test-drives" className="card p-5 hover:border-primary-500 transition-colors flex items-center gap-4">
          <div className="p-3 bg-primary-100 text-primary-600 rounded-lg dark:bg-primary-900/40 dark:text-primary-300"><Calendar /></div>
          <div><h3 className="font-semibold text-lg">Test Drives</h3><p className="text-sm text-gray-500">Review upcoming bookings</p></div>
        </Link>
      </div>
    </div>
  )
}

function SalespersonDashboard() {
  const { user } = useAuth()
  const { data: p, isLoading: lp } = useQuery({ queryKey: ['purchases', 'salesperson', user.userId], queryFn: () => getPurchasesBySalesperson(user.userId) })

  if (lp) return <Loader />
  
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Sales Overview</h1>
        <p className="text-gray-500 mt-1">Your recent activity.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><ShoppingCart className="h-4 w-4" /> Your Assigned Purchases</div>
          <span className="text-2xl font-bold">{p?.length || 0}</span>
        </div>
      </div>
      
      <div className="bg-blue-50 dark:bg-blue-900/30 p-4 rounded-xl border border-blue-200 dark:border-blue-800 text-sm text-blue-800 dark:text-blue-200">
        <p><strong>Note:</strong> Test drives and purchases are assigned via Employee ID. Ensure you provide your ID ({user.userId}) to customers when they book, or use it when entering data manually.</p>
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <Link to="/purchases/add" className="card p-5 hover:border-primary-500 transition-colors flex items-center gap-4">
          <div className="p-3 bg-primary-100 text-primary-600 rounded-lg dark:bg-primary-900/40 dark:text-primary-300"><CreditCard /></div>
          <div><h3 className="font-semibold text-lg">New Purchase</h3><p className="text-sm text-gray-500">Record a new sale</p></div>
        </Link>
        <Link to="/test-drives/book" className="card p-5 hover:border-primary-500 transition-colors flex items-center gap-4">
          <div className="p-3 bg-primary-100 text-primary-600 rounded-lg dark:bg-primary-900/40 dark:text-primary-300"><Calendar /></div>
          <div><h3 className="font-semibold text-lg">Book Test Drive</h3><p className="text-sm text-gray-500">Schedule for a customer</p></div>
        </Link>
      </div>
    </div>
  )
}

function CustomerDashboard() {
  const { user } = useAuth()
  const { data: c, isLoading: lc } = useQuery({ queryKey: ['customers', 'user', user.userId], queryFn: () => getCustomerByUserId(user.userId) })
  const { data: p, isLoading: lp } = useQuery({ queryKey: ['purchases', 'customer', user.userId], queryFn: () => getPurchasesByCustomer(user.userId) })
  const { data: td, isLoading: ltd } = useQuery({ queryKey: ['testDrives', 'customer', user.userId], queryFn: () => getTestDrivesByCustomer(user.userId) })

  if (lc || lp || ltd) return <Loader />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Welcome back{c?.fullName ? `, ${c.fullName}` : ''}</h1>
        <p className="text-gray-500 mt-1">Manage your vehicles and appointments.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><ShoppingCart className="h-4 w-4" /> My Purchases</div>
          <span className="text-2xl font-bold">{p?.length || 0}</span>
        </div>
        <div className="stat-card">
          <div className="flex items-center gap-2 text-gray-500 mb-2"><Calendar className="h-4 w-4" /> Test Drives</div>
          <span className="text-2xl font-bold">{td?.length || 0}</span>
        </div>
      </div>

      <div className="grid sm:grid-cols-3 gap-4 mt-6">
        <Link to="/vehicles" className="card p-5 hover:border-primary-500 transition-colors flex flex-col items-center text-center gap-2">
          <Car className="h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Browse Cars</h3>
        </Link>
        <Link to="/test-drives/book" className="card p-5 hover:border-primary-500 transition-colors flex flex-col items-center text-center gap-2">
          <Calendar className="h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Book Test Drive</h3>
        </Link>
        <Link to="/purchases" className="card p-5 hover:border-primary-500 transition-colors flex flex-col items-center text-center gap-2">
          <ShoppingCart className="h-8 w-8 text-primary-600" />
          <h3 className="font-semibold">Purchase History</h3>
        </Link>
      </div>
    </div>
  )
}
