# JWT Authentication (Spring Boot 3) — Access & Refresh Tokens

This is a **learning project** demonstrating JWT-based authentication in **Spring Boot 3.2** with:
- **Access tokens** (1 hour)
- **Refresh tokens** (7 days)
- Secured REST endpoint
- User registration with BCrypt password encoding
- Stateless security via a JWT filter

> Project source folder name used locally: `Springsecjwt`

---

## 📁 Repository Placement
If you’re collecting multiple learning projects in one repo (recommended), place this folder as:
```
springboot-learnings/
└── jwt-authentication/
    ├── (project files here)
    └── README.md
```
You can rename the folder to `jwt-authentication` (or keep `Springsecjwt`).

---

## ⚙️ Tech Stack
- Java 17
- Spring Boot 3.2.x
- Spring Security 6
- Spring Web, Spring Data JPA (MySQL)
- JJWT (io.jsonwebtoken)
- Maven

---

## 🔑 JWT Details
- **Access token TTL:** 1 hour
- **Refresh token TTL:** 7 days
- Both tokens are signed using HS256 with a secret set in `application.properties`:
  ```properties
  jwt.secret=I05hehFf7Y7dEqNBL3TRUOX7AnCSsknItW0WYT+HNBc=
  ```

> You can generate a new secret using the included helper `JwtSecretGenerator.java` (or any 256-bit base64 key).

---

## 🗄️ Database Setup
Create a local database and update credentials if needed:
```sql
CREATE DATABASE jwt_db;
```
`src/main/resources/application.properties` (defaults):
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_db
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.org.springframework.security=DEBUG
# JWT secret:
jwt.secret=I05hehFf7Y7dEqNBL3TRUOX7AnCSsknItW0WYT+HNBc=
```

---

## ▶️ How to Run
With Maven Wrapper (recommended):
```bash
# From the project root (where pom.xml exists)
./mvnw spring-boot:run         # macOS/Linux
# or
mvnw.cmd spring-boot:run       # Windows
```

Or package and run:
```bash
./mvnw clean package
java -jar target/*.jar
```

App starts at **http://localhost:8081**.

---

## 🔐 API Endpoints

### 1) Register
`POST /auth/register`
```json
{
  "username": "alice",
  "password": "password123"
}
```
**Response**: "User registered successfully" (409 if username exists)

### 2) Login (get access & refresh tokens)
`POST /auth/login`
```json
{
  "username": "alice",
  "password": "password123"
}
```
**Response**
```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi..."
}
```

### 3) Refresh access token
`POST /auth/refresh`
```json
{ "refreshToken": "<your-refresh-token>" }
```
**Response**
```json
{
  "accessToken": "newAccessToken...",
  "refreshToken": "<same-refresh-token>"
}
```

### 4) Secured test endpoint
`GET /api/hello`

Add header:
```
Authorization: Bearer <accessToken>
```
**Response**: `Hello, secured world!`

---

## 🧱 Key Classes (short map)
- `Security/SecurityConfig.java` — Security filter chain, stateless sessions, JWT filter registration.
- `utilities/JwtFilter.java` — Extracts Bearer token, validates, sets `SecurityContext`.
- `utilities/JwtUtil.java` — Generates and validates access/refresh tokens.
- `Controller/AuthController.java` — `/auth/login`, `/auth/refresh`, `/auth/register`.
- `Controller/TestController.java` — `/api/hello` (secured).
- `UserDetailsServiceImpl.java` — Loads user and grants roles.
- `Model/Users.java` + `UserRepository.java` — JPA entity/repo for users.

---

## 🧪 Quick cURL Test
```bash
# 1) Register
curl -X POST http://localhost:8081/auth/register   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 2) Login
curl -X POST http://localhost:8081/auth/login   -H "Content-Type: application/json"   -d '{"username":"alice","password":"password123"}'

# 3) Use access token
curl -X GET http://localhost:8081/api/hello   -H "Authorization: Bearer <ACCESS_TOKEN>"

# 4) Refresh
curl -X POST http://localhost:8081/auth/refresh   -H "Content-Type: application/json"   -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

---

## 🔒 Notes & Improvements (ideas for future)
- Move DB credentials & JWT secret to environment variables.
- Add roles/authorities-based endpoint restrictions.
- Implement token blacklist/revocation.
- Add integration tests for filter + controller layer.
- Dockerize app with MySQL for easy setup.

---

## 📝 License
MIT (or your choice).

---

## 🙌 Credits
Learning project by **Tanishq Singh**.
