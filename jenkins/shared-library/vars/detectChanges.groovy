// vars/detectChanges.groovy
// ─── Detect changed ShopMicro services ──────────────────────────────
// Returns map of changed service paths.
// If a shared pom.xml changes → rebuild all services.

def call(String baseRef = 'HEAD~1') {
    def changedFiles = bat(
        script: "git diff --name-only ${baseRef}...HEAD 2>nul || git diff --name-only HEAD~1",
        returnStdout: true
    ).trim().readLines().drop(1).join('\n').split('\n').toList()

    echo "Changed files: ${changedFiles}"

    def allServices = [
        'microservices-backend/eureka-server',
        'microservices-backend/api-gateway',
        'microservices-backend/auth-service',
        'microservices-backend/product-service',
        'microservices-backend/inventory-service',
        'microservices-backend/cart-service',
        'microservices-backend/order-service',
        'microservices-backend/payment-service',
        'microservices-backend/notification-service',
    ]

    def services  = []
    def rootPomChanged = changedFiles.contains('pom.xml') ||
                         changedFiles.any { it.startsWith('microservices-backend/pom.xml') }

    // If root pom changed → rebuild everything
    if (rootPomChanged) {
        services = allServices
    } else {
        allServices.each { svc ->
            if (changedFiles.any { it.startsWith("${svc}/") }) {
                services << svc
            }
        }
    }

    def frontendChanged = changedFiles.any { it.startsWith('microservices-app/') }

    return [
        services:        services,
        frontendChanged: frontendChanged,
        hasJava:         services.size() > 0,
        total:           services.size() + (frontendChanged ? 1 : 0)
    ]
}
