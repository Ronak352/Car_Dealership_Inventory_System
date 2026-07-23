import React, { useState } from 'react'
import { Bell, LogOut, Menu, Moon, Sun, User } from 'lucide-react'
import { useAuth } from '../../hooks/useAuth'
import { useNavigate } from 'react-router-dom'

export default function Navbar({ onToggleSidebar, darkMode, onToggleDarkMode }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-gray-200 dark:border-gray-800 bg-white/80 dark:bg-gray-950/80 backdrop-blur px-4">
      <div className="flex items-center gap-3">
        <button onClick={onToggleSidebar} className="lg:hidden rounded-lg p-2 hover:bg-gray-100 dark:hover:bg-gray-800">
          <Menu className="h-5 w-5" />
        </button>
        <span className="hidden sm:inline text-sm text-gray-500">Car Dealership Inventory System</span>
      </div>

      <div className="flex items-center gap-2">
        <button onClick={onToggleDarkMode} className="rounded-lg p-2 hover:bg-gray-100 dark:hover:bg-gray-800" title="Toggle theme">
          {darkMode ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
        </button>
        <button className="rounded-lg p-2 hover:bg-gray-100 dark:hover:bg-gray-800" title="Notifications">
          <Bell className="h-5 w-5" />
        </button>

        <div className="relative">
          <button
            onClick={() => setMenuOpen((v) => !v)}
            className="flex items-center gap-2 rounded-lg px-2 py-1.5 hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            <span className="flex h-8 w-8 items-center justify-center rounded-full bg-primary-600 text-white text-sm font-semibold">
              {user?.email?.[0]?.toUpperCase() || <User className="h-4 w-4" />}
            </span>
            <span className="hidden md:flex flex-col items-start leading-tight">
              <span className="text-sm font-medium">{user?.email}</span>
              <span className="text-xs text-gray-500">{user?.role}</span>
            </span>
          </button>

          {menuOpen && (
            <div className="absolute right-0 mt-2 w-48 rounded-lg border border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 shadow-lg py-1">
              <button
                onClick={() => { setMenuOpen(false); navigate('/settings/profile') }}
                className="flex w-full items-center gap-2 px-4 py-2 text-sm hover:bg-gray-50 dark:hover:bg-gray-800"
              >
                <User className="h-4 w-4" /> My Profile
              </button>
              <button
                onClick={handleLogout}
                className="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
              >
                <LogOut className="h-4 w-4" /> Logout
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
