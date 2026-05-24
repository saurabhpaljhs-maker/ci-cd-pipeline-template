#!/usr/bin/env groovy
// Shared Library: slackNotify.groovy
// Usage: slackNotify(status: 'SUCCESS', appName: 'demo', buildNumber: env.BUILD_NUMBER)

def call(Map config) {
    def status      = config.status      ?: 'UNKNOWN'
    def appName     = config.appName     ?: 'app'
    def buildNumber = config.buildNumber ?: '0'
    def channel     = config.channel     ?: '#deployments'
    def buildUrl    = config.buildUrl    ?: env.BUILD_URL

    def (color, icon) = status == 'SUCCESS' ? ['good', '✅'] :
                        status == 'FAILURE'  ? ['danger', '❌'] :
                                               ['warning', '⚠️']

    slackSend(
        channel: channel,
        color:   color,
        message: "${icon} *${status}* — `${appName}` Build #${buildNumber} | <${buildUrl}|View>"
    )
}
