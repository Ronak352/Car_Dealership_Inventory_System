import React, { createContext, useCallback, useEffect, useMemo, useState } from 'react'
import { loginUser, registerUser } from '../api/authApi'
import { TOKEN_KEY, USER_KEY } from '../utils/constants'
import { isTokenExpired } from '../utils/jwt'

export const AuthContext = createContext(null)

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser)
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY))
  const [initializing, setInitializing] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem(TOKEN_KEY)
    if (storedToken && isTokenExpired(storedToken)) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      setToken(null)
      setUser(null)
    }
    setInitializing(false)
  }, [])

  const persistSession = useCallback((authResponse) => {
    // authResponse: { userId, email, role, token }
    const nextUser = {
      userId: authResponse.userId,
      email: authResponse.email,
      role: authResponse.role,
    }
    localStorage.setItem(TOKEN_KEY, authResponse.token)
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
    setToken(authResponse.token)
    setUser(nextUser)
  }, [])

  const login = useCallback(
    async (credentials) => {
      const response = await loginUser(credentials)
      persistSession(response)
      return response
    },
    [persistSession]
  )

  const register = useCallback(
    async (payload) => {
      const response = await registerUser(payload)
      persistSession(response)
      return response
    },
    [persistSession]
  )

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setToken(null)
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token && user),
      initializing,
      login,
      register,
      logout,
    }),
    [user, token, initializing, login, register, logout]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
