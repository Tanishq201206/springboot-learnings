# Multi‑Factor Authentication (MFA) with Google Authenticator (TOTP) — Spring Boot 3

This learning project demonstrates **Time‑based One‑Time Password (TOTP)** MFA in **Spring Boot 3 / Spring Security 6**.
It covers:
- MFA setup flow (provisioning secret + QR)
- TOTP verification with Google Authenticator / Microsoft Authenticator
- Login → password check → TOTP step
- (Optional) JWT issuance **after** successful TOTP verification
- Secure endpoints accessible only after MFA

> Works with any RFC‑6238 authenticator app (Google Authenticator, Microsoft Authenticator, Authy, etc.).

---

## 📁 Repository Placement
Recommended location inside your collection repo:
```
springboot-learnings/
└── mfa-google-auth/
    ├── (project files here)
    └── README.md
```

---

## ⚙️ Tech Stack
- Java 17
- Spring Boot 3.2.x
- Spring Security 6
- Spring Web, Spring Data JPA (MySQL)
- TOTP library: `com.warrenstrange:googleauth` **or** `dev.samstevens.totp:totp` (either approach works)
- Maven

---

## 🗄️ Database Setup
Create a database and update credentials as needed:
```sql
CREATE DATABASE mfa_db;
```
`src/main/resources/application.properties` (sample):
```properties
server.port=8082

spring.datasource.url=jdbc:mysql://localhost:3306/mfa_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# If also issuing JWT post-MFA:
jwt.secret=change-me-base64-256-bit
```

Entity typically stores (at minimum):
- `username`, `password` (BCrypt)
- `mfaEnabled` (boolean)
- `mfaSecret` (Base32 secret used for TOTP)

---

## 🔐 Auth & MFA Flow (typical)
1) **Register** a user (username + password).
2) **Login (Step 1)** — verify username/password.  
   - If `mfaEnabled=false` → return message prompting **MFA setup**.
   - If `mfaEnabled=true` → return message **"MFA enabled. Please verify OTP."**
3) **Setup MFA** — server generates a TOTP secret + otpauth URI + QR (base64). User scans the QR in Google Authenticator.
4) **Verify TOTP (Step 2)** — user submits the 6‑digit code; if correct, mark `mfaEnabled=true`.  
   - If using JWT, issue tokens **only after** OTP success.
5) **Access Secured Endpoints** — use JWT or session after MFA is complete.

---

## 🧭 API Endpoints (example design)

### 1) Register
`POST /auth/register`
```json
{ "username": "alice", "password": "password123" }
```
**Response:** `"User registered successfully"`

### 2) Login (Step 1 — password)
`POST /auth/login`
```json
{ "username": "alice", "password": "password123" }
```
**Possible responses:**
```json
{ "message": "MFA not set up. Please complete setup at /auth/setup-mfa" }
```
or
```json
{ "message": "MFA enabled. Please verify OTP." }
```

### 3) Setup MFA (provision secret + QR)
`POST /auth/setup-mfa`
```json
{ "username": "alice", "password": "password123" }
```
**Response:**
```json
{
  "secret": "JBSWY3DPEHPK3PXP",
  "qrImageBase64": "data:image/png;base64,iVBORw0KGgo...",
  "otpAuthUrl": "otpauth://totp/YourApp:alice?secret=JBSW...&issuer=YourApp&digits=6&period=30"
}
```

### 4) Verify OTP (Step 2 — 6‑digit TOTP)
`POST /auth/verify-otp`
```json
{ "username": "alice", "code": "123456" }
```
**Response (JWT variant):**
```json
{
  "message": "MFA verified",
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi..."
}
```
(or a simple success message if using session‑based auth)

### 5) Secure endpoint
`GET /api/secure/hello` with
```
Authorization: Bearer <accessToken>
```
**Response:** `"Hello after MFA!"`

---

## 🧱 Key Components (typical)
- `MfaController` — handles `/auth/setup-mfa`, `/auth/verify-otp`.
- `AuthController` — handles `/auth/register`, `/auth/login`.
- `TotpService` — generates secret, QR/URI, and verifies TOTP codes.
- `UserService` / `UserDetailsService` — loads and updates user data (`mfaEnabled`, `mfaSecret`).
- `SecurityConfig` — configures Spring Security filters; if using JWT, adds JWT filter.
- (Optional) `JwtUtil`, `JwtFilter` — if issuing JWT tokens after MFA.

---

## ▶️ How to Run
```bash
# From the project root (where pom.xml exists)
./mvnw spring-boot:run         # macOS/Linux
# or
mvnw.cmd spring-boot:run       # Windows
```

App starts at **http://localhost:8082** (changeable via `server.port`).

---

## 🧪 Quick cURL Test
```bash
# 1) Register user
curl -X POST http://localhost:8082/auth/register   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 2) Login (password step)
curl -X POST http://localhost:8082/auth/login   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 3) Setup MFA (get secret + QR)
curl -X POST http://localhost:8082/auth/setup-mfa   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 4) Verify one-time code from authenticator
curl -X POST http://localhost:8082/auth/verify-otp   -H "Content-Type: application/json"   -d '{"username":"alice","code":"123456"}'

# 5) Call a protected endpoint with the issued access token
curl -X GET http://localhost:8082/api/secure/hello   -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## 🧩 Notes & Improvements
- Enforce **BCrypt** for passwords.
- Use **Base32** for the TOTP secret and store securely.
- **Do not** log secrets or OTP codes.
- Consider rate‑limiting OTP attempts and account lockout policies.
- Add backup codes / recovery flow.
- If JWT is used: rotate refresh tokens, consider blacklist/revocation, store secrets in env vars.

---

## 📝 License
MIT (or your choice).

---

## 🙌 Credits
Learning project by **Tanishq Singh**.
