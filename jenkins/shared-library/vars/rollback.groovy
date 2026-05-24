#!/usr/bin/env groovy
// Shared Library: rollback.groovy
// Usage: rollback(appName: 'demo-app', namespace: 'production')

def call(Map config) {
    def appName    = config.appName
    def namespace  = config.namespace  ?: 'default'
    def kubeCredId = config.kubeCredId ?: 'k8s-kubeconfig'

    echo "🔄 Rolling back ${appName} in ${namespace}..."
    withKubeConfig([credentialsId: kubeCredId]) {
        sh """
            kubectl rollout undo deployment/${appName} -n ${namespace}
            kubectl rollout status deployment/${appName} -n ${namespace} --timeout=60s
            echo "Rollback complete for ${appName}"
        """
    }
}
