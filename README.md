# TDD Kata: Car Dealership Inventory System


# Objective


The goal of this kata is to design, build, and test a full-stack Car Dealership Inventory System.


This project tests skills in:


- API development
- Database management
- Frontend implementation
- Testing
- Modern development workflows


---

# Core Requirements


# 1. Backend API (RESTful)


The backend API serves as the brain of the application and manages dealership operations.


The system provides REST APIs for:


- Vehicle management
- Inventory management
- Purchase processing
- Customer management
- Employee management
- Payment management
- Test drive management


---

# Technology Stack


The backend is implemented using:


- Java 21
- Spring Boot 3.5.16
- Spring MVC
- Spring Data JPA
- PostgreSQL


---

# Database


The application connects to a real PostgreSQL database.


| Property | Details |
|---|---|
| Database | PostgreSQL |
| Database Name | car_dealership_db |
| ORM | Hibernate / JPA |
| Schema Management | Hibernate ddl-auto update |


An in-memory database is not used.


---

# User Authentication


The application implements secure token-based authentication using:


- Spring Security
- JWT Authentication
- Role-Based Authorization


Features:


- User registration
- User login
- JWT token generation
- Protected API endpoints
- Admin authorization


---

# API Endpoints


## Authentication


| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Authenticate user and generate JWT | Public |


---

# Vehicle Management (Protected)


| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/vehicles` | Add new vehicle | Protected |
| GET | `/api/vehicles` | View available vehicles | Protected |
| GET | `/api/vehicles/search` | Search vehicles | Protected |
| PUT | `/api/vehicles/{id}` | Update vehicle details | Protected |
| DELETE | `/api/vehicles/{id}` | Delete vehicle | Admin |


Vehicle contains:


| Field | Description |
|---|---|
| ID | Unique vehicle identifier |
| Make | Vehicle manufacturer |
| Model | Vehicle model |
| Category | Vehicle category |
| Price | Vehicle price |
| Quantity | Available stock quantity |


---

# Inventory Management (Protected)


| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/vehicles/{id}/purchase` | Purchase vehicle and decrease quantity | Protected |
| POST | `/api/vehicles/{id}/restock` | Increase vehicle quantity | Admin |


---

# 2. Frontend Application


A modern Single Page Application (SPA) was developed using React to interact with the backend API.


---

# Frontend Technology


| Layer | Technology |
|---|---|
| Framework | React 18 |
| Build Tool | Vite |
| Styling | Tailwind CSS |
| Routing | React Router |
| API Communication | Axios |
| Server State Management | React Query |
| Forms | React Hook Form |
| Authentication | JWT Token Handling |


---

# Frontend Functionality


## Authentication


| Feature | Description |
|---|---|
| Registration | Create new user account |
| Login | Authenticate users |
| JWT Storage | Maintain authenticated session |
| Protected Routes | Restrict unauthorized access |
| Role Access | Admin and user based UI control |


---

# Dashboard / Homepage


The dashboard provides:


- Available vehicle listing
- Vehicle cards
- Vehicle details
- Inventory availability
- Responsive interface


---

# Search and Filter


Users can:


- Search vehicles
- Filter vehicles
- View vehicle details


---

# Purchase System


Features:


- Purchase button for vehicles
- Stock validation
- Purchase disabled when quantity reaches zero
- Inventory update after purchase


---

# Admin Vehicle Management


Admin users can:


- Add vehicles
- Update vehicles
- Delete vehicles
- Manage inventory


---

# Technology Stack


## Backend Stack


| Layer | Technology |
|---|---|
| Programming Language | Java 21 |
| Framework | Spring Boot 3.5.16 |
| Build Tool | Maven |
| Database | PostgreSQL |
| Persistence | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT |
| Validation | Spring Boot Validation |
| API Documentation | Swagger / OpenAPI |
| Testing | JUnit 5, Mockito, Testcontainers |
| Code Reduction | Lombok |


---

## Frontend Stack


