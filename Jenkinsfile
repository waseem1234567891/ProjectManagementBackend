pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'   // Replace with your Maven tool name in Jenkins
        jdk 'JAVA_HOME'      // Replace with your JDK tool name in Jenkins
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
                // Run tests but don't fail the pipeline immediately if tests fail
                bat 'mvn test || exit 0'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/target/surefire-reports/*.xml'
                }
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
        unstable {
            echo 'Build UNSTABLE ⚠ Some tests failed'
        }
        failure {
            echo 'Build FAILED ❌'
        }
    }
}
