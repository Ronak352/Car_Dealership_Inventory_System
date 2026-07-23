// Roles exactly as defined in the backend enum com.dealership.enums.Role
export const ROLES = {
  ADMIN: 'ADMIN',
  MANAGER: 'MANAGER',
  SALESPERSON: 'SALESPERSON',
  CUSTOMER: 'CUSTOMER',
}

export const ALL_ROLES = Object.values(ROLES)

// Roles that make sense to assign to a staff/employee record. The backend's
// Role enum (and EmployeeRequest.role / the assign-role endpoint) technically
// accepts CUSTOMER too -- nothing server-side stops it -- but doing so on an
// employee record has no real business meaning, so it's left out of the
// picker used on AddEmployee/EditEmployee/AssignRole rather than removed from
// the backend.
export const EMPLOYEE_ROLES = [ROLES.ADMIN, ROLES.MANAGER, ROLES.SALESPERSON]

// com.dealership.enums.VehicleCategory
export const VEHICLE_CATEGORIES = ['SUV', 'SEDAN', 'HATCHBACK', 'LUXURY', 'ELECTRIC']
// com.dealership.enums.FuelType
export const FUEL_TYPES = ['PETROL', 'DIESEL', 'CNG', 'ELECTRIC', 'HYBRID']
// com.dealership.enums.Transmission
export const TRANSMISSIONS = ['MANUAL', 'AUTOMATIC']
// com.dealership.enums.VehicleCondition
export const VEHICLE_CONDITIONS = ['NEW', 'USED']
// com.dealership.enums.VehicleStatus
export const VEHICLE_STATUSES = ['AVAILABLE', 'SOLD', 'RESERVED', 'SERVICE']
// com.dealership.enums.PaymentMethod
export const PAYMENT_METHODS = ['CASH', 'CARD', 'UPI', 'LOAN']
// com.dealership.enums.PaymentStatus
export const PAYMENT_STATUSES = ['PENDING', 'SUCCESS', 'FAILED']
// com.dealership.enums.PurchaseStatus
export const PURCHASE_STATUSES = ['BOOKED', 'COMPLETED', 'CANCELLED']
// com.dealership.enums.TestDriveStatus
export const TEST_DRIVE_STATUSES = ['REQUESTED', 'APPROVED', 'COMPLETED', 'CANCELLED']
// com.dealership.enums.InventoryOperation
export const INVENTORY_OPERATIONS = ['ADD', 'REMOVE', 'UPDATE']

export const TOKEN_KEY = 'dealership_token'
export const USER_KEY = 'dealership_user'
