#!/bin/bash
# ============================================================
# Jenkins Plugin Installer Script
# Run this after Jenkins is installed to install required plugins
# Usage: ./scripts/setup-jenkins.sh <jenkins-url> <admin-user> <admin-password>
# ============================================================
set -euo pipefail

JENKINS_URL="${1:-http://localhost:8080}"
JENKINS_USER="${2:-admin}"
JENKINS_PASS="${3:-admin}"
CLI_JAR="/tmp/jenkins-cli.jar"

REQUIRED_PLUGINS=(
  "pipeline-stage-view"
  "workflow-aggregator"
  "git"
  "docker-workflow"
  "kubernetes-cli"
  "sonar"
  "slack"
  "jacoco"
  "htmlpublisher"
  "blueocean"
  "build-timeout"
  "timestamper"
  "ws-cleanup"
)

echo "📦 Downloading Jenkins CLI..."
curl -sL "${JENKINS_URL}/jnlpJars/jenkins-cli.jar" -o "${CLI_JAR}"

echo "🔌 Installing plugins..."
for plugin in "${REQUIRED_PLUGINS[@]}"; do
  echo "  Installing: ${plugin}"
  java -jar "${CLI_JAR}" \
    -s "${JENKINS_URL}" \
    -auth "${JENKINS_USER}:${JENKINS_PASS}" \
    install-plugin "${plugin}" -deploy || echo "  ⚠️  Warning: ${plugin} may already be installed"
done

echo "🔄 Restarting Jenkins to activate plugins..."
java -jar "${CLI_JAR}" \
  -s "${JENKINS_URL}" \
  -auth "${JENKINS_USER}:${JENKINS_PASS}" \
  safe-restart

echo "✅ Done! Jenkins will restart with all plugins active."
