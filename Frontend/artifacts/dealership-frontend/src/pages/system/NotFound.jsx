import React from 'react'
import { Link } from 'react-router-dom'
import { CarFront } from 'lucide-react'
import Button from '../../components/common/Button'

export default function NotFound() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-center px-6">
      <CarFront className="h-16 w-16 text-primary-600" />
      <h1 className="text-5xl font-extrabold">404</h1>
      <p className="text-gray-500 max-w-sm">This page took a wrong turn and drove off the lot. It doesn't exist.</p>
      <Link to="/"><Button>Back to home</Button></Link>
    </div>
  )
}
