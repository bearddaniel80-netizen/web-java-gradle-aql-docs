# web-java-gradle-aql-docs

## Description
A secured api/frontend for the AQL platform.
Features
- Getting-Started
- Query-Language
- Examples
- Sources
- Script

---

## Endpoints
These endpoint can also be reached with 
```bash
curl -ikL localhost
```
while services are running.

### /api/examples/list
Displays all possible files in examples directory.

### /api/examples/{filename}
Shows individual files

### /api/scripts/list
Displays all possible files in scripts directory.

### /api/scripts/{filename}
Shows individual files

### /api/sources/list
Displays all possible files in sources directory.

### /api/sources/{filename}
Shows individual files

### /api/{filenames}
Shows `getting-started` and `query-langage` files.

---

## To run

### Start services
```bash
docker compose up --build
```

### View frontend
[localhost](https://localhost)

### Health check
```bash
curl -ikL localhost/health
```

### Call API
```bash
curl -ikL localhost/api/data
```

### Run playwrite tests
```bash
docker compose --profile test up
```

### Stop services
```bash
docker compose down
```

### Cleanup docker
```bash
docker volume prune && docker system prune
```
---

## Services

### Caddy
Reverse proxy with additional rate limit plugin. Manages self-signed ssl certificates.

### FastAPI
A Java Spring REST api only gateway protected by docker secrets file.

### Frontend
A Java / React simple page that displays api data.

### BFF
A Java Spring BFF (backend for frontend) gateway protected by docker secrets file.

### Playwrite
Tests browser and api.

---

## Creating docker secrets
```bash
openssl rand -hex 32
```

---

## How docker secrets are shared
```text
             Docker secret
                  │
          ┌───────┴────────┐
          ▼                ▼
       Node.js           FastAPI
          │                │
          └── same key ────┘
```

---
## Network Flow

```text
                  PUBLIC
        ┌───────────┼───────────┐
        │           │           │
      Caddy      frontend     MinIO
        │                       │
        │                       │
        └───────────────────────┘

                  BACKEND
        ┌───────────┼───────────┐
        │           │           │
       Node      FastAPI       MinIO
```
---

## System Flow

```mermaid
flowchart TB
    %% Application Architecture
    node_0["Browser"]
    node_1["Frontend"]
    node_0 --> node_1
    node_2["Caddy"]
    node_1 --> node_2
    node_3["Node"]
    node_2 --> node_3
    node_4["FastAPI"]
    node_3 --> node_4
```

## Reference
This is a web implementation of a [CLI tool](https://github.com/bearddaniel80-netizen/aql-public.git).