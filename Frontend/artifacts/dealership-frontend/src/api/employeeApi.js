import axiosClient from './axiosClient'

// Matches com.dealership.controller.EmployeeController (backend is
// read-only / locked -- this file only wraps the endpoints exactly as they
// exist).
//
// Known backend behaviors/limitations that shape the Employees pages:
//
// 1. There is no endpoint to list/search User accounts anywhere in this
//    backend (only POST /api/auth/register creates one, and it returns the
//    new userId once). EmployeeRequest requires a userId, so creating an
//    employee record means the ADMIN has to already know the target user's
//    id (e.g. from when that person registered) -- see AddEmployee.jsx.
//
// 2. EmployeeServiceImpl.createEmployee/updateEmployee/assignRole all call
//    user.setRole(...) on the linked User entity -- so the "role" on an
//    employee is really the underlying account's role. Assigning a role
//    here changes what that person can log in and do everywhere else in
//    the app, not just an employee-module label.
//
// 3. EmployeeServiceImpl.updateEmployee never reads request.getUserId() --
//    it always keeps the employee's existing linked user. EmployeeRequest
//    still declares userId as @NotNull though, so PUT still requires a
//    (now-ignored) value in the payload -- see EditEmployee.jsx.
//
// 4. GET /{id}, GET / (list) and GET /search are ADMIN, MANAGER only
//    (hasAnyRole('ADMIN','MANAGER')) -- a SALESPERSON employee has no
//    backend-permitted way to view even their own employee record. There's
//    no /employees/user/{userId} self-lookup endpoint like the customer
//    module has. EmployeeProfile.jsx surfaces this rather than pretending
//    to support it.
//
// 5. Create/Update/Delete/AssignRole are ADMIN only (hasRole('ADMIN')).
//
// 6. No pagination/sorting on the backend for the list endpoint -- only a
//    single free-text /search?keyword= endpoint (matches employeeCode /
//    first name / last name / email). EmployeeList.jsx uses that endpoint
//    directly for searching, and getAllEmployees() otherwise, then handles
//    sorting/pagination client-side.

// GET /api/employees/{id} -- ADMIN, MANAGER.
export function getEmployeeById(id) {
  return axiosClient.get(`/employees/${id}`).then((res) => res.data)
}

// GET /api/employees -- every employee record. ADMIN, MANAGER.
export function getAllEmployees() {
  return axiosClient.get('/employees').then((res) => res.data)
}

// GET /api/employees/search?keyword= -- ADMIN, MANAGER. Matches
// employeeCode / first name / last name / email, case-insensitive,
// server-side.
export function searchEmployees(keyword) {
  return axiosClient.get('/employees/search', { params: { keyword } }).then((res) => res.data)
}

// POST /api/employees -- ADMIN only.
export function createEmployee(payload) {
  return axiosClient.post('/employees', payload).then((res) => res.data)
}

// PUT /api/employees/{id} -- ADMIN only.
export function updateEmployee(id, payload) {
  return axiosClient.put(`/employees/${id}`, payload).then((res) => res.data)
}

// DELETE /api/employees/{id} -- ADMIN only.
export function deleteEmployee(id) {
  return axiosClient.delete(`/employees/${id}`).then((res) => res.data)
}

// PUT /api/employees/{id}/role?role=ROLE -- ADMIN only. `role` is sent as a
// query param (not a JSON body) to match @RequestParam Role role.
export function assignEmployeeRole(id, role) {
  return axiosClient.put(`/employees/${id}/role`, null, { params: { role } }).then((res) => res.data)
}
