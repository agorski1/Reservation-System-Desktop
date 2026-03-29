# Hotel Reservation System – Desktop Application

## 📌 Overview

This module is a **desktop application for hotel employees**, built with JavaFX. It is part of a complete hotel reservation system.

The system consists of three main parts:

* **Backend (Spring Boot)** – Spring Boot REST API
* **Web Application (React)** – client interface for customers
* **Desktop Application (this repository)** – internal management tool for employees
### 🔗 Related Repositories

* 🌐 Web (React) – *customer-facing application*  
  👉 [React Repo](https://github.com/agorski1/Reservation-System-Web)

* 🖥 Desktop (JavaFX) – *employee management system*  
  👉 [Reservation System Desktop](https://github.com/agorski1/Reservation-System-Desktop)


---

The desktop application is designed for hotel staff to efficiently manage:

* reservations
* rooms and room types
* customers
* payments
* reports and analytics

---

## 🛠 Tech Stack

* Java 21
* JavaFX
* Maven
* MaterialFX (UI components)
* Jackson (JSON processing)
* JJWT (JWT handling)
* Google Guice (dependency injection)
* JFreeChart (charts & analytics)
* OpenPDF / iText (PDF reports)

---

## 🖥 Features

* 🔐 Authentication using JWT
* 🛏 Room and reservation management
* 👥 Employee and customer management
* 💳 Payment processing
* 📊 Reports and statistics (charts)
* 📄 PDF report generation
* 🎨 Styled UI (CSS, BootstrapFX, MaterialFX)

---

## 🏗 Architecture

The application follows a layered structure:

* **controller** – UI logic (JavaFX controllers)
* **service** – communication with backend API
* **model** – application data structures
* **util** – helper classes and utilities

The application communicates with the backend via REST API.

---

## 🚀 Getting Started

### Prerequisites

* Java 21
* Maven
* Running backend service

---

## ▶️ Running the Application

```bash id="runapp1"
mvn javafx:run
```

---

## ⚙️ Configuration

The application communicates with the backend API.

Make sure the backend is running at:

```id="cfg1"
http://localhost:8080
```

If needed, update the API base URL in the configuration files or service layer.

---

## 📡 Backend Integration

The desktop app consumes REST endpoints such as:

* `/hd/auth/login`
* `/hd/reservations`
* `/hd/rooms`
* `/hd/payments`
* `/hd/reports`

JWT token is stored and attached to each request after login.

---

## 📂 Project Structure

```plaintext id="struct1"
src/main/java/com/reservio/reservation_system/reservationsystemdesktop
├── controller        # JavaFX controllers (UI logic)
├── service           # API communication layer
├── model             # application models
│   ├── reservation
│   ├── room
│   ├── user
│   ├── payment
│   ├── report
│   └── RoomType
└── util              # helper classes

src/main/resources
├── fxml              # UI layouts
├── css               # styling
└── fonts             # custom fonts
```

---

## 📊 Reports & Analytics

The application supports generating reports using:

* charts (JFreeChart)
* PDF exports (OpenPDF / iText)

---

## 🧪 Testing

Unit tests are planned for future development.

---

## ⚠️ Notes

* Backend must be running before launching the application
* JWT authentication is required to access most features
* Designed for internal use by hotel staff (employees/admins)

