pipeline {
    agent any

    environment {
        REGISTRY     = 'docker.io/iramnaazbasade'
        IMAGE_TAG    = "${env.BUILD_NUMBER}"
        COMPOSE_FILE = 'infra/docker-compose.dev.yml'
        JAVA_HOME    = tool name: 'jdk-21',    type: 'jdk'
        MAVEN_HOME   = tool name: 'maven-3.9', type: 'maven'
        PATH         = "${env.JAVA_HOME}/bin;${env.MAVEN_HOME}/bin;C:\\Users\\iramnaaz.basade\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
    }

    options {
        timeout(time: 180, unit: 'MINUTES')
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
                    def services = [
                        'eureka-server', 'api-gateway', 'auth-service',
                        'product-service', 'inventory-service', 'cart-service',
                        'order-service', 'payment-service', 'notification-service'
                    ]
                    services.each { svc ->
                        dir("microservices-backend/${svc}") {
                            retry(3) {
                                bat "docker build -t ${env.REGISTRY}/${svc}:${env.IMAGE_TAG} ."
                                bat "docker push ${env.REGISTRY}/${svc}:${env.IMAGE_TAG}"
                            }
                            echo "Pushed: ${env.REGISTRY}/${svc}:${env.IMAGE_TAG}"
                        }
                    }
                    dir('microservices-app') {
                        retry(3) {
                            bat "docker build --no-cache --pull -t ${env.REGISTRY}/frontend:${env.IMAGE_TAG} ."
                            bat "docker push ${env.REGISTRY}/frontend:${env.IMAGE_TAG}"
                        }
                        echo "Pushed: ${env.REGISTRY}/frontend:${env.IMAGE_TAG}"
                    }
                }
            }
        }

        // ── 6. Stop old containers ───────────────────────────────────
        stage('Stop Old Containers') {
            steps {
                bat "docker-compose -f ${env.COMPOSE_FILE} down --remove-orphans 2>nul || echo No containers running"
                bat "docker image prune -f 2>nul || echo No dangling images"
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
                echo "Waiting 120s for services to start..."
                sleep(time: 120, unit: 'SECONDS')
            }
        }

        // ── 8. Health check ──────────────────────────────────────────
        stage('Health Check') {
            steps {
                script {
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
                        def curlCmd = svc.name == 'eureka-server'
                            ? "curl -s -o nul -w \"%{http_code}\" -u admin:admin123 http://localhost:${svc.port}/actuator/health --max-time 10 --connect-timeout 5 2>nul || echo 000"
                            : "curl -s -o nul -w \"%{http_code}\" http://localhost:${svc.port}/actuator/health --max-time 10 --connect-timeout 5 2>nul || echo 000"
                        def status = bat(script: curlCmd, returnStdout: true).trim().readLines().last()
                        echo status == '200' ? "UP: ${svc.name}" : "WARNING: ${svc.name} returned ${status}"
                    }
                    def frontendStatus = bat(
                        script: "curl -s -o nul -w \"%{http_code}\" http://localhost:80 --max-time 10 --connect-timeout 5 2>nul || echo 000",
                        returnStdout: true
                    ).trim().readLines().last()
                    echo frontendStatus == '200' ? "UP: frontend" : "WARNING: frontend returned ${frontendStatus}"
                }
            }
        }
    }

    post {
        success {
            echo "SUCCESS - Build ${env.BUILD_NUMBER} deployed"
            echo "Frontend:    http://shopify.local"
            echo "API Gateway: http://shopify.local/api/"
            echo "Eureka:      http://shopify.local/eureka/"
        }
        failure {
            echo "FAILED - check Docker Desktop for container logs"
        }
    }
}
