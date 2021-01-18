pipeline {
    agent { docker { image 'adoptopenjdk/openjdk11:alpine-slim' } }
    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
    }
}
