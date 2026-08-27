// vars/verifyImages.groovy
// ─── Verify all service images exist in registry ─────────────────────

def call(Map config) {
    def version      = config.version
    def registryUrl  = config.registryUrl  ?: env.REGISTRY_URL
    def registryCreds = config.registryCreds ?: 'registry-credentials'

    def services = [
        'eureka-server', 'api-gateway', 'auth-service',
        'product-service', 'inventory-service', 'cart-service',
        'order-service', 'payment-service', 'notification-service'
    ]

    withCredentials([usernamePassword(
        credentialsId: registryCreds,
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        sh """
            echo "\${REG_PASS}" | docker login "${registryUrl}" \
                -u "\${REG_USER}" --password-stdin
        """

        def failed = 0
        services.each { svc ->
            def image = "${registryUrl}/shopmicro/${svc}:${version}"
            def exists = sh(
                script: "docker manifest inspect '${image}' > /dev/null 2>&1 && echo 'yes' || echo 'no'",
                returnStdout: true
            ).trim()

            if (exists == 'yes') {
                echo "  ✅ ${svc}:${version}"
            } else {
                echo "  ❌ ${svc}:${version} NOT FOUND in registry"
                failed++
            }
        }

        if (failed > 0) {
            error "${failed} image(s) missing in registry for version ${version}"
        }
    }

    echo "✅ All images verified for version ${version}"
}
