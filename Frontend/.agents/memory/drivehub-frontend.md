---
name: DriveHub Frontend Architecture
description: Key decisions, constraints, and quirks for artifacts/dealership-frontend (React + Vite + Tailwind v4).
---

## Tailwind v4 @apply constraint
In Tailwind v4 (`@tailwindcss/vite`), `@apply` inside `@layer components` cannot reference custom CSS class names (e.g. `@apply card` fails with "Cannot apply unknown utility class"). All component classes in `index.css` must be written as plain CSS — no `@apply` that references another custom class. Tailwind utility classes (e.g. `bg-white`, `rounded-xl`) are fine to `@apply`.

**Why:** Tailwind v4 resolves `@apply` arguments as utility tokens, not CSS class selectors. Custom classes defined in `@layer components` are not utility tokens.

**How to apply:** Whenever adding a new composite component class that would reuse another custom class (like `.stat-card { @apply card ... }`), expand all styles inline instead.

## Backend API limitations (never invent endpoints)
- No list-all purchases endpoint — can only fetch by customer, vehicle, or salesperson ID.
- No test drive status update endpoint — display an info message in BookingDetails.
- No password change endpoint — ChangePassword page shows a disabled form with explanation.
- Payment status update: PUT /payments/{id}/status?status= (query param, no body).

## Route / role guard pattern
- `RoleRoute` wraps routes that need role restriction.
- Role logic inside pages (via `useAuth`) is used for UI-level restrictions when multiple roles share a route.
- Sidebar `roles: null` means all authenticated users see the item.

## File naming
- New page directories follow the pattern used by the design subagent: `src/pages/purchases/`, `src/pages/payments/`, `src/pages/testDrives/`, `src/pages/reports/`, `src/pages/settings/`.
- All files are `.jsx` (not `.tsx`). The entry point `main.tsx` is the only `.tsx` file by design.

## Phases completed
1–4B (auth, vehicles, customers, employees, inventory) were already in place.
5–14 (purchases, payments, test drives, role dashboards, reports, settings, Dockerfile, README) were built in one session.
