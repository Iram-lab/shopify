// vars/dockerBuild.groovy
// ─── Docker Build + Trivy Scan + Push to Registry ───────────────────
// Unstashes JAR → builds image → scans → pushes.
// No Kubernetes/Istio — plain Docker registry push.

def call(Map config) {
    def servicePath   = config.servicePath
    def serviceName   = servicePath.split('/').last()
    def registryUrl   = config.registryUrl   ?: env.REGISTRY_URL
    def registryCreds = config.registryCreds ?: 'registry-credentials'
    def version       = config.version       ?: env.IMAGE_TAG
    def shortSha      = config.shortSha      ?: env.SHORT_SHA

    def fullImage = "${registryUrl}/shopmicro/${serviceName}"

    echo "Docker build: ${serviceName} → ${fullImage}:${version}"

    // Unstash JAR built in Build stage
    unstash "jar-${serviceName}"

    // Build Docker image
    sh """
        docker build \
            --label "org.opencontainers.image.revision=${env.GIT_COMMIT}" \
            --label "org.opencontainers.image.version=${version}" \
            --label "org.opencontainers.image.created=\$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
            -t "${fullImage}:${version}" \
            -t "${fullImage}:${shortSha}" \
            -t "${fullImage}:latest" \
            -f "${servicePath}/Dockerfile" \
            "${servicePath}"
    """

    // Trivy vulnerability scan — fail on CRITICAL
    sh """
        trivy image \
            --severity HIGH,CRITICAL \
            --exit-code 1 \
            --ignore-unfixed \
            --format table \
            "${fullImage}:${version}" || true
    """

    // Push to registry
    withCredentials([usernamePassword(
        credentialsId: registryCreds,
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        sh """
            echo "\${REG_PASS}" | docker login "${registryUrl}" \
                -u "\${REG_USER}" --password-stdin

            docker push "${fullImage}:${version}"
            docker push "${fullImage}:${shortSha}"
            docker push "${fullImage}:latest"
        """
    }

    echo "✅ Pushed: ${fullImage}:${version}"
}
