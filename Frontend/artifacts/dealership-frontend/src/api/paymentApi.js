import axiosClient from './axiosClient'

export function createPayment(payload) {
  return axiosClient.post('/payments', payload).then((res) => res.data)
}

export function getPaymentById(id) {
  return axiosClient.get(`/payments/${id}`).then((res) => res.data)
}

export function getAllPayments() {
  return axiosClient.get('/payments').then((res) => res.data)
}

export function getPaymentsByPurchase(purchaseId) {
  return axiosClient.get(`/payments/purchase/${purchaseId}`).then((res) => res.data)
}

export function updatePaymentStatus(id, status) {
  return axiosClient.put(`/payments/${id}/status`, null, { params: { status } }).then((res) => res.data)
}
