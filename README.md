# User Management API

Simple REST API for user management and authentication built with Spring Boot.

---

## 🚀 Features

* CRUD operations for users
* Login with email & password validation
* Password hashing (BCrypt)
* Global exception handling
* OpenAPI (Swagger) documentation
* Dockerized setup

---

## 🐳 Run with Docker

### 1. Clone the repo

```bash
git clone <your-repo-url>
cd <your-repo-folder>
```

### 2. Start the app

```bash
docker compose up --build
```

---

## 🌐 Access

* API:
  http://localhost:8080/api/users

* Swagger UI:
  http://localhost:8080/swagger-ui.html

---

## 📖 Endpoints

* `POST /api/users` → create user
* `POST /api/users/login` → login
* `GET /api/users` → list users
* `PUT /api/users/{id}` → update user
* `DELETE /api/users/{id}` → delete user

---

## 🔐 Notes

* Passwords are hashed using BCrypt
* No JWT/auth system (out of scope for this task)
