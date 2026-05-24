# cicd-pipeline-templates

> Production-ready Jenkins CI/CD pipeline templates for Java/Maven applications with Docker, Kubernetes deployment, Slack notifications and automated rollback.

![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)

---

## 📁 Repository Structure

```
cicd-pipeline-templates/
├── app/src/main/java/com/devops/demo/App.java
├── app/src/test/java/com/devops/demo/AppTest.java
├── pom.xml
├── jenkins/
│   ├── pipelines/
│   │   ├── Jenkinsfile.ci        # CI only: build + test + scan
│   │   ├── Jenkinsfile.cd        # CD only: deploy to K8s
│   │   └── Jenkinsfile.full      # Full CI/CD all-in-one
│   └── shared-library/vars/
│       ├── buildMaven.groovy
│       ├── dockerBuildPush.groovy
│       ├── deployToK8s.groovy
│       ├── slackNotify.groovy
│       └── rollback.groovy
├── docker/Dockerfile
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   └── configmap.yaml
├── scripts/
│   ├── setup-jenkins.sh
│   └── rollback.sh
└── docs/architecture.md
```

---

## 🏗️ Architecture

```
Developer Push
      │
      ▼ Webhook trigger
  Jenkins Master
      │
      ├── Stage 1: Checkout
      ├── Stage 2: Maven Build & Test
      ├── Stage 3: SonarQube Code Scan
      ├── Stage 4: Docker Build & Push → DockerHub/ECR
      ├── Stage 5: Deploy to K8s (kubectl apply)
      ├── Stage 6: Health Check
      └── Stage 7: Slack Notification
                        │
                   (on failure)
                        ▼
                  Auto Rollback
```

---

## 🚀 Pipelines

| Pipeline | File | Purpose |
|---|---|---|
| CI Only | `Jenkinsfile.ci` | Build, test, scan — no deploy |
| CD Only | `Jenkinsfile.cd` | Deploy existing image to K8s |
| Full CI/CD | `Jenkinsfile.full` | End-to-end: build → test → push → deploy |

---

## ⚙️ Prerequisites

| Tool | Version |
|---|---|
| Jenkins | 2.400+ |
| Java | 17+ |
| Maven | 3.9+ |
| Docker | 24+ |
| kubectl | 1.28+ |
| SonarQube | 9+ (optional) |

### Jenkins Plugins Required
- Pipeline, Git, Docker Pipeline
- Kubernetes CLI, SonarQube Scanner
- Slack Notification, Blue Ocean (optional)

---

## 🔧 Setup

### 1. Configure Jenkins Credentials

| ID | Type | Description |
|---|---|---|
| `dockerhub-credentials` | Username/Password | DockerHub login |
| `k8s-kubeconfig` | Secret file | Kubeconfig for target cluster |
| `sonarqube-token` | Secret text | SonarQube auth token |
| `slack-token` | Secret text | Slack Bot token |

### 2. Create Jenkins Pipeline Job

```
New Item → Pipeline → Pipeline script from SCM
Script Path: jenkins/pipelines/Jenkinsfile.full
```

### 3. Set Environment Variables in Jenkinsfile

```groovy
APP_NAME        = 'demo-app'
DOCKER_REGISTRY = 'your-dockerhub-username'
K8S_NAMESPACE   = 'production'
SLACK_CHANNEL   = '#deployments'
```

---

## 📦 Docker

```bash
docker build -t demo-app:latest -f docker/Dockerfile .
docker run -p 8080:8080 demo-app:latest
```

---

## ☸️ Kubernetes

```bash
kubectl apply -f k8s/ -n production
kubectl rollout status deployment/demo-app -n production
# Rollback:
kubectl rollout undo deployment/demo-app -n production
```

---

## 🔔 Slack Notifications

- ✅ SUCCESS — build number, image tag, deploy time
- ❌ FAILURE — failing stage, error log link, rollback status
- ⚠️ UNSTABLE — test failures or quality gate warnings

---

## 🔄 Rollback

Auto-triggers when health check fails post-deploy. Manual:
```bash
./scripts/rollback.sh <previous-image-tag> <namespace>
```

---

## 👨‍💻 Author

**Your Name** — DevOps Engineer | [LinkedIn](https://linkedin.com/in/yourprofile) | [GitHub](https://github.com/yourusername)
