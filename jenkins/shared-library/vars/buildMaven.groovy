#!/usr/bin/env groovy
// Shared Library: buildMaven.groovy
// Usage in Jenkinsfile: buildMaven(skipTests: false, goals: 'clean package')

def call(Map config = [:]) {
    def goals   = config.get('goals',     'clean package')
    def skipTests = config.get('skipTests', false)
    def profiles  = config.get('profiles',  '')

    def cmd = "mvn ${goals} -B"
    if (skipTests)    cmd += " -DskipTests"
    if (profiles)     cmd += " -P${profiles}"

    echo "Running: ${cmd}"
    sh cmd
}
