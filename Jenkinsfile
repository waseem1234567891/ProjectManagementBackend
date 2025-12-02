pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'   // Name from Jenkins Global Tool Configuration
        jdk 'JAVA_HOME'     // Or your Java version
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/waseem1234567891/ProjectManagementBackend.git'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Build SUCCESSFUL ✔'
        }
        failure {
            echo 'Build FAILED ❌'
        }
    }
}
