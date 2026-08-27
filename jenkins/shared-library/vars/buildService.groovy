// vars/buildService.groovy
// ─── Build a single Spring Boot microservice on Windows ───────────────
// Maven clean verify → JaCoCo → stash JAR for deploy stage.

def call(Map config) {
    def servicePath = config.servicePath
    def serviceName = servicePath.split('/').last()
    def version     = config.version ?: env.VERSION ?: '0.0.1'

    echo "Building service: ${serviceName} @ ${servicePath}"

    dir(servicePath) {
        bat """
            mvn -B clean verify ^
                -Drevision=${version} ^
                -Djacoco.destFile=target/jacoco.exec ^
                -Dmaven.test.failure.ignore=false ^
                -q
        """
    }

    // Publish test results
    junit allowEmptyResults: true,
          testResults: "${servicePath}/target/surefire-reports/*.xml"

    // Publish coverage
    jacoco(
        execPattern:   "${servicePath}/target/jacoco.exec",
        classPattern:  "${servicePath}/target/classes",
        sourcePattern: "${servicePath}/src/main/java"
    )

    // Stash JAR for deploy stage
    stash name: "jar-${serviceName}",
          includes: "${servicePath}/target/*.jar"

    echo "✅ Built: ${serviceName}"
}
