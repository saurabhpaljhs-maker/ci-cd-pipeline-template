#!/usr/bin/env groovy
// Shared Library: deployToK8s.groovy
// Usage: deployToK8s(appName: 'demo-app', image: 'myuser/demo-app:1.0', namespace: 'production')

def call(Map config) {
    def appName    = config.appName
    def image      = config.image
    def namespace  = config.namespace  ?: 'default'
    def manifestDir = config.manifestDir ?: 'k8s'
    def kubeCredId  = config.kubeCredId  ?: 'k8s-kubeconfig'
    def timeout     = config.timeout     ?: '120s'

    withKubeConfig([credentialsId: kubeCredId]) {
        sh """
            sed -i 's|IMAGE_PLACEHOLDER|${image}|g' ${manifestDir}/deployment.yaml
            kubectl apply -f ${manifestDir}/ -n ${namespace}
            kubectl rollout status deployment/${appName} -n ${namespace} --timeout=${timeout}
        """
        echo "Deployed ${appName} to ${namespace}"
    }
}
