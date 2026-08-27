// vars/notifyDeploy.groovy
// ─── Deploy Notifications (Slack + Teams) ────────────────────────────
// Usage:
//   notifyDeploy(status: 'SUCCESS', environment: 'prod', version: '1.2.3')
//   notifyDeploy(status: 'FAILURE', environment: 'dev',  version: '1.2.3')
//   notifyDeploy(status: 'ROLLBACK', environment: 'prod', version: '1.2.3')

def call(Map config) {
    def status      = config.status      ?: 'UNKNOWN'
    def environment = config.environment ?: 'unknown'
    def version     = config.version     ?: 'unknown'
    def duration    = currentBuild.durationString?.replace(' and counting', '') ?: 'unknown'

    def emoji = [SUCCESS: '✅', FAILURE: '❌', ROLLBACK: '🚨', UNSTABLE: '⚠️'][status] ?: '❓'
    def color = [SUCCESS: '#28a745', FAILURE: '#dc3545', ROLLBACK: '#fd7e14', UNSTABLE: '#ffc107'][status] ?: '#6c757d'

    def message = """
${emoji} *ShopMicro Deploy — ${status}*
*Environment:* ${environment}
*Version:* \`${version}\`
*Duration:* ${duration}
*Build:* <${env.BUILD_URL}|#${env.BUILD_NUMBER}>
*Triggered by:* ${currentBuild.getBuildCauses()[0]?.userId ?: 'ci-pipeline'}
*Time:* ${new Date().format('yyyy-MM-dd HH:mm:ss z')}
    """.stripIndent().trim()

    // ── Slack notification ──
    try {
        withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_URL')]) {
            def payload = groovy.json.JsonOutput.toJson([
                channel    : '#shopmicro-deploys',
                username   : 'ShopMicro CI',
                icon_emoji : ':rocket:',
                attachments: [[
                    color : color,
                    text  : message,
                    footer: "Jenkins | ${env.JOB_NAME}",
                    ts    : (System.currentTimeMillis() / 1000).toLong()
                ]]
            ])
            httpRequest(
                url        : SLACK_URL,
                httpMode   : 'POST',
                contentType: 'APPLICATION_JSON',
                requestBody: payload,
                quiet      : true
            )
        }
    } catch (e) {
        echo "Slack notification skipped: ${e.message}"
    }

    // ── Microsoft Teams notification ──
    try {
        withCredentials([string(credentialsId: 'teams-webhook', variable: 'TEAMS_URL')]) {
            def teamsPayload = groovy.json.JsonOutput.toJson([
                '@type'   : 'MessageCard',
                '@context': 'http://schema.org/extensions',
                themeColor: color.replace('#', ''),
                summary   : "Deploy ${status}: ${environment}",
                sections  : [[
                    activityTitle: "${emoji} ShopMicro Deploy — ${status}",
                    facts: [
                        [name: 'Environment', value: environment],
                        [name: 'Version',     value: version],
                        [name: 'Duration',    value: duration],
                        [name: 'Triggered by', value: currentBuild.getBuildCauses()[0]?.userId ?: 'ci-pipeline']
                    ],
                    markdown: true
                ]],
                potentialAction: [[
                    '@type': 'OpenUri',
                    name   : 'View Build',
                    targets: [[os: 'default', uri: env.BUILD_URL]]
                ]]
            ])
            httpRequest(
                url        : TEAMS_URL,
                httpMode   : 'POST',
                contentType: 'APPLICATION_JSON',
                requestBody: teamsPayload,
                quiet      : true
            )
        }
    } catch (e) {
        echo "Teams notification skipped: ${e.message}"
    }
}
