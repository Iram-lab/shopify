// ─── ShopMicro Root Jenkinsfile ──────────────────────────────────────
// Jenkins auto-discovers this file from the repo root.
// Routes to the correct pipeline based on branch or PR context.
//
// Pipeline flow:
//   PR branch    → Jenkinsfile.pr-validation  (gates: build, test, SAST, Sonar)
//   main branch  → Jenkinsfile.ci-main        (version, build, docker, push)
//   ci-main done → Jenkinsfile.deploy-dev     (auto-triggered)
//   dev done     → Jenkinsfile.deploy-qa      (auto-triggered)
//   qa approved  → Jenkinsfile.deploy-prod    (manual approval required)
//
// Spring Cloud Eureka handles service discovery — NO Istio, NO Kubernetes.
// ─────────────────────────────────────────────────────────────────────

// This root Jenkinsfile just delegates to the correct pipeline file.
// In Jenkins, configure separate pipeline jobs pointing to each Jenkinsfile.

pipeline {
    agent none

    stages {
        stage('Route Pipeline') {
            steps {
                script {
                    if (env.CHANGE_ID) {
                        // Pull Request
                        echo "PR #${env.CHANGE_ID} detected → running PR validation"
                        load 'jenkins/pipelines/Jenkinsfile.pr-validation'
                    } else if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'master') {
                        // Merge to main
                        echo "Main branch push → running CI main pipeline"
                        load 'jenkins/pipelines/Jenkinsfile.ci-main'
                    } else {
                        echo "Branch: ${env.BRANCH_NAME} — no pipeline configured for this branch"
                        currentBuild.result = 'NOT_BUILT'
                    }
                }
            }
        }
    }
}
