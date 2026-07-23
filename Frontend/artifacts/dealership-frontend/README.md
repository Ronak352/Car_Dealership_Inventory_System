# Car Dealership Inventory System

A comprehensive frontend for managing a car dealership. 

## Features
- **Vehicles:** Manage car inventory, view details.
- **Customers:** Manage customer profiles and history.
- **Employees:** Manage staff and roles.
- **Inventory:** Track stock levels, low-stock alerts, and stock adjustments.
- **Purchases:** Process and view car purchases.
- **Payments:** Manage payment transactions.
- **Test Drives:** Book and track test drives.
- **Reports:** View analytics on vehicles, sales, and inventory.

## Roles
- `ADMIN`: Full access to all modules.
- `MANAGER`: Manage inventory, oversee purchases, and view reports.
- `SALESPERSON`: Create purchases, test drives, and view assigned entities.
- `CUSTOMER`: Browse cars, book test drives, view purchase history.

## Setup
1. Install dependencies: `npm install`
2. Configure `.env`: Set `VITE_API_BASE_URL` (default: `http://localhost:8087/api`)
3. Run the app: `npm run dev`

## Deployment
Build for production with `npm run build`. You can use the included `Dockerfile` to deploy with Nginx.
