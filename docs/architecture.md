# Pipeline Architecture

## Flow

```
Developer git push
        │
        ▼  Webhook
  Jenkins Master
        │
        ├── Stage 1: Checkout
        ├── Stage 2: Maven Build & Test  ──► JUnit reports + JaCoCo coverage
        ├── Stage 3: SonarQube Scan      ──► Quality Gate (blocks if < 80% coverage)
        ├── Stage 4: Docker Build & Push ──► DockerHub / ECR
        ├── Stage 5: K8s Deploy          ──► kubectl apply + rollout status
        ├── Stage 6: Health Check        ──► curl /health endpoint
        └── Stage 7: Slack Notify        ──► ✅ success / ❌ failure + auto rollback
```

## Branching Strategy

| Branch | CI | Deploy Target | Approval |
|---|---|---|---|
| `feature/*` | ✅ | — | — |
| `develop` | ✅ | Staging | No |
| `main` | ✅ | Production | Yes |

## Security Practices

- All credentials in Jenkins Credentials Store — never in code
- Docker container runs as non-root user
- K8s resource limits set on all containers
- Quality gate blocks deploy if coverage < 80%
- Rollback triggers automatically on failed health check
