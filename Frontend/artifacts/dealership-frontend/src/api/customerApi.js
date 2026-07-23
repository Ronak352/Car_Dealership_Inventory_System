import axiosClient from './axiosClient'

// Matches com.dealership.controller.CustomerController (backend is
// read-only / locked -- this file only wraps the endpoints exactly as they
// exist).
//
// Known backend response limitation (CustomerServiceImpl.mapToResponse never
// sets these, on ANY endpoint -- create/get/list/update all return the same
// shape): CustomerRequest requires `city`, `state` and `pincode`, but
// CustomerResponse only ever carries `address` (plus id/fullName/email/
// phone). city/state/pincode are stored server-side but never sent back, so
// the frontend cannot display or filter by them -- see the note surfaced in
// CustomerList/CustomerDashboard. This is a backend limitation, not a
// frontend bug, and per the backend-protection rule it is handled here
// rather than by touching the backend.
//
// Also note: a "customer" here is a profile record (address/city/state/
// pincode) attached to an existing User account with role CUSTOMER --
// created via POST /customers/{userId}, separately from account
// registration. A registered CUSTOMER user who hasn't completed that step
// yet won't show up in this list at all.

// GET /api/customers/{id} -- ADMIN, MANAGER, SALESPERSON, CUSTOMER.
export function getCustomerById(id) {
  return axiosClient.get(`/customers/${id}`).then((res) => res.data)
}

// GET /api/customers/user/{userId} -- ADMIN, MANAGER, SALESPERSON, CUSTOMER.
export function getCustomerByUserId(userId) {
  return axiosClient.get(`/customers/user/${userId}`).then((res) => res.data)
}

// GET /api/customers -- every customer profile. ADMIN, MANAGER only
// (enforced server-side too) -- there is no pagination/sorting/filtering on
// the backend, so the frontend fetches the full list once and handles all
// of that client-side.
export function getAllCustomers() {
  return axiosClient.get('/customers').then((res) => res.data)
}

// POST /api/customers/{userId} -- ADMIN, MANAGER, CUSTOMER.
export function createCustomer(userId, payload) {
  return axiosClient.post(`/customers/${userId}`, payload).then((res) => res.data)
}

// PUT /api/customers/{id} -- ADMIN, MANAGER, CUSTOMER.
export function updateCustomer(id, payload) {
  return axiosClient.put(`/customers/${id}`, payload).then((res) => res.data)
}

// DELETE /api/customers/{id} -- ADMIN only (enforced server-side too).
export function deleteCustomer(id) {
  return axiosClient.delete(`/customers/${id}`).then((res) => res.data)
}