| Layer | Technology |
|---|---|
| Framework | React 18 |
| Build Tool | Vite |
| CSS Framework | Tailwind CSS |
| Routing | React Router |
| HTTP Client | Axios |
| State Management | React Query |
| Form Handling | React Hook Form |


---

# Project Overview


The Car Dealership Inventory System is a full-stack application designed to digitize dealership operations.


The system manages:


- Vehicles
- Inventory
- Purchases
- Customers
- Employees
- Test Drives
- Payments


The application consists of:


## Backend Application


Responsible for:


- REST API services
- Database operations
- Authentication
- Authorization
- Business logic


---

## Frontend Application


Responsible for:


- User interface
- Vehicle browsing
- Searching
- Purchasing
- Administrative operations

# Application Architecture


The backend follows a standard layered Spring Boot architecture.


```
Controller

      |

Service Interface

      |

Service Implementation

      |

Repository

      |

Database
```


---

# Backend Layers


| Layer | Responsibility |
|---|---|
| Controller Layer | Handles REST API requests and responses |
| Service Layer | Contains business logic and application rules |
| Service Implementation | Implements service operations |
| Repository Layer | Handles database communication |
| Entity Layer | Defines JPA database models |
| DTO Layer | Handles request and response objects |
| Mapper Layer | Converts entities and DTOs |
| Security Layer | Handles authentication and authorization |
| Exception Layer | Provides centralized error handling |
| Configuration Layer | Application configuration management |


---

# Implemented Modules


| Module | Responsibility |
|---|---|
| Authentication Module | User registration, login, JWT generation, and security |
| Vehicle Management | Vehicle CRUD operations and vehicle searching |
| Purchase Management | Vehicle purchase processing |
| Purchase History | Maintains previous purchase records |
| Inventory Management | Stock management and inventory tracking |
| Customer Management | Customer information management |
| Employee Management | Employee records and role management |
| Test Drive Management | Test drive scheduling and tracking |
| Payment Management | Payment records and transaction tracking |


---

# Database Entities


The application uses JPA entities mapped with PostgreSQL database tables.


| Entity | Responsibility |
|---|---|
| User | Stores authentication and user information |
| Vehicle | Stores vehicle details and inventory quantity |
| Customer | Stores customer information |
| Employee | Stores employee information |
| PurchaseHistory | Stores vehicle purchase records |
| InventoryLog | Tracks inventory changes |
| TestDriveBooking | Stores test drive appointments |
| PaymentHistory | Stores payment transactions |


---

# Vehicle Management Module


The Vehicle Management module provides complete vehicle inventory operations.


Features:


| Operation | Description |
|---|---|
| Create | Add new vehicle into inventory |
| Read | View vehicle details |
| Update | Modify vehicle information |
| Delete | Remove vehicle from inventory |
| Search | Search vehicles using different parameters |


---

# Purchase Management Module


The Purchase Management module handles vehicle purchase operations.


Features:


| Feature | Description |
|---|---|
| Purchase Processing | Creates vehicle purchase records |
| Customer Linking | Associates purchases with customers |
| Employee Linking | Tracks responsible employees |
| History Tracking | Maintains purchase history |


Purchase workflow:


```
Customer

   |

Select Vehicle

   |

Purchase Request

   |

Inventory Update

   |

Purchase History Created
```


---

# Inventory Management Module


The Inventory module manages vehicle stock availability.


Features:


| Feature | Description |
|---|---|
| Stock Tracking | Maintains available vehicle quantity |
| Purchase Update | Decreases quantity after purchase |
| Restock | Increases available quantity |
| Inventory Logs | Records stock changes |


---

# Customer Management Module


Features:


- Create customer records
- Update customer information
- View customer details
- Maintain customer history


---

# Employee Management Module


Features:


- Employee CRUD operations
- Employee role management
- Employee information tracking


Employee information:


| Field | Description |
|---|---|
| Employee Code | Unique employee identifier |
| Role | Employee designation |
| Joining Date | Employee joining information |
| Salary | Employee salary information |


---

# Test Drive Management Module


Features:


- Schedule test drives
- Assign vehicles
- Assign customers
- Track bookings


