import axiosClient from './axiosClient'

// Matches com.dealership.controller.VehicleController (backend is read-only /
// locked -- this file only wraps the endpoints exactly as they exist).
//
// Known backend response limitation (VehicleServiceImpl never sets these on
// VehicleResponse, on ANY endpoint -- add/list/search/update all return the
// same shape): `category` and `status` are declared on VehicleResponse but
// are always null in the JSON that comes back. fuelType, transmission,
// manufacturingYear, color, engineNumber, discount and condition aren't on
// VehicleResponse at all (only on VehicleRequest), so they're never
// returned either. The frontend cannot display or filter by those fields
// from list/search results -- see the note surfaced in VehicleList/
// VehicleDashboard. This is a backend limitation, not a frontend bug, and
// per the backend-protection rule it is handled here rather than by
// touching the backend.
//
// Also note: GET /api/vehicles/search does an EXACT (case-insensitive)
// match on brand/model, not a "contains" search (see VehicleRepository's
// `LOWER(v.brand) = :brand` query). So the frontend does its own
// contains-style filtering client-side against the full fetched list
// instead of relying on the backend for partial text search.

// GET /api/vehicles -- vehicles currently in AVAILABLE status only.
export function getAvailableVehicles() {
  return axiosClient.get('/vehicles').then((res) => res.data)
}

// GET /api/vehicles/search -- optional brand/model/category/minPrice/maxPrice.
// Called with no params, this is the only way to get vehicles across every
// status (AVAILABLE/SOLD/RESERVED/SERVICE), since /vehicles is hard-filtered
// to AVAILABLE server-side.
export function searchVehicles(params = {}) {
  const cleaned = Object.fromEntries(
    Object.entries(params).filter(([, v]) => v !== '' && v !== null && v !== undefined)
  )
  return axiosClient.get('/vehicles/search', { params: cleaned }).then((res) => res.data)
}

// GET the full inventory (every status). Thin convenience wrapper around
// searchVehicles() with no filters, since there's no dedicated "list all" endpoint.
export function getAllVehicles() {
  return searchVehicles({})
}

// POST /api/vehicles -- ADMIN, MANAGER only (enforced server-side too).
export function createVehicle(payload) {
  return axiosClient.post('/vehicles', payload).then((res) => res.data)
}

// PUT /api/vehicles/{id} -- ADMIN, MANAGER only (enforced server-side too).
export function updateVehicle(id, payload) {
  return axiosClient.put(`/vehicles/${id}`, payload).then((res) => res.data)
}

// DELETE /api/vehicles/{id} -- ADMIN only (enforced server-side too).
export function deleteVehicle(id) {
  return axiosClient.delete(`/vehicles/${id}`).then((res) => res.data)
}
