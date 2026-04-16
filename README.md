# 🏥 Online Appointment Scheduling System

A full-stack, web-based **healthcare appointment management platform** built with **Spring Boot**, **Spring MVC**, **Spring Security**, **Spring Data JPA**, and **Thymeleaf**. The system digitizes the end-to-end lifecycle of medical appointments — from booking and scheduling through payment processing and real-time notifications — for three distinct user roles: **Patient**, **Doctor**, and **Administrator**.

> **Academic Context:** Mini Project — UE23CS352B Object Oriented Analysis & Design | PES University, Bengaluru | Semester VI (Jan–May 2026)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Design Patterns](#-design-patterns)
- [Design Principles (SOLID)](#-design-principles-solid)
- [Project Structure](#-project-structure)
- [Domain Model](#-domain-model)
- [State Machines](#-state-machines)
- [Getting Started](#-getting-started)
- [Default Credentials](#-default-credentials-seed-data)
- [Configuration](#-configuration)
- [API / Route Overview](#-route-overview)
- [UML Models Summary](#-uml-models-summary)
- [Team Contributions](#-team-contributions)

---

## ✨ Features

### Major Features
| Feature | Description |
|---|---|
| **Appointment Booking & Management** | Patients search for doctors, view available time slots, and book appointments with a stated reason for visit |
| **Doctor Schedule Management** | Doctors add/remove availability slots and approve, reject, or mark appointments as completed |
| **Payment Processing** | Patients pay for approved appointments via Card, Cash, or UPI — implemented using the Strategy Pattern |
| **Admin Dashboard & Reports** | Administrators view system-wide KPIs, manage users, generate revenue and appointment breakdown reports |

### Minor Features
- **In-App Notification System** — Real-time notifications for appointment status changes (Observer Pattern)
- **Appointment Rescheduling** — Patients can move a pending appointment to a different available slot
- **Appointment Cancellation** — Cancellation automatically frees up the doctor's slot
- **User Profile Management** — Users can update their name and phone number

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Web / MVC | Spring MVC (`@Controller`, `@GetMapping`, `@PostMapping`) |
| Security | Spring Security 6 + Thymeleaf Security extras |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory, dev/demo) |
| Templating | Thymeleaf 3 |
| Build Tool | Apache Maven |
| Validation | Jakarta Bean Validation (`spring-boot-starter-validation`) |

---

## 🏗 Architecture

The project strictly follows the **Model-View-Controller (MVC)** architectural pattern as enforced by Spring MVC:

```
┌──────────────────────────────────────────────────┐
│                    Browser                        │
└──────────────────────┬───────────────────────────┘
                       │ HTTP
┌──────────────────────▼───────────────────────────┐
│              Controller Layer                     │
│  AuthController │ PatientController               │
│  DoctorController │ AdminController               │
│  (handles HTTP requests, binds model attributes)  │
└──────────────────────┬───────────────────────────┘
                       │ calls interfaces only
┌──────────────────────▼───────────────────────────┐
│               Service Layer                       │
│  AppointmentServiceImpl │ UserServiceImpl         │
│  PaymentServiceImpl │ ScheduleServiceImpl         │
│  DoctorServiceImpl │ NotificationService          │
│  (all business logic lives here)                  │
└──────────┬───────────────────────┬───────────────┘
           │ uses                  │ calls
┌──────────▼──────────┐  ┌────────▼───────────────┐
│  Design Patterns    │  │   Repository Layer      │
│  Factory / Decorator│  │  AppointmentRepository  │
│  Observer / Strategy│  │  UserRepository         │
│                     │  │  ScheduleRepository     │
└─────────────────────┘  │  PaymentRepository      │
                         │  NotificationRepository  │
                         │  DoctorRepository        │
                         │  PatientRepository       │
                         └────────────┬────────────┘
                                      │ JPA
                         ┌────────────▼────────────┐
                         │      H2 Database         │
                         └─────────────────────────┘
```

**Key separation rules enforced:**
- Controllers never access repositories directly — only service interfaces
- Services encapsulate all business logic
- Views receive data only via the Spring `Model` object and contain zero business logic

---

## 🎨 Design Patterns

### 1. Factory Method Pattern (Creational)
**Location:** `com.appointment.pattern.factory.UserFactory`

Centralises creation of `User` subtypes (`Patient`, `Doctor`, `Admin`). The `createUser(UserRole role, ...)` factory method returns the correct concrete subtype, decoupling `UserServiceImpl` from constructor details. Adding a new role (e.g., `RECEPTIONIST`) requires only a new case in the factory — zero changes elsewhere.

```
UserFactory.createUser(role, dto)
  ├── PATIENT  → new Patient(...)
  ├── DOCTOR   → new Doctor(...)
  └── ADMIN    → new Admin(...)
```

---

### 2. Decorator Pattern (Structural)
**Location:** `com.appointment.pattern.decorator`

Applied to the appointment processing pipeline. Behaviours are composed dynamically without subclassing:

```
LoggingAppointmentDecorator
  └── ValidationAppointmentDecorator   ← checks slot availability, null guards
        └── BasicAppointmentProcessor  ← sets status to PENDING, persists
```

In `AppointmentServiceImpl.book()`, the chain is assembled at runtime. New cross-cutting concerns (e.g., authorization, audit) are added as new decorator classes without touching existing code.

| Class | Role |
|---|---|
| `AppointmentProcessor` | Interface defining `process()` contract |
| `BasicAppointmentProcessor` | Core logic — sets PENDING, saves |
| `ValidationAppointmentDecorator` | Pre-processing validation |
| `LoggingAppointmentDecorator` | Audit logging before/after |

---

### 3. Observer Pattern (Behavioral)
**Location:** `com.appointment.pattern.observer`

Decouples appointment status changes from their notification side-effects. `AppointmentServiceImpl` holds a `List<AppointmentObserver>` injected by Spring and calls `notifyObservers()` after every status-changing operation.

```
AppointmentObserver (interface)
  └── InAppNotifier (@Component)
        ├── Persists Notification records to DB
        └── Simulates email/SMS dispatch
```

Adding a new channel (e.g., `PushNotificationNotifier`, `SMSNotifier`, `EmailNotifier`) requires only a new `@Component` implementing `AppointmentObserver` — no changes to `AppointmentServiceImpl`.

---

### 4. Strategy Pattern (Behavioral)
**Location:** `com.appointment.pattern.strategy`

Enables runtime selection of the payment algorithm without any `if-else` or `switch` logic in the service layer.

```
PaymentStrategy (interface)
  ├── CardPaymentStrategy  (@Component)
  ├── UpiPaymentStrategy   (@Component)
  └── CashPaymentStrategy  (@Component)

PaymentContext → selects strategy by matching getMethodName() → executePayment()
```

Adding a new method (e.g., NetBanking) requires only a new class implementing `PaymentStrategy`.

---

## 📐 Design Principles (SOLID)

| Principle | How It's Applied |
|---|---|
| **SRP** | Each class has one reason to change. `AppointmentServiceImpl` handles only appointment logic. `InAppNotifier` owns notification creation. `PatientController` routes only patient-facing HTTP requests. |
| **OCP** | Open for extension, closed for modification. New payment methods extend `PaymentStrategy` without touching `PaymentContext`. New notifiers extend `AppointmentObserver` without touching `AppointmentServiceImpl`. |
| **LSP** | `Patient`, `Doctor`, `Admin` are valid substitutes for `User` everywhere. Any `PaymentStrategy` can replace another in `PaymentContext` without breaking flow. |
| **DIP** | High-level modules depend on abstractions. `AppointmentServiceImpl` depends on `AppointmentObserver` (not `InAppNotifier`). Controllers depend on service interfaces, not impl classes. `PaymentContext` depends on `PaymentStrategy`, not concrete strategies. All wired via Spring DI. |

---

## 📁 Project Structure

```
appointment-system/
├── pom.xml
└── src/
    └── main/
        ├── java/com/appointment/
        │   ├── AppointmentSystemApplication.java
        │   ├── config/
        │   │   ├── AuthHelper.java              # Session/auth utility
        │   │   ├── CustomUserDetailsService.java # Spring Security integration
        │   │   ├── DataSeeder.java              # Seeds demo users & schedules
        │   │   ├── SecurityConfig.java          # HTTP security rules & role routing
        │   │   └── WebConfig.java
        │   ├── controller/
        │   │   ├── AuthController.java          # /login, /register, /logout
        │   │   ├── PatientController.java       # /patient/**
        │   │   ├── DoctorController.java        # /doctor/**
        │   │   └── AdminController.java         # /admin/**
        │   ├── dto/
        │   │   └── RegisterDto.java             # Registration form binding
        │   ├── model/
        │   │   ├── User.java                    # Abstract base entity
        │   │   ├── Patient.java
        │   │   ├── Doctor.java
        │   │   ├── Admin.java
        │   │   ├── Appointment.java
        │   │   ├── AppointmentStatus.java       # Enum: PENDING/APPROVED/REJECTED/COMPLETED/CANCELLED
        │   │   ├── Schedule.java
        │   │   ├── Payment.java
        │   │   ├── PaymentStatus.java           # Enum: PENDING/PAID/FAILED
        │   │   ├── Notification.java
        │   │   ├── NotificationType.java
        │   │   └── UserRole.java                # Enum: PATIENT/DOCTOR/ADMIN
        │   ├── pattern/
        │   │   ├── decorator/
        │   │   │   ├── AppointmentProcessor.java
        │   │   │   ├── BasicAppointmentProcessor.java
        │   │   │   ├── ValidationAppointmentDecorator.java
        │   │   │   └── LoggingAppointmentDecorator.java
        │   │   ├── factory/
        │   │   │   └── UserFactory.java
        │   │   ├── observer/
        │   │   │   ├── AppointmentObserver.java
        │   │   │   └── InAppNotifier.java
        │   │   └── strategy/
        │   │       ├── PaymentStrategy.java
        │   │       ├── CardPaymentStrategy.java
        │   │       ├── UpiPaymentStrategy.java
        │   │       ├── CashPaymentStrategy.java
        │   │       └── PaymentContext.java
        │   ├── repository/
        │   │   ├── AppointmentRepository.java
        │   │   ├── DoctorRepository.java
        │   │   ├── PatientRepository.java
        │   │   ├── UserRepository.java
        │   │   ├── ScheduleRepository.java
        │   │   ├── PaymentRepository.java
        │   │   └── NotificationRepository.java
        │   └── service/
        │       ├── AppointmentService.java
        │       ├── DoctorService.java
        │       ├── PaymentService.java
        │       ├── ScheduleService.java
        │       ├── UserService.java
        │       ├── NotificationService.java
        │       └── impl/
        │           ├── AppointmentServiceImpl.java
        │           ├── DoctorServiceImpl.java
        │           ├── PaymentServiceImpl.java
        │           ├── ScheduleServiceImpl.java
        │           └── UserServiceImpl.java
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/main.css
            │   └── js/main.js
            └── templates/
                ├── login.html
                ├── register.html
                ├── fragments/sidebars.html
                ├── admin/
                │   ├── dashboard.html
                │   ├── appointments.html
                │   ├── doctors.html
                │   ├── reports.html
                │   └── users.html
                ├── doctor/
                │   ├── dashboard.html
                │   ├── appointments.html
                │   ├── schedule.html
                │   ├── notifications.html
                │   └── profile.html
                └── patient/
                    ├── dashboard.html
                    ├── doctors.html
                    ├── book.html
                    ├── appointments.html
                    ├── reschedule.html
                    ├── payment.html
                    ├── notifications.html
                    └── profile.html
```

---

## 🗂 Domain Model

| Class | Responsibility | Key Attributes |
|---|---|---|
| `User` | Abstract base entity for all users | `userId`, `name`, `email`, `phone`, `password`, `role` |
| `Patient` | Book / cancel / reschedule appointments | Inherits from `User` |
| `Doctor` | Manage schedule, approve appointments | `specialization`, `qualification`, `hospital`, `consultationFee` |
| `Admin` | User management, system oversight | `department` |
| `Appointment` | Core booking entity | `appointmentId`, `date`, `time`, `status`, `reason` |
| `Schedule` | Available time slots per doctor | `availableDate`, `availableTime`, `isBooked` |
| `Payment` | Payment record for an appointment | `amount`, `method`, `status`, `paidAt` |
| `Notification` | Status update messages | `message`, `type`, `isRead`, `createdAt` |

**Relationships:**
- `Patient` → books → `Appointment` (many-to-one Patient, many-to-one Doctor)
- `Doctor` → owns → `Schedule` (one-to-many)
- `Appointment` → has one → `Payment`
- `Appointment` status change → triggers → `Notification` (for both Patient and Doctor)

---

## 🔄 State Machines

### Appointment States
```
               ┌──────────┐
               │  PENDING  │ ◄── initial state on booking
               └─────┬─────┘
          ┌──────────┴──────────┐
          ▼                     ▼
    ┌──────────┐         ┌──────────┐
    │ APPROVED │         │ REJECTED │
    └─────┬────┘         └──────────┘
          │
    ┌─────┴────────────────┐
    ▼                      ▼
┌──────────┐        ┌───────────┐
│ COMPLETED│        │ CANCELLED │ ◄── also reachable from PENDING
└──────────┘        └───────────┘
                    (frees Schedule slot)
```

### Payment States
```
PENDING → PAID    (strategy executes successfully)
PENDING → FAILED  (strategy throws exception)
```

### Schedule Slot States
```
AVAILABLE → BOOKED      (appointment created)
BOOKED    → AVAILABLE   (appointment cancelled or rejected)
```

### Notification States
```
UNREAD → READ   (user visits notifications page; markAllRead() called)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)

### Clone & Run

```bash
git clone https://github.com/<your-username>/appointment-system.git
cd appointment-system

# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

On first startup, `DataSeeder` automatically seeds demo accounts and 7 days × 6 slots of schedule for every doctor — no manual database setup required.

### H2 Console (Dev)

The in-memory H2 database console is available at:

```
http://localhost:8080/h2-console
JDBC URL:  jdbc:h2:mem:appointmentdb
Username:  sa
Password:  (leave blank)
```

---

## 🔑 Default Credentials (Seed Data)

### Admin
| Email | Password |
|---|---|
| `admin@clinic.com` | `admin123` |

### Doctors (password for all: `doctor123`)
| Name | Email | Specialization | Hospital | Fee |
|---|---|---|---|---|
| Dr. Priya Sharma | `priya@clinic.com` | Cardiology | Apollo Hospital | ₹800 |
| Dr. Rahul Mehta | `rahul@clinic.com` | Neurology | Fortis Hospital | ₹1000 |
| Dr. Anita Nair | `anita@clinic.com` | Dermatology | Manipal Hospital | ₹600 |
| Dr. Suresh Kumar | `suresh@clinic.com` | Orthopedics | City Hospital | ₹700 |
| Dr. Meena Reddy | `meena@clinic.com` | Pediatrics | Rainbow Hospital | ₹500 |
| Dr. Vikram Joshi | `vikram@clinic.com` | Ophthalmology | Vision Care Centre | ₹550 |

### Patients (password for all: `patient123`)
| Name | Email |
|---|---|
| Arjun Patel | `arjun@example.com` |
| Kavya Iyer | `kavya@example.com` |
| Ravi Krishnan | `ravi@example.com` |

---

## ⚙️ Configuration

All configuration lives in `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# H2 In-Memory Database
spring.datasource.url=jdbc:h2:mem:appointmentdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Thymeleaf
spring.thymeleaf.cache=false
```

### Switching to MySQL (Production)

Replace the H2 block with:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/appointmentdb
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

And add the MySQL dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🗺 Route Overview

### Auth
| Method | URL | Description |
|---|---|---|
| GET/POST | `/login` | Login page |
| GET/POST | `/register` | Registration page |
| POST | `/logout` | Logout |

### Patient (`/patient/**`)
| Method | URL | Description |
|---|---|---|
| GET | `/patient/dashboard` | Patient home |
| GET | `/patient/doctors` | Browse doctors |
| GET | `/patient/book/{doctorId}` | Book appointment form |
| POST | `/patient/book` | Submit booking |
| GET | `/patient/appointments` | View my appointments |
| POST | `/patient/cancel/{id}` | Cancel appointment |
| GET | `/patient/reschedule/{id}` | Reschedule form |
| POST | `/patient/reschedule` | Submit reschedule |
| GET | `/patient/payment/{id}` | Payment form |
| POST | `/patient/payment` | Process payment |
| GET | `/patient/notifications` | View notifications |
| GET/POST | `/patient/profile` | View / update profile |

### Doctor (`/doctor/**`)
| Method | URL | Description |
|---|---|---|
| GET | `/doctor/dashboard` | Doctor home |
| GET | `/doctor/schedule` | View & manage schedule |
| POST | `/doctor/schedule/add` | Add time slot |
| POST | `/doctor/schedule/remove/{id}` | Remove time slot |
| GET | `/doctor/appointments` | View appointment requests |
| POST | `/doctor/appointments/approve/{id}` | Approve appointment |
| POST | `/doctor/appointments/reject/{id}` | Reject appointment |
| POST | `/doctor/appointments/complete/{id}` | Mark as completed |
| GET | `/doctor/notifications` | View notifications |
| GET/POST | `/doctor/profile` | View / update profile |

### Admin (`/admin/**`)
| Method | URL | Description |
|---|---|---|
| GET | `/admin/dashboard` | KPI dashboard |
| GET | `/admin/appointments` | All appointments (filterable) |
| GET | `/admin/users` | All users |
| GET | `/admin/doctors` | Add doctor form |
| POST | `/admin/doctors` | Create doctor account |
| POST | `/admin/users/remove/{id}` | Remove user |
| GET | `/admin/reports` | Revenue & appointment reports |

---

## 📊 UML Models Summary

The project includes the following UML models (documented in the OOAD report):

- **Use Case Diagram** — Interactions between Patient, Doctor, and Administrator actors and the system's use cases
- **Class Diagram** — Domain entities, inheritance hierarchy (`User` → `Patient`/`Doctor`/`Admin`), and associations
- **Activity Diagrams** — Detailed flow for: Book Appointment, Doctor Schedule & Approval, Payment Processing, Admin Management
- **State Diagrams** — State machines for: Appointment, Payment, Schedule Slot, Notification

---

## 👥 Team Contributions

| Name | Module Owned | Design Pattern | Principle |
|---|---|---|---|
| Member 1 | Appointment Booking & Rescheduling (`PatientController`, `AppointmentServiceImpl`, booking views) | Decorator Pattern | SRP |
| Member 2 | Doctor Schedule & Approval (`DoctorController`, `ScheduleServiceImpl`, doctor views) | Observer Pattern | OCP |
| Member 3 | Payment Processing (`PaymentServiceImpl`, `PaymentContext`, payment views) | Strategy Pattern | DIP |
| Member 4 | Admin Dashboard, User Management & Reporting (`AdminController`, `UserFactory`, admin views) | Factory Method Pattern | LSP |

Each module was developed in a feature branch and merged into `main` via pull request. All use cases share a single Spring Boot application with a unified H2/MySQL database.

---

## 📄 License

This project was developed as an academic mini-project for **UE23CS352B – Object Oriented Analysis & Design** at **PES University, Bengaluru** (Jan–May 2026). It is intended for educational purposes.
