pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'   // Jenkins Maven tool name
        jdk 'JAVA_HOME'      // Jenkins JDK tool name
    }

    environment {
        IMAGE_NAME = "project-management-backend"
        DOCKERHUB_USER = "yourdockerhubusername"   // Optional if pushing
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
                bat 'mvn test || exit 0'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build Docker Image') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    def imageTag = "${env.BUILD_NUMBER}"

                    echo "Building Docker image..."
                    bat """
                        docker build -t ${env.IMAGE_NAME}:${imageTag} .
                        docker tag ${env.IMAGE_NAME}:${imageTag} ${env.IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying with docker-compose..."
                bat """
                    docker-compose down
                    docker-compose up -d --build
                """
            }
        }

        // OPTIONAL: Push to Docker Hub if you want later:
        /*
        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhub-token', variable: 'TOKEN')]) {
                        bat "docker login -u ${env.DOCKERHUB_USER} -p %TOKEN%"
                        bat "docker push ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:${env.BUILD_NUMBER}"
                        bat "docker push ${env.DOCKERHUB_USER}/${env.IMAGE_NAME}:latest"
                    }
                }
            }
        }
        */

    } // ← END stages

    post {
        success {
            echo 'Build SUCCESSFUL ✔ Docker image created and deployed!'
        }
        unstable {
            echo 'Build UNSTABLE ⚠ Some tests failed.'
        }
        failure {
            echo 'Build FAILED ❌'
        }
    }
}
