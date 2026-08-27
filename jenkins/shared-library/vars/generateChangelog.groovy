// vars/generateChangelog.groovy
// ─── Generate CHANGELOG from git log ────────────────────────────────

def call(Map config) {
    def version = config.version ?: env.VERSION ?: 'unknown'

    def log = bat(
        script: 'git log --oneline --no-merges HEAD~5..HEAD 2>nul',
        returnStdout: true
    ).trim().readLines().drop(1).join('\n') ?: 'No changes listed'

    def changelog = """
## ShopMicro Release ${version}
**Date:** ${new Date().format('yyyy-MM-dd')}
**Build:** #${env.BUILD_NUMBER}
**Commit:** ${env.GIT_COMMIT?.take(7) ?: 'N/A'}

### Changes
${log}
"""

    writeFile file: "CHANGELOG-${version}.md", text: changelog
    archiveArtifacts artifacts: "CHANGELOG-${version}.md"
    echo "✅ Changelog generated for ${version}"
}
