import axiosClient from './axiosClient'

// Matches com.dealership.controller.InventoryController (backend is
// read-only / locked -- this file only wraps the endpoints exactly as they
// exist).
//
// Known backend behaviors/limitations that shape the Inventory pages:
//
// 1. There is NO "list every vehicle's stock level" endpoint here. Stock
//    quantity lives on VehicleResponse.quantity, which VehicleServiceImpl
//    DOES populate correctly on every vehicle endpoint (unlike category/
//    status, which are always null everywhere -- see vehicleApi.js). So the
//    Inventory pages get their vehicle+quantity data from vehicleApi's
//    getAllVehicles()/searchVehicles(), not from this file, and use this
//    file only for the inventory-specific actions below.
//
// 2. increaseStock / decreaseStock / updateStock all require a
//    `performedByUserId` query param (whoever is making the change), but
//    there's no user-search endpoint to look someone up. In practice this
//    is always the signed-in ADMIN/MANAGER doing the action, so the
//    frontend defaults it to the current session's userId (from
//    useAuth()) rather than asking the user to type their own id -- see
//    StockDetails.jsx / VehicleStockList.jsx.
//
// 3. getLowStockVehicles is the ONE place in the whole backend where
//    VehicleResponse.category and .status actually get populated
//    (InventoryServiceImpl.getLowStockVehicles sets them explicitly) --
//    every other vehicle-returning endpoint leaves them null. That's a real
//    asymmetry, not a frontend bug, so don't be surprised these two fields
//    show up here and nowhere else.
//
// 4. GET /history/{vehicleId} and GET /low-stock are ADMIN, MANAGER only.
//    GET /quantity/{vehicleId} is open to every authenticated role.
//    POST/PUT stock-mutation endpoints are ADMIN, MANAGER only.

// POST /api/inventory/increase/{vehicleId}?quantity=&performedByUserId= --
// ADMIN, MANAGER.
export function increaseStock(vehicleId, quantity, performedByUserId) {
  return axiosClient
    .post(`/inventory/increase/${vehicleId}`, null, { params: { quantity, performedByUserId } })
    .then((res) => res.data)
}

// POST /api/inventory/decrease/{vehicleId}?quantity=&performedByUserId= --
// ADMIN, MANAGER. Backend rejects (400) if quantity requested exceeds what's
// currently in stock.
export function decreaseStock(vehicleId, quantity, performedByUserId) {
  return axiosClient
    .post(`/inventory/decrease/${vehicleId}`, null, { params: { quantity, performedByUserId } })
    .then((res) => res.data)
}

// PUT /api/inventory/update/{vehicleId}?quantity=&performedByUserId= --
// ADMIN, MANAGER. Sets stock to an exact quantity (not a delta).
export function setStock(vehicleId, quantity, performedByUserId) {
  return axiosClient
    .put(`/inventory/update/${vehicleId}`, null, { params: { quantity, performedByUserId } })
    .then((res) => res.data)
}

// GET /api/inventory/history/{vehicleId} -- ADMIN, MANAGER. Newest first.
export function getInventoryHistory(vehicleId) {
  return axiosClient.get(`/inventory/history/${vehicleId}`).then((res) => res.data)
}

// GET /api/inventory/quantity/{vehicleId} -- every authenticated role.
export function getAvailableQuantity(vehicleId) {
  return axiosClient.get(`/inventory/quantity/${vehicleId}`).then((res) => res.data)
}

// GET /api/inventory/low-stock?threshold= -- ADMIN, MANAGER. Backend
// defaults threshold to 5 if omitted; passed explicitly here so callers
// always know what they asked for.
export function getLowStockVehicles(threshold = 5) {
  return axiosClient.get('/inventory/low-stock', { params: { threshold } }).then((res) => res.data)
}
