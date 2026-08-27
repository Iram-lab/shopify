@echo off
echo ─────────────────────────────────────────
echo  Pushing ShopMicro pipeline changes to GitHub
echo ─────────────────────────────────────────

cd /d "C:\Users\iramnaaz.basade\Desktop\Microservices"

git add jenkins\pipelines\Jenkinsfile.ci-main
git add jenkins\pipelines\Jenkinsfile.deploy-dev
git add jenkins\pipelines\Jenkinsfile.deploy-qa
git add jenkins\pipelines\Jenkinsfile.deploy-prod
git add jenkins\pipelines\Jenkinsfile.pr-validation
git add jenkins\shared-library\vars\buildService.groovy

git commit -m "ci: rewrite pipelines for no-docker Windows deployment"
git push origin main

echo ─────────────────────────────────────────
echo  Done! Check GitHub to confirm push.
echo ─────────────────────────────────────────
pause
