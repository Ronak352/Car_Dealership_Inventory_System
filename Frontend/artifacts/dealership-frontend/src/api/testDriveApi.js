import axiosClient from './axiosClient'

export function createTestDrive(payload) {
  return axiosClient.post('/test-drives', payload).then((res) => res.data)
}

export function getTestDriveById(id) {
  return axiosClient.get(`/test-drives/${id}`).then((res) => res.data)
}

export function getAllTestDrives() {
  return axiosClient.get('/test-drives').then((res) => res.data)
}

export function getTestDrivesByCustomer(customerId) {
  return axiosClient.get(`/test-drives/customer/${customerId}`).then((res) => res.data)
}
