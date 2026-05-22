# Profiling and Optimization Report

## 1. Background
During load testing and system monitoring, we identified that the `getAllProducts()` and `getProductById()` methods were a significant bottleneck. Each read operation required a direct trip to the PostgreSQL database.

## 2. Methodology (Profiling)
We utilized **VisualVM** and **k6** to profile the application under load (20 concurrent users for 1 minute). The k6 script uses the existing read-only API behavior:

```bash
BASE_URL=http://localhost:4002/api/products k6 run k6-load-test.js
```

**Before Optimization:**
- `getAllProducts()` Average Latency: ~140ms
- `getProductById()` Average Latency: ~65ms
- DB Connections: Maxed out under heavy load.
- CPU Usage: ~45% average.

## 3. Optimization Strategy
To optimize this critical path (Non-Functional Requirement), we implemented the following:
1. **Spring Cache (`@EnableCaching`)**: We added a caching layer to our application.
2. **`@Cacheable` & `@CacheEvict`**: Applied to read and write methods respectively in `ProductServiceImpl`.

## 4. Results (After Optimization)
We re-ran the identical **k6** load test.

**After Optimization:**
- `getAllProducts()` Average Latency: ~15ms (An **89% improvement**)
- `getProductById()` Average Latency: ~8ms (An **87% improvement**)
- DB Connections: Reduced by >90% for read-heavy operations.
- CPU Usage: ~12% average.

## 5. Conclusion
The implementation of the caching layer successfully fulfilled the optimization requirement, exceeding the target of a >50% improvement in critical path execution times without altering application behavior.

## 6. Observability Follow-up

The application exposes Prometheus metrics through Spring Boot Actuator at `/actuator/prometheus`. A local Prometheus and Grafana stack is available through the `observability` Docker Compose profile:

```bash
docker compose --profile observability up --build
```

The dashboard in `docs/monitoring/grafana-dashboard.json` tracks HTTP latency, throughput, an APDEX proxy, and database connection pool usage so performance regressions can be monitored during load testing.
