// vars/deployCompose.groovy
// ─── Deploy via docker-compose ───────────────────────────────────────
// No Kubernetes/Istio — Spring Cloud Eureka handles service discovery.
// Pulls new images, restarts services one by one (rolling) or all at once.

def call(Map config) {
    def environment  = config.environment
    def version      = config.version      ?: env.IMAGE_TAG
    def composeFile  = config.composeFile  ?: "infra/docker-compose.${environment}.yml"
    def rolling      = config.rolling      ?: false

    echo "Deploying to ${environment} with version ${version}"
    echo "Compose file: ${composeFile}"

    withCredentials([usernamePassword(
        credentialsId: 'registry-credentials',
        usernameVariable: 'REG_USER',
        passwordVariable: 'REG_PASS'
    )]) {
        sh """
            echo "\${REG_PASS}" | docker login "\${REGISTRY_URL}" \
                -u "\${REG_USER}" --password-stdin
        """
    }

    // Export version so docker-compose picks it up
    withEnv(["IMAGE_TAG=${version}", "APP_ENV=${environment}"]) {

        // Pull latest images
        sh "docker-compose -f ${composeFile} pull"

        if (rolling) {
            // Rolling restart: restart each service individually
            // so there is no full downtime
            def services = [
                'eureka-server',
                'api-gateway',
                'auth-service',
                'product-service',
                'inventory-service',
                'cart-service',
                'order-service',
                'payment-service',
                'notification-service'
            ]

            services.each { svc ->
                echo "Rolling restart: ${svc}"
                sh """
                    docker-compose -f ${composeFile} up -d --no-deps --force-recreate ${svc}
                    sleep 10
                """
            }
        } else {
            // Full restart (dev/qa)
            sh """
                docker-compose -f ${composeFile} up -d --force-recreate --remove-orphans
            """
        }
    }

    echo "✅ Deployed ${environment} with version ${version}"
}