Test drive information:


| Field | Description |
|---|---|
| Customer | Customer requesting test drive |
| Vehicle | Selected vehicle |
| Booking Date | Scheduled date |
| Salesperson | Assigned employee |


---

# Payment Management Module


Features:


- Record payments
- Maintain payment history
- Track financial transactions


Payment information:


| Field | Description |
|---|---|
| Payment ID | Unique payment identifier |
| Amount | Payment amount |
| Payment Method | Payment type |
| Date | Transaction date |


---

# Security Implementation


Security is implemented using:


- Spring Security
- JWT Authentication
- Role-Based Authorization


Authentication flow:


```
User Login

      |

Credential Validation

      |

JWT Token Generation

      |

Token Stored By Client

      |

Token Sent With Requests

      |

JWT Filter Validation

      |

Access Granted
```


---

# JWT Security Features


| Feature | Implementation |
|---|---|
| Authentication | JWT Token Based Authentication |
| Session Management | Stateless Authentication |
| Authorization | Role-Based Access Control |
| Password Security | BCrypt Password Encryption |
| Request Security | JWT Request Filtering |


---

# Security Components


| Component | Responsibility |
|---|---|
| JwtAuthenticationFilter | Validates JWT tokens for requests |
| CustomUserDetailsService | Loads user details from database |
| SecurityConfig | Defines security rules |
| AccessDeniedHandler | Handles forbidden requests |
| AuthenticationEntryPoint | Handles unauthorized requests |


---

# Exception Handling


The project uses centralized exception handling.


Implemented using:


```
@ControllerAdvice
```


Features:


| Feature | Description |
|---|---|
| Global Error Handling | Centralized API error responses |
| Validation Errors | Handles invalid input |
| Resource Not Found | Handles missing records |
| Security Errors | Handles authentication failures |


---

# API Documentation


Swagger/OpenAPI is integrated for API documentation.


Swagger UI:


```
http://localhost:8087/swagger-ui/index.html
```


OpenAPI JSON:


```
http://localhost:8087/v3/api-docs
```


Swagger provides:


- API exploration
- Endpoint testing
- JWT authorization support


---

# Testing Strategy (TDD)


The project follows:


```
Red → Green → Refactor
```


Testing includes:


| Testing Type | Coverage |
|---|---|
| Unit Testing | Service and business logic |
| Repository Testing | Database operations |
| Controller Testing | REST endpoint validation |
| Security Testing | Authentication and authorization |
| Integration Testing | Complete application workflow |


Testing technologies:


| Tool | Purpose |
|---|---|
| JUnit 5 | Test framework |
| Mockito | Mocking dependencies |
| Spring Boot Test | Application testing |
| Spring Security Test | Security testing |
| Testcontainers | PostgreSQL integration testing |


Run tests:


```bash
mvn clean test
```

# Database Configuration


The application uses PostgreSQL as the primary database.


## PostgreSQL Configuration


Configuration file:


```
src/main/resources/application.properties
```


Configuration:


```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/car_dealership_db

spring.datasource.username=postgres

spring.datasource.password=<your-password>

spring.jpa.hibernate.ddl-auto=update
```


---

# Database Setup


Create database:


```sql
CREATE DATABASE car_dealership_db;
```


Update the database username and password according to your local PostgreSQL installation.


---

# Running the Application


## Prerequisites


| Requirement | Version |
|---|---|
| Java | 21 |
| Maven | Latest Version |
| PostgreSQL | Latest Version |
| Node.js | Latest Version |
| npm | Latest Version |


---

# Backend Setup


Navigate to backend project:


```bash
cd backend
```


Build the application:


```bash
mvn clean install
```


Run Spring Boot application:


```bash
mvn spring-boot:run
```


Backend server:


```
http://localhost:8087
```


---

# Frontend Setup


Navigate to frontend project:


```bash
cd vehicle-dealership-frontend
```


Install dependencies:


```bash
npm install
```


Start development server:


```bash
npm run dev
```


Frontend server:


```
http://localhost:5173
```


---

# Frontend Project Structure


