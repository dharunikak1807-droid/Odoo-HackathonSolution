# AssetFlow AI — Backend Foundation

Enterprise Asset & Resource Management System — Odoo Hackathon.

This is **Phase 1** of the project: a working, runnable Spring Boot backend covering
JWT authentication with role-based access control, and the core Department →
AssetCategory → Asset → Allocation flow (including duplicate-allocation prevention).
Booking, Maintenance, Audit, Reports, Notifications, Activity Logs, and AI features
are the next phases — this foundation is built so those modules plug in cleanly.

## Tech Stack

- Java 17, Spring Boot 3.3
- Spring Security + JWT (jjwt 0.12)
- Spring Data JPA / Hibernate
- MySQL
- Lombok, ModelMapper
- springdoc-openapi (Swagger UI)

## Prerequisites

- JDK 17
- Maven 3.9+ (or use VS Code's bundled Maven support)
- MySQL 8+ running locally (or via Docker)

## 1. Create the database

```bash
mysql -u root -p -e "CREATE DATABASE assetflow;"
```

(The connection URL also has `createDatabaseIfNotExist=true`, so this step is
optional — MySQL will create the schema automatically on first run.)

Or with Docker:

```bash
docker run --name assetflow-db -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=assetflow -p 3306:3306 -d mysql:8
```

## 2. Configure

Defaults live in `src/main/resources/application.yml` and read from env vars:

| Variable      | Default    |
|---------------|------------|
| `DB_USERNAME`  | `root` |
| `DB_PASSWORD`  | `root` |
| `JWT_SECRET`   | (a dev default is baked in — **change this before any real deployment**) |

## 3. Run

```bash
mvn spring-boot:run
```

The app starts on `http://localhost:8080`. Hibernate `ddl-auto: update` will create
all tables automatically on first run.

Swagger UI: `http://localhost:8080/swagger-ui.html`

## 4. Bootstrap your first admin

Signup always creates an `EMPLOYEE` account (this is intentional — nobody can
self-register as ADMIN). To get your first admin:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"System Admin","email":"admin@assetflow.ai","password":"Password123!"}'
```

Then promote that one account directly in the database (one-time bootstrap step —
see `src/main/resources/data-sample.sql` for the exact SQL and further sample data
for departments, categories, and demo assets).

After that, use `PATCH /api/users/{id}/role` (as an ADMIN) to promote anyone else —
never touch the database directly again.

## 5. Try the core flow

1. `POST /api/auth/login` → get a JWT
2. Use `Authorization: Bearer <token>` on subsequent requests (or click **Authorize**
   in Swagger UI)
3. `POST /api/asset-categories` (ADMIN/ASSET_MANAGER) → create a category
4. `POST /api/assets` → register an asset (asset code like `AF-0001` is auto-generated)
5. `POST /api/allocations` → allocate it to a user
   - Try allocating the **same asset again** → you'll get `409 Conflict`, proving the
     duplicate-allocation guard works
6. `POST /api/allocations/{id}/return` → return it, asset flips back to `AVAILABLE`

## Roles

- `ADMIN` — full access, only role that can promote/demote users
- `ASSET_MANAGER` — manages categories, assets, allocations
- `DEPARTMENT_HEAD` — can allocate/return within their scope
- `EMPLOYEE` — default signup role, read access + can request things (future modules)

## Project Structure

```
src/main/java/com/assetflow/
├── config/         JPA auditing, ModelMapper, Security, Swagger/OpenAPI beans
├── controller/      REST endpoints (Auth, Users, Departments, AssetCategories, Assets, Allocations)
├── dto/             Request/response objects — entities are never exposed directly
├── entity/          JPA entities (User, Department, AssetCategory, Asset, Allocation + enums)
├── exception/        Global exception handler + custom exceptions
├── repository/      Spring Data JPA repositories
├── security/         JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
└── service/          Business logic layer
```

## What's next (not yet built)

- Booking module (rooms/vehicles/equipment, conflict detection)
- Maintenance workflow + AI triage assistant
- Audit cycles + discrepancy reports
- Notifications (in-app + email) and scheduled reminders
- Activity log (who/what/when/old-value/new-value/IP)
- Reports (PDF/Excel export)
- AI: asset health score engine, predictive maintenance, chat assistant
- React frontend (Odoo-style dashboard, sidebar, charts, QR display)

Say the word and I'll build the next module the same way — real, working code you
can drop straight in.
