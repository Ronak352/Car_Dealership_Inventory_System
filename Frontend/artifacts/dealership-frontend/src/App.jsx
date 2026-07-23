import React from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'

import AuthLayout from './layouts/AuthLayout'
import DashboardLayout from './layouts/DashboardLayout'
import ProtectedRoute from './routes/ProtectedRoute'
import RoleRoute from './routes/RoleRoute'

import Landing from './pages/auth/Landing'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import ForgotPassword from './pages/auth/ForgotPassword'
import ResetPassword from './pages/auth/ResetPassword'

import Dashboard from './pages/dashboard/Dashboard'

import VehicleDashboard from './pages/vehicles/VehicleDashboard'
import VehicleList from './pages/vehicles/VehicleList'
import AddVehicle from './pages/vehicles/AddVehicle'
import EditVehicle from './pages/vehicles/EditVehicle'
import VehicleDetails from './pages/vehicles/VehicleDetails'

import CustomerDashboard from './pages/customers/CustomerDashboard'
import CustomerList from './pages/customers/CustomerList'
import AddCustomer from './pages/customers/AddCustomer'
import EditCustomer from './pages/customers/EditCustomer'
import CustomerDetails from './pages/customers/CustomerDetails'
import CustomerProfile from './pages/customers/CustomerProfile'
import CustomerPurchaseHistory from './pages/customers/CustomerPurchaseHistory'

import EmployeeDashboard from './pages/employees/EmployeeDashboard'
import EmployeeList from './pages/employees/EmployeeList'
import AddEmployee from './pages/employees/AddEmployee'
import EditEmployee from './pages/employees/EditEmployee'
import EmployeeDetails from './pages/employees/EmployeeDetails'
import EmployeeProfile from './pages/employees/EmployeeProfile'
import AssignRole from './pages/employees/AssignRole'

import InventoryDashboard from './pages/inventory/InventoryDashboard'
import StockOverview from './pages/inventory/StockOverview'
import VehicleStockList from './pages/inventory/VehicleStockList'
import StockDetails from './pages/inventory/StockDetails'
import InventoryHistory from './pages/inventory/InventoryHistory'
import LowStockAlert from './pages/inventory/LowStockAlert'

import PurchaseDashboard from './pages/purchases/PurchaseDashboard'
import PurchaseList from './pages/purchases/PurchaseList'
import AddPurchase from './pages/purchases/AddPurchase'
import PurchaseDetails from './pages/purchases/PurchaseDetails'
import PurchaseHistory from './pages/purchases/PurchaseHistory'

import PaymentDashboard from './pages/payments/PaymentDashboard'
import PaymentList from './pages/payments/PaymentList'
import AddPayment from './pages/payments/AddPayment'
import PaymentDetails from './pages/payments/PaymentDetails'
import TransactionHistory from './pages/payments/TransactionHistory'

import TestDriveDashboard from './pages/testDrives/TestDriveDashboard'
import BookingList from './pages/testDrives/BookingList'
import CreateBooking from './pages/testDrives/CreateBooking'
import BookingDetails from './pages/testDrives/BookingDetails'
import CalendarView from './pages/testDrives/CalendarView'

import VehicleReport from './pages/reports/VehicleReport'
import SalesReport from './pages/reports/SalesReport'
import InventoryReport from './pages/reports/InventoryReport'

import MyProfile from './pages/settings/MyProfile'
import ChangePassword from './pages/settings/ChangePassword'

import NotFound from './pages/system/NotFound'
import Forbidden from './pages/system/Forbidden'

