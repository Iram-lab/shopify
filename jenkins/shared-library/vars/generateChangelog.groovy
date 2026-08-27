// vars/generateChangelog.groovy
// ─── Generate CHANGELOG from git log ────────────────────────────────

def call(Map config) {
    def version = config.version ?: env.VERSION ?: 'unknown'

    def log = sh(
        script: """
            git log --oneline --no-merges \
                \$(git describe --tags --abbrev=0 HEAD^ 2>/dev/null || echo "")..HEAD \
                2>/dev/null | head -50 || echo "No previous tag found"
        """,
        returnStdout: true
    ).trim()

    def changelog = """
## ShopMicro Release ${version}
**Date:** ${new Date().format('yyyy-MM-dd')}
**Build:** #${env.BUILD_NUMBER}
**Commit:** ${env.GIT_COMMIT?.take(7) ?: 'N/A'}

### Changes
${log ?: 'No changes listed'}
"""

    writeFile file: "CHANGELOG-${version}.md", text: changelog
    archiveArtifacts artifacts: "CHANGELOG-${version}.md"
    echo "✅ Changelog generated for ${version}"
}
