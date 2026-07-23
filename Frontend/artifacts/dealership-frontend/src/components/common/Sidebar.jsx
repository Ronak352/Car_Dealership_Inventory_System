import React from 'react'
import { NavLink } from 'react-router-dom'
import { LayoutDashboard, Car, Users, UserCircle, Briefcase, Warehouse, X, ShoppingCart, CreditCard, CalendarCheck, BarChart2, Settings } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'

// Centralized nav config. Each new phase appends its module here once the
// corresponding pages/routes exist -- so the sidebar always reflects only
// real, working pages instead of placeholder links.
// `roles: null` means every authenticated role can see the item.
const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard, roles: null },
  // Vehicles (Phase 1A/1B): every role can view, matching VehicleController's
  // GET endpoints (ADMIN, MANAGER, SALESPERSON, CUSTOMER can all browse).
  { to: '/vehicles', label: 'Vehicles', icon: Car, roles: null },
  // Customers (Phase 2A/2B): only ADMIN/MANAGER can list every customer,
  // matching CustomerController's GET / endpoint.
  { to: '/customers', label: 'Customers', icon: Users, roles: ['ADMIN', 'MANAGER'] },
  // My Profile (Phase 2B): a CUSTOMER's own customer record, via
  // GET /api/customers/user/{userId}. ADMIN/MANAGER/SALESPERSON don't get
  // this link -- they manage customer profiles through the Customers list
  // instead, and typically don't have a customer profile of their own.
  { to: '/customers/profile', label: 'My Profile', icon: UserCircle, roles: ['CUSTOMER'] },
  // Employees (Phase 3): only ADMIN/MANAGER can list/view every employee
  // record, matching EmployeeController's GET endpoints
  // (hasAnyRole('ADMIN','MANAGER')).
  { to: '/employees', label: 'Employees', icon: Briefcase, roles: ['ADMIN', 'MANAGER'] },
  // My Employee Profile: shown to SALESPERSON too even though the backend
  // can't actually serve them any data -- the page itself explains why,
  // rather than hiding the link and leaving them wondering if the feature
  // exists at all.
  { to: '/employees/profile', label: 'My Employee Profile', icon: UserCircle, roles: ['SALESPERSON'] },
  // Inventory (Phase 4A): only ADMIN/MANAGER, matching InventoryController's
  // stock-mutation and low-stock endpoints (hasAnyRole('ADMIN','MANAGER')).
  { to: '/inventory', label: 'Inventory', icon: Warehouse, roles: ['ADMIN', 'MANAGER'] },
  { to: '/purchases', label: 'Purchases', icon: ShoppingCart, roles: null },
  { to: '/payments', label: 'Payments', icon: CreditCard, roles: ['ADMIN', 'MANAGER'] },
  { to: '/test-drives', label: 'Test Drives', icon: CalendarCheck, roles: null },
  { to: '/reports/vehicles', label: 'Reports', icon: BarChart2, roles: ['ADMIN', 'MANAGER'] },
  { to: '/settings/profile', label: 'Settings', icon: Settings, roles: null },
]

export default function Sidebar({ open, onClose }) {
  const { user } = useAuth()

  const visibleItems = NAV_ITEMS.filter((item) => !item.roles || item.roles.includes(user?.role))

  return (
    <>
      {open && <div className="fixed inset-0 z-30 bg-black/40 lg:hidden" onClick={onClose} />}
      <aside
        className={`fixed lg:sticky top-0 z-40 h-screen w-64 shrink-0 border-r border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 transition-transform duration-200
        ${open ? 'translate-x-0' : '-translate-x-full'} lg:translate-x-0`}
      >
        <div className="flex h-16 items-center justify-between px-5 border-b border-gray-200 dark:border-gray-800">
          <div className="flex items-center gap-2 font-semibold text-primary-700 dark:text-primary-400">
            <Car className="h-6 w-6" />
            <span>DriveHub</span>
          </div>
          <button onClick={onClose} className="lg:hidden text-gray-400 hover:text-gray-600">
            <X className="h-5 w-5" />
          </button>
        </div>

        <nav className="flex flex-col gap-1 p-3">
          {visibleItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary-50 text-primary-700 dark:bg-primary-900/30 dark:text-primary-300'
                    : 'text-gray-600 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-800'
                }`
              }
              onClick={onClose}
            >
              <Icon className="h-5 w-5" />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="absolute bottom-0 w-full border-t border-gray-200 dark:border-gray-800 p-4 text-xs text-gray-400">
          App modules are fully loaded.
        </div>
      </aside>
    </>
  )
}