```
vehicle-dealership-frontend

src

├── api
├── assets
├── components
├── context
├── hooks
├── pages
├── routes
├── services
├── utils
└── App.jsx
```


---

# Frontend Folder Responsibility


| Folder | Responsibility |
|---|---|
| api | Axios configuration and API communication |
| assets | Static resources |
| components | Reusable UI components |
| context | Global state management |
| hooks | Custom React hooks |
| pages | Application screens |
| routes | Route configuration |
| services | Backend API service calls |
| utils | Helper functions |


---

# Backend Project Structure


```
src/main/java/com/dealership

├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
├── mapper
├── security
├── exception
└── config
```


---

# Backend Package Responsibility


| Package | Responsibility |
|---|---|
| controller | REST API endpoints |
| service | Business logic interfaces |
| service.impl | Business logic implementation |
| repository | Database operations |
| entity | JPA database entities |
| dto | Request and response objects |
| mapper | Entity and DTO conversion |
| security | JWT authentication and authorization |
| exception | Global exception handling |
| config | Application configuration |


---

# API Request Flow


```
React Frontend

        |

        |

Axios HTTP Request

        |

        |

Spring Boot Controller

        |

        |

Service Layer

        |

        |

Repository Layer

        |

        |

PostgreSQL Database
```


---

# Git & Version Control


Git was used throughout the complete development lifecycle.


The project follows proper version control practices:


| Practice | Description |
|---|---|
| Meaningful Commits | Clear commit messages describing changes |
| Incremental Development | Features developed and tested separately |
| Feature Tracking | Changes organized logically |
| Version History | Complete development history maintained |


---

# Clean Code Practices


The project follows:


| Practice | Implementation |
|---|---|
| SOLID Principles | Applied in backend architecture |
| Separation of Concerns | Layer-based application structure |
| DTO Pattern | Entity and API separation |
| Validation | Request data validation |
| Exception Handling | Centralized error management |
| Reusable Components | React component architecture |
| Maintainable Code | Clean naming conventions and structure |


---

# Testing Report


The project contains:


| Test Category | Status |
|---|---|
| Entity Tests | Completed |
| Repository Tests | Completed |
| Service Tests | Completed |
| Controller Tests | Completed |
| Security Tests | Completed |
| Integration Tests | Completed |


Run complete test suite:


```bash
mvn clean test
```


---

# Complete Feature List


| Feature | Status |
|---|---|
| User Registration | Completed |
| User Login | Completed |
| JWT Authentication | Completed |
| Role-Based Authorization | Completed |
| Vehicle CRUD Operations | Completed |
| Vehicle Search | Completed |
| Purchase Management | Completed |
| Purchase History | Completed |
| Inventory Management | Completed |
| Inventory Logs | Completed |
| Customer Management | Completed |
| Employee Management | Completed |
| Test Drive Booking | Completed |
| Payment Management | Completed |
| Swagger Documentation | Completed |
| Backend Testing | Completed |
| React Frontend | Completed |
| Responsive User Interface | Completed |


---

# Deliverables


The project includes:


✔ Full-stack Car Dealership Inventory System

✔ Spring Boot REST API

✔ PostgreSQL Database Integration

✔ React Single Page Application

✔ JWT Authentication

✔ Role-Based Authorization

✔ Vehicle Inventory Management

✔ Purchase Management

✔ Customer Management

✔ Employee Management

✔ Test Drive Management

✔ Payment Management

✔ Swagger API Documentation

✔ Automated Testing

✔ Complete Source Code


---

# Future Improvements


Possible future enhancements:


- Cloud deployment
- Vehicle image upload
- Advanced analytics dashboard
- Email notifications
- Payment gateway integration
- Mobile application
- AI-based inventory prediction


---

# Conclusion


The **Car Dealership Inventory System** is a complete full-stack application built using modern backend and frontend technologies.


The project demonstrates:


- REST API development
- Database management
- JWT security implementation
- React SPA development
- TDD-based testing approach
- Clean software architecture


The application provides a scalable foundation for managing real-world dealership operations.
