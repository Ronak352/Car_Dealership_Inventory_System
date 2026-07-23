import axios from 'axios'
import { TOKEN_KEY } from '../utils/constants'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8087/api'

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attach the JWT (if present) to every outgoing request.
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Normalize backend error shape (see com.dealership.exception.ErrorResponse:
// { timestamp, status, error, message, path }) into a single `message` string
// so calling code never has to branch on shape.
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const data = error.response?.data

    let message = 'Something went wrong. Please try again.'
    if (data?.message) {
      message = data.message
    } else if (typeof data === 'string' && data.length) {
      message = data
    } else if (error.message === 'Network Error') {
      message = 'Cannot reach the server. Is the backend running on ' + baseURL + '?'
    }

    if (status === 401) {
      // Token missing/invalid/expired -> force re-auth.
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('dealership_user')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login?sessionExpired=1'
      }
    }

    return Promise.reject({ status, message, raw: error })
  }
)

export default axiosClient
