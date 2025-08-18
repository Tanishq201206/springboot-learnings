# Multi‑Auth (TOTP + Email OTP + SMS OTP) with JWT — Spring Boot 3

This project (package **`com.prectice.mfa`**) implements **three authentication flows**. All of them issue a **JWT** after verification so you can access secured endpoints.

**Controllers & base paths (from code):**
- **TOTP (Google/Microsoft Authenticator)** → `@RequestMapping("/api/auth")`
  - `POST /register` — register user and provision TOTP (QR/secret).
  - `POST /login` — login with **username + password + totp** → returns JWT.
- **Email OTP** → `@RequestMapping("/api/email-auth")`
  - `POST /register` — register with email.
  - `POST /login/init` — start login, **sends OTP to email**.
  - `POST /login/verify` — verify OTP → returns JWT.
- **SMS OTP** → `@RequestMapping("/api/sms-auth")`
  - `POST /register` — register with phone.
  - `POST /login` — start login, **prints OTP in console** (dev mode).
  - `POST /verify` — verify OTP → returns JWT.
- **Secured sample** → `GET /api/secure/hello` (requires `Authorization: Bearer <token>`)

> TOTP uses **GoogleAuthenticator** (`com.warrenstrange.googleauth`) under the hood. Email OTP uses Spring Mail; SMS OTP currently prints to console (can be wired to Fast2SMS). JWT is built with **jjwt**.

---

## ✅ How to Run

### Database (MySQL)
Default config from `src/main/resources/application.properties` (found in your code):
```
spring.datasource.url=jdbc:mysql://localhost:3306/mfa_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
server.port=8083
```
Create the DB if missing:
```sql
CREATE DATABASE mfa_db;
```
> **Security note:** Your current `application.properties` contains real mail credentials. **Do not commit** those publicly. Use the `application.properties.example` in this folder to replace secrets with placeholders.

### Build & Run
From the project root (where `pom.xml` exists):
```bash
./mvnw spring-boot:run        # macOS/Linux
# or on Windows
mvnw.cmd spring-boot:run
```
App will start at **http://localhost:8083**.

---

## 📦 Dependencies (from `pom.xml`)
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- mysql-connector-java
- **jjwt**: `jjwt-api`, `jjwt-impl`, `jjwt-jackson`
- **googleauth** (TOTP)
- test: spring-boot-starter-test, spring-security-test

---

## 🔑 Request/Response Schemas (from DTOs)

### 1) TOTP (Google/Microsoft Authenticator) — `/api/auth`
**Register** — `POST /api/auth/register`  
Request (body — `RegisterRequest`):
```json
{ "username": "alice", "password": "password123" }
```
Response: JSON that includes **TOTP provisioning info** (QR/secret).  
*(Your service generates a secret with GoogleAuthenticator and a QR using `GoogleAuthenticatorQRGenerator`.)*

**Login** — `POST /api/auth/login`  
Request (body — `LoginRequest`):
```json
{ "username": "alice", "password": "password123", "totp": 123456 }
```
Response (success):
```json
{ "token": "<JWT>" }
```

### 2) Email OTP — `/api/email-auth`
**Register** — `POST /api/email-auth/register`  
Request (body — `EmailRegisterRequest`):
```json
{ "username": "alice", "password": "password123", "email": "alice@example.com" }
```
**Start login** — `POST /api/email-auth/login/init`  
Request (body — `EmailLoginRequest`):
```json
{ "username": "alice", "password": "password123" }
```
(OTP is **sent to email** using Spring Mail.)

**Verify** — `POST /api/email-auth/login/verify`  
Request (body — `EmailOtpVerificationRequest`):
```json
{ "username": "alice", "otp": "123456" }
```
Response:
```json
{ "token": "<JWT>" }
```

### 3) SMS OTP — `/api/sms-auth`
**Register** — `POST /api/sms-auth/register`  
Request (body — `SmsRegisterRequest`):
```json
{ "username": "bob", "password": "password123", "phoneNumber": "+919999999999" }
```
**Start login** — `POST /api/sms-auth/login`  
Request (body — `SmsLoginRequest`):
```json
{ "username": "bob", "password": "password123" }
```
(OTP is **printed to console** in dev mode.)

**Verify** — `POST /api/sms-auth/verify`  
Request (body — `SmsOtpVerifyRequest`):
```json
{ "username": "bob", "otp": "123456" }
```
Response:
```json
{ "token": "<JWT>" }
```

### Secured endpoint
**GET** `/api/secure/hello` with header:
```
Authorization: Bearer <JWT>
```

---

## 🔒 Security Notes (very important)
- **Do not commit** real secrets (mail password, JWT secret) to GitHub.
- In `JwtService` the secret is hardcoded (`SECRET_KEY = "my-super-secret..."`). Replace with an **env variable**.
- Rotate your leaked mail **app password** immediately if it has been committed anywhere public.
- Use `.gitignore` to exclude `target/`, `.idea/`, etc.

---

## 🧩 Fast2SMS (optional SMS provider)
If you later switch from console to Fast2SMS, configure keys in properties or env and plug them into your `SmsOtpService`.

---

## 🧪 Quick cURL Smoke Test

```bash
# 1) Register TOTP user
curl -X POST http://localhost:8083/api/auth/register   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 2) Login with TOTP
curl -X POST http://localhost:8083/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123","totp":123456}'

# 3) Secured hello
curl -H "Authorization: Bearer <JWT>" http://localhost:8083/api/secure/hello
```

---

## 📁 Suggested repo placement
Place the whole project under a folder like:
```
springboot-learnings/
└── mfa-multi-auth/
    ├── pom.xml
    ├── src/...
    └── README.md
```
