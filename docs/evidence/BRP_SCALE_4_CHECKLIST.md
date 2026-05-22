# BRP Scale 4 Evidence Checklist

This checklist maps the implementation to the BRP Pemrograman Lanjut scale-4 criteria without changing the deployed API behavior.

## Behavior Preservation

- Public product endpoints remain unchanged:
  - `POST /api/products/create`
  - `GET /api/products/list`
  - `GET /api/products/{id}`
  - `PUT /api/products/update/{id}`
  - `DELETE /api/products/delete/{id}`
  - `GET /api/products/search`
  - `GET /api/products/my-catalog`
  - `GET /api/products/jastiper/{jastiperId}`
  - `POST /api/products/{id}/reserve`
  - `POST /api/products/{id}/release`
- Controller response bodies and status codes are preserved.
- The deployment workflow now smoke-tests the new container before stopping the active container.

## Software Design

- Strategy pattern: `StockValidationStrategy` and `DefaultStockValidationStrategy`.
- Observer/event-driven pattern: `ProductCreatedEvent` and `ProductEventListener`.
- Factory pattern: `ProductFactory` centralizes product creation while preserving the same field values.
- Before/after evidence to keep for review:
  - Before: direct product construction and inline stock quantity validation.
  - After: factory-created products, strategy-based validation, async event observer.

## Software Quality

- Unit and integration tests are expected to pass with:

```bash
JAVA_HOME=/Users/nandapascua/Library/Java/JavaVirtualMachines/ms-21.0.10/Contents/Home ./gradlew clean test jacocoTestReport
```

- SonarCloud and JaCoCo are configured in `build.gradle.kts` and CI.
- Profiling evidence:
  - `PROFILING.md` documents the critical read path and before/after latency.
  - `k6-load-test.js` runs a safe read-only load test against `GET /api/products/list`.

## Software Architecture

- Atomic stock updates prevent overselling under concurrent reservation requests.
- Async product-created observer demonstrates an event-driven extension point.
- Load testing can simulate the architecture benefit:

```bash
BASE_URL=http://localhost:4002/api/products k6 run k6-load-test.js
```

## Software Deployment

- CI/CD builds, tests, scans, builds Docker image, pushes image, and deploys via SSH.
- Deployment includes automated database readiness checks and Flyway migration support.
- Deployment strategy:
  - Start a smoke-test container with the new image.
  - Verify `/actuator/health`.
  - Stop and replace the active container only after smoke test passes.
  - Abort deploy while keeping the old container active when smoke test fails.

## Monitoring and Observability

- Actuator exposes `health`, `info`, and `prometheus`.
- Optional local monitoring stack:

```bash
docker compose --profile observability up --build
```

- Prometheus scrapes `/actuator/prometheus`.
- Grafana dashboard tracks:
  - HTTP average latency
  - HTTP throughput
  - APDEX proxy
  - HikariCP active/idle database connections

## Remaining External Evidence To Capture

- Screenshot or exported report from the successful GitHub Actions run.
- SonarCloud quality gate and coverage report showing target score.
- k6 before/after output attached to the final submission.
- Grafana screenshot showing app and database metrics during load test.
- If the frontend is graded with this backend, attach Lighthouse and Microsoft Clarity evidence from the deployed frontend flow that uses these APIs.
