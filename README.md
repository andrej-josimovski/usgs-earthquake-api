# Earthquake Tracker
 
A small full-stack application that fetches, filters, stores, and visualizes recent
earthquake data from the USGS public GeoJSON feed.
 
**Backend:** Java 17, Spring Boot 4.1, Spring Data JPA, PostgreSQL
**Frontend:** React + Vite, Axios, React-Leaflet (map view)
**Data source:** [USGS Earthquake GeoJSON feed](https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson)
 
---
 
## 1. Project Setup Instructions
 
### Prerequisites
 
- Java 17+
- Maven (or use the included `mvnw` wrapper)
- Node.js 18+ and npm
- Docker (for the PostgreSQL database)
### Clone the repository
 
```bash
git clone https://github.com/andrej-josimovski/usgs-earthquake-api.git
cd usgs-earthquake-api
```
 
The project is split into two folders:
 
```
earthquake-backend/    ← Spring Boot REST API
earthquake-frontend/   ← React + Vite client
```
 
---
 
## 2. Database Configuration
 
PostgreSQL runs in Docker via `docker-compose.yml` (in `earthquake-backend/`):
 
```bash
cd earthquake-backend
docker compose up -d
```
 
This starts a Postgres 16 container with:
 
| Setting  | Value           |
|----------|-----------------|
| Database | `earthquake_db` |
| User     | `earthquake_user` |
| Password | `earthquake_pass` |
| Port     | `5432`          |
 
Data persists in a named Docker volume (`earthquake_pgdata`), so it survives container restarts.
 
The application's `src/main/resources/application.properties` is already configured to connect to
this container (`jdbc:postgresql://localhost:5432/earthquake_db`). No manual schema setup is
needed, Hibernate creates/updates the `earthquakes` table automatically on startup
(`spring.jpa.hibernate.ddl-auto=update`).
 
---
 
## 3. Running the Backend
 
With the Postgres container running:
 
```bash
cd earthquake-backend
./mvnw spring-boot:run
```
 
The API starts on `http://localhost:8080`.
 
### Available endpoints
 
| Method | Endpoint                     | Description                                              |
|--------|-------------------------------|------------------------------------------------------------|
| POST   | `/api/earthquakes/fetch`      | Fetches the latest data from USGS, parses it, and stores all valid records (replacing the previous dataset) |
| GET    | `/api/earthquakes`            | Returns all stored earthquakes                            |
| GET    | `/api/earthquakes/filter`     | Returns stored earthquakes filtered by `minMagnitude` and/or `after` (ISO-8601 UTC, e.g. `2026-04-15T10:30:00Z`) |
| DELETE | `/api/earthquakes/{id}`       | Deletes a specific earthquake record by id                |
 
### Running the tests
 
```bash
./mvnw test
```
 
This runs both the unit tests (parsing/filtering logic, isolated with Mockito) and the
integration tests (full `fetchAndStore` / `getFiltered` flow through a real Spring context and
an in-memory H2 database, configured separately in `src/test/resources/application.properties`).
 
---
 
## 4. Running the Frontend
 
```bash
cd earthquake-frontend
npm install
npm run dev
```
 
The app starts on `http://localhost:5173` (default Vite port) and talks to the backend at
`http://localhost:8080/api`.
 
It provides:
 
- A **table view** of stored earthquakes (magnitude, place, title, time)
- A **map view** (Leaflet/OpenStreetMap) plotting earthquakes by latitude/longitude
- Filtering controls (minimum magnitude, after a given time)
- A button to trigger `fetch` from USGS, and per-row delete
---
 
 
## 5. Optional Improvements Implemented
 
- **Centralized exception handling** via `@RestControllerAdvice` — every custom exception
  (`ApiUnavailableException`, `InvalidGeoJsonException`, `DatabaseException`,
  `EarthquakeNotFoundException`, `InvalidDateFormatException`) is mapped to an appropriate HTTP
  status code and a clean JSON error body, instead of a default stack trace response.
- **Layered service architecture** — a `domain` service (business rules, parsing, repository
  access) and an `application` service (orchestration: HTTP client → domain service → DTO
  mapping), keeping the controller thin.
- **DTO separation** — the JPA entity is never exposed directly over REST; a `DisplayEarthquakeDto`
  is used for all outbound responses.
- **Unit tests** for GeoJSON parsing and filtering logic, covering edge cases: missing fields,
  non-numeric values, malformed JSON, missing geometry.
- **Integration tests** for the full `fetchAndStore()` / `getFiltered()` flow using a mocked
  external client (`UsgsEarthquakeClient`) and a real Spring context backed by an in-memory H2
  database.
- **Map visualization** using React-Leaflet, plotting stored earthquakes
  by coordinates with popups showing magnitude, place, and time.
---