export default function App() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<Landing />} />

      <Route element={<AuthLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />
      </Route>

      <Route path="/403" element={<Forbidden />} />

      {/* Protected */}
      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route path="/dashboard" element={<Dashboard />} />

          {/* Vehicle module. Dashboard/list/details are viewable by every
              authenticated role, matching VehicleController's GET endpoints
              (hasAnyRole('ADMIN','MANAGER','SALESPERSON','CUSTOMER')). */}
          <Route path="/vehicles" element={<VehicleDashboard />} />
          <Route path="/vehicles/list" element={<VehicleList />} />
          <Route path="/vehicles/:id" element={<VehicleDetails />} />

          {/* Add/Edit are ADMIN, MANAGER only, matching
              @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") on the
              corresponding POST/PUT endpoints. Delete (ADMIN only) doesn't
              need its own route -- it's a confirm-and-call action inside
              VehicleList/VehicleDetails. */}
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/vehicles/add" element={<AddVehicle />} />
            <Route path="/vehicles/:id/edit" element={<EditVehicle />} />
          </Route>

          {/* Customer module (Phase 2A): dashboard/list only, restricted to
              ADMIN/MANAGER -- matching CustomerController's GET / all-list
              endpoint (hasAnyRole('ADMIN','MANAGER')). SALESPERSON and
              CUSTOMER can look up a single profile by id via the backend,
              but can't list every customer, so they don't get this module in
              the sidebar. */}
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/customers" element={<CustomerDashboard />} />
            <Route path="/customers/list" element={<CustomerList />} />
          </Route>

          {/* Customer module (Phase 2B). Details is reachable by every
              authenticated role, matching GET /api/customers/{id}
              (ADMIN, MANAGER, SALESPERSON, CUSTOMER). Add/Edit are allowed
              for ADMIN, MANAGER and CUSTOMER (CUSTOMER can only create/edit
              via routes this app links them to -- their own profile --
              matching POST/PUT /api/customers, which itself has no
              ownership check server-side). Delete is ADMIN only, gated
              inside CustomerDetails/CustomerList rather than as a route. */}
          <Route path="/customers/:id" element={<CustomerDetails />} />
          <Route path="/customers/:id/purchases" element={<CustomerPurchaseHistory />} />
          <Route path="/customers/profile" element={<CustomerProfile />} />
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER', 'CUSTOMER']} />}>
            <Route path="/customers/add" element={<AddCustomer />} />
            <Route path="/customers/:id/edit" element={<EditCustomer />} />
          </Route>

          {/* Employee module (Phase 3). Dashboard/list/details/search are all
              ADMIN, MANAGER only, matching EmployeeController's GET
              endpoints (hasAnyRole('ADMIN','MANAGER')) -- there is no
              backend endpoint that lets a SALESPERSON view even their own
              employee record, so this whole module is gated off for them
              except the self-service "My Profile" page below, which
              explains that limitation instead of guessing at data. */}
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/employees" element={<EmployeeDashboard />} />
            <Route path="/employees/list" element={<EmployeeList />} />
            <Route path="/employees/:id" element={<EmployeeDetails />} />
          </Route>

          {/* Create/Update/Delete/AssignRole are ADMIN only, matching
              @PreAuthorize("hasRole('ADMIN')") on the corresponding
              POST/PUT/DELETE endpoints. */}
          <Route element={<RoleRoute allowedRoles={['ADMIN']} />}>
            <Route path="/employees/add" element={<AddEmployee />} />
            <Route path="/employees/:id/edit" element={<EditEmployee />} />
            <Route path="/employees/:id/role" element={<AssignRole />} />
          </Route>

          {/* "My Profile" for an employee to view their own record. Open to
              every authenticated role -- the page itself decides what it
              can actually show (ADMIN/MANAGER get a real lookup;
              SALESPERSON sees the backend-limitation notice). */}
          <Route path="/employees/profile" element={<EmployeeProfile />} />

          {/* Inventory module (Phase 4A + 4B). Every page here reads vehicle
              quantity (populated on every vehicle endpoint) and/or the real
              GET /inventory/low-stock, GET /inventory/history/{id} and
              GET /inventory/quantity/{id} endpoints. history/low-stock and
              the increase/decrease/update mutations are ADMIN/MANAGER only
              server-side; quantity is open to every role but there's no
              inventory-facing feature a SALESPERSON/CUSTOMER needs beyond
              what VehicleDetails already shows them (a single vehicle's
              quantity), so this whole module is gated the same way as
              EmployeeController's read endpoints. */}
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/inventory" element={<InventoryDashboard />} />
            <Route path="/inventory/overview" element={<StockOverview />} />
            <Route path="/inventory/stock-list" element={<VehicleStockList />} />
            <Route path="/inventory/low-stock-alerts" element={<LowStockAlert />} />
            <Route path="/inventory/stock/:vehicleId" element={<StockDetails />} />
            <Route path="/inventory/stock/:vehicleId/history" element={<InventoryHistory />} />
          </Route>

          {/* Phase 5+ Modules */}
          <Route path="/purchases" element={<PurchaseDashboard />} />
          <Route path="/purchases/list" element={<PurchaseList />} />
          <Route path="/purchases/add" element={<AddPurchase />} />
          <Route path="/purchases/:id" element={<PurchaseDetails />} />
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/purchases/history" element={<PurchaseHistory />} />
          </Route>

          <Route path="/payments" element={<PaymentDashboard />} />
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/payments/list" element={<PaymentList />} />
          </Route>
          <Route path="/payments/add" element={<AddPayment />} />
          <Route path="/payments/:id" element={<PaymentDetails />} />
          <Route path="/payments/transactions" element={<TransactionHistory />} />

          <Route path="/test-drives" element={<TestDriveDashboard />} />
          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/test-drives/list" element={<BookingList />} />
            <Route path="/test-drives/calendar" element={<CalendarView />} />
          </Route>
          <Route path="/test-drives/book" element={<CreateBooking />} />
          <Route path="/test-drives/:id" element={<BookingDetails />} />

          <Route element={<RoleRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
            <Route path="/reports/vehicles" element={<VehicleReport />} />
            <Route path="/reports/sales" element={<SalesReport />} />
            <Route path="/reports/inventory" element={<InventoryReport />} />
          </Route>

          <Route path="/settings/profile" element={<MyProfile />} />
          <Route path="/settings/change-password" element={<ChangePassword />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
