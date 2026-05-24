#!/usr/bin/env groovy
// Shared Library: dockerBuildPush.groovy
// Usage: dockerBuildPush(registry: 'myuser', imageName: 'app', tag: '1.0', credId: 'dockerhub-credentials')

def call(Map config) {
    def registry   = config.registry
    def imageName  = config.imageName
    def tag        = config.tag        ?: 'latest'
    def dockerfile = config.dockerfile ?: 'docker/Dockerfile'
    def credId     = config.credId     ?: 'dockerhub-credentials'
    def fullImage  = "${registry}/${imageName}"

    stage('Docker Build') {
        sh "docker build -t ${fullImage}:${tag} -t ${fullImage}:latest -f ${dockerfile} ."
    }

    stage('Docker Push') {
        docker.withRegistry('https://registry.hub.docker.com', credId) {
            sh "docker push ${fullImage}:${tag}"
            sh "docker push ${fullImage}:latest"
        }
        echo "Pushed: ${fullImage}:${tag}"
    }
}
