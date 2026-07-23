import React from 'react'
import { Link } from 'react-router-dom'
import { ShieldAlert } from 'lucide-react'
import Button from '../../components/common/Button'

export default function Forbidden() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-center px-6">
      <ShieldAlert className="h-16 w-16 text-red-500" />
      <h1 className="text-5xl font-extrabold">403</h1>
      <p className="text-gray-500 max-w-sm">Your role doesn't have permission to view this page.</p>
      <Link to="/dashboard"><Button>Back to dashboard</Button></Link>
    </div>
  )
}
