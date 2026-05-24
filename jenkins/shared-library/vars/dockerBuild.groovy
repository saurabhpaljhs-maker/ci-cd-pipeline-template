// jenkins/shared-library/vars/dockerBuild.groovy
// Reusable Docker build + push function
// Usage: dockerBuild registry: 'docker.io/user', appName: 'myapp', tag: '42-abc1234'

def call(Map config = [:]) {
    def registry    = config.registry    ?: error("registry is required")
    def appName     = config.appName     ?: error("appName is required")
    def tag         = config.tag         ?: env.BUILD_NUMBER
    def dockerfile  = config.dockerfile  ?: 'docker/Dockerfile'
    def context     = config.context     ?: '.'
    def pushLatest  = config.pushLatest  ?: false
    def credentialsId = config.credentialsId ?: 'docker-hub-creds'

    def imageFull = "${registry}/${appName}:${tag}"

    echo "🐳 Building: ${imageFull}"

    sh """
        docker build \
            --build-arg BUILD_DATE=\$(date -u +%Y-%m-%dT%H:%M:%SZ) \
            --build-arg GIT_COMMIT=${env.GIT_COMMIT ?: 'unknown'} \
            --build-arg APP_VERSION=${tag} \
            --label org.opencontainers.image.created=\$(date -u +%Y-%m-%dT%H:%M:%SZ) \
            --label org.opencontainers.image.revision=${env.GIT_COMMIT ?: 'unknown'} \
            --label org.opencontainers.image.version=${tag} \
            -t ${imageFull} \
            -f ${dockerfile} \
            ${context}
    """

    withCredentials([usernamePassword(
        credentialsId: credentialsId,
        usernameVariable: 'DOCKER_USER',
        passwordVariable: 'DOCKER_PASS'
    )]) {
        sh """
            echo \${DOCKER_PASS} | docker login -u \${DOCKER_USER} --password-stdin
            docker push ${imageFull}
        """

        if (pushLatest) {
            sh """
                docker tag ${imageFull} ${registry}/${appName}:latest
                docker push ${registry}/${appName}:latest
            """
        }

        sh "docker logout"
    }

    // Cleanup local image to save disk space
    sh "docker rmi ${imageFull} || true"

    echo "✅ Image pushed: ${imageFull}"
    return imageFull
}
