pipeline {
    agent any

    environment {
        REGISTRY     = 'docker.io/iramlab'
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        COMPOSE_FILE = 'infra/docker-compose.dev.yml'
        JAVA_HOME    = tool name: 'jdk-21',    type: 'jdk'
        MAVEN_HOME   = tool name: 'maven-3.9', type: 'maven'
        PATH         = "${env.JAVA_HOME}/bin;${env.MAVEN_HOME}/bin;${env.PATH}"
    }

    options {
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        // ── 1. Pull latest code from GitHub ─────────────────────────
        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-pat',
                    url: 'https://github.com/Iram-lab/shopify.git'
            }
        }

        // ── 2. Build all 9 backend JARs ──────────────────────────────
        stage('Build JARs') {
            steps {
                script {
                    def services = [
                        'eureka-server', 'api-gateway', 'auth-service',
                        'product-service', 'inventory-service', 'cart-service',
                        'order-service', 'payment-service', 'notification-service'
                    ]
                    services.each { svc ->
                        dir("microservices-backend/${svc}") {
                            bat 'mvn clean package -DskipTests -q'
                            echo "Built: ${svc}"
                        }
                    }
                }
            }
        }

        // ── 3. Build Angular frontend ────────────────────────────────
        stage('Build Frontend') {
            steps {
                dir('microservices-app') {
                    bat 'npm ci --prefer-offline --legacy-peer-deps'
                    bat 'npm run build -- --configuration production'
                    echo 'Frontend built'
                }
            }
        }

        // ── 4. Login to Docker Hub ───────────────────────────────────
        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                }
            }
        }

        // ── 5. Docker build + push 9 backend + 1 frontend ───────────
        stage('Docker Build & Push') {
            steps {
                script {
                    // Backend
                    def services = [
                        'eureka-server', 'api-gateway', 'auth-service',
                        'product-service', 'inventory-service', 'cart-service',
                        'order-service', 'payment-service', 'notification-service'
                    ]
                    services.each { svc ->
                        dir("microservices-backend/${svc}") {
                            bat "docker build -t ${env.REGISTRY}/${svc}:${env.IMAGE_TAG} ."
                            bat "docker push ${env.REGISTRY}/${svc}:${env.IMAGE_TAG}"
                            echo "Pushed: ${env.REGISTRY}/${svc}:${env.IMAGE_TAG}"
                        }
                    }
                    // Frontend
                    dir('microservices-app') {
                        bat "docker build -t ${env.REGISTRY}/frontend:${env.IMAGE_TAG} ."
                        bat "docker push ${env.REGISTRY}/frontend:${env.IMAGE_TAG}"
                        echo "Pushed: ${env.REGISTRY}/frontend:${env.IMAGE_TAG}"
                    }
                }
            }
        }

        // ── 6. Stop old containers ───────────────────────────────────
        stage('Stop Old Containers') {
            steps {
                bat "docker-compose -f ${env.COMPOSE_FILE} down --remove-orphans 2>nul || echo No containers running"
            }
        }

        // ── 7. Start all containers ──────────────────────────────────
        stage('Deploy') {
            steps {
                bat """
                    set REGISTRY_URL=${env.REGISTRY}
                    set IMAGE_TAG=${env.IMAGE_TAG}
                    docker-compose -f ${env.COMPOSE_FILE} up -d --force-recreate
                """
                echo "Waiting 60s for services to start..."
                sleep(time: 60, unit: 'SECONDS')
            }
        }

        // ── 8. Health check ──────────────────────────────────────────
        stage('Health Check') {
            steps {
                script {
                    // Backend — check Spring Boot actuator
                    def backendServices = [
                        [name: 'eureka-server',       port: 8761],
                        [name: 'api-gateway',          port: 8080],
                        [name: 'auth-service',         port: 8081],
                        [name: 'product-service',      port: 8082],
                        [name: 'inventory-service',    port: 8083],
                        [name: 'cart-service',         port: 8084],
                        [name: 'payment-service',      port: 8085],
                        [name: 'order-service',        port: 8086],
                        [name: 'notification-service', port: 8087]
                    ]
                    backendServices.each { svc ->
                        def status = bat(
                            script: "curl -s -o nul -w \"%%{http_code}\" http://localhost:${svc.port}/actuator/health --max-time 10 2>nul",
                            returnStdout: true
                        ).trim().readLines().last()
                        echo status == '200' ? "UP: ${svc.name}" : "WARNING: ${svc.name} returned ${status}"
                    }

                    // Frontend — check nginx root (no actuator)
                    def frontendStatus = bat(
                        script: "curl -s -o nul -w \"%%{http_code}\" http://localhost:4200 --max-time 10 2>nul",
                        returnStdout: true
                    ).trim().readLines().last()
                    echo frontendStatus == '200' ? "UP: frontend" : "WARNING: frontend returned ${frontendStatus}"
                }
            }
        }
    }

    post {
        success {
            echo "SUCCESS — Build ${env.IMAGE_TAG} deployed"
            echo "Frontend: http://localhost:4200"
            echo "API Gateway: http://localhost:8080"
            echo "Eureka: http://localhost:8761"
        }
        failure {
            echo "FAILED — Printing logs..."
            bat "docker-compose -f ${env.COMPOSE_FILE} logs --tail=50 2>nul || echo No logs"
        }
        always {
            cleanWs()
        }
    }
}
