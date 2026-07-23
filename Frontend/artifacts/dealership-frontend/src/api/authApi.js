import axiosClient from './axiosClient'

// Matches AuthController: POST /api/auth/register, POST /api/auth/login
// Both are public (no bearer token needed) and return an AuthResponse:
// { userId, email, role, token }

export function registerUser(payload) {
  // payload: { firstName, lastName, email, phone, password, role }
  return axiosClient.post('/auth/register', payload).then((res) => res.data)
}

export function loginUser(payload) {
  // payload: { email, password }
  return axiosClient.post('/auth/login', payload).then((res) => res.data)
}
