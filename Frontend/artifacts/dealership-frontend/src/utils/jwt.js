// Minimal, dependency-free JWT payload decoder (no signature verification --
// verification always happens server-side; this is only used to read claims
// like expiry/role for client-side UX decisions).
export function decodeJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (e) {
    return null
  }
}

export function isTokenExpired(token) {
  const payload = decodeJwt(token)
  if (!payload || !payload.exp) return true
  return Date.now() >= payload.exp * 1000
}
