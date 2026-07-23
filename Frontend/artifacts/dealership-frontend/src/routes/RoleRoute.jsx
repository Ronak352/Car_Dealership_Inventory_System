import React from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

// Restricts nested routes to a specific set of roles (mirrors the backend's
// @PreAuthorize("hasAnyRole(...)") on the corresponding endpoint).
export default function RoleRoute({ allowedRoles }) {
  const { user } = useAuth()

  if (!user || !allowedRoles.includes(user.role)) {
    return <Navigate to="/403" replace />
  }

  return <Outlet />
}
