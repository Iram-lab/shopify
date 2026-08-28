// vars/buildService.groovy
// ─── Build a single Spring Boot microservice ──────────────────────────
// Flow: Maven clean package → Docker build → Docker push to local registry

def call(Map config) {
    def servicePath = config.servicePath
    def serviceName = servicePath.split('/').last()
    def version     = config.version ?: env.VERSION ?: '0.0.1'
    def registry    = env.REGISTRY_URL ?: 'localhost:5000'
    def imageTag    = "${registry}/shopmicro/${serviceName}:${version}"

    echo "Building service: ${serviceName} @ ${servicePath}"

    dir(servicePath) {
        // ── Maven build ──
        bat """
            mvn -B clean package ^
                -Drevision=${version} ^
                -DskipTests=false ^
                -Dmaven.test.failure.ignore=false ^
                -q
        """

        // ── Docker build ──
        bat "docker build -t ${imageTag} ."

        // ── Docker push to local registry ──
        bat "docker push ${imageTag}"
    }

    // Publish test results
    junit allowEmptyResults: true,
          testResults: "${servicePath}/target/surefire-reports/*.xml"

    echo "✅ Built and pushed: ${imageTag}"
}
