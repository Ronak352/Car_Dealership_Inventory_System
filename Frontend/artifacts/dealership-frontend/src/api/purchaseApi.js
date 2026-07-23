import axiosClient from './axiosClient'

// Partial wrapper around com.dealership.controller.PurchaseController.

export function getPurchasesByCustomer(customerId) {
  return axiosClient.get(`/purchases/customer/${customerId}`).then((res) => res.data)
}

export function createPurchase(payload) {
  return axiosClient.post('/purchases', payload).then((res) => res.data)
}

export function getPurchaseById(id) {
  return axiosClient.get(`/purchases/${id}`).then((res) => res.data)
}

export function getPurchasesByVehicle(vehicleId) {
  return axiosClient.get(`/purchases/vehicle/${vehicleId}`).then((res) => res.data)
}

export function getPurchasesBySalesperson(salespersonId) {
  return axiosClient.get(`/purchases/salesperson/${salespersonId}`).then((res) => res.data)
}
