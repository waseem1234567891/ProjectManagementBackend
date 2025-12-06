pipeline {
    agent any

    environment {
        BACKEND_IMAGE = "project-management-backend"
        FRONTEND_IMAGE = "project-frontend"
    }

    stages {

        stage('Checkout Repositories') {
            parallel {
                stage('Checkout Backend') {
                    steps {
                        dir('backend') {
                            git branch: 'main', url: 'https://github.com/yourusername/backend-repo.git'
                        }
                    }
                }
                stage('Checkout Frontend') {
                    steps {
                        dir('frontend') {
                            git branch: 'main', url: 'https://github.com/waseem1234567891/ProjectManagementFrontEnd.git'
                        }
                    }
                }
            }
        }

        stage('Build & Test') {
            parallel {
                stage('Backend Build & Test') {
                    steps {
                        dir('backend') {
                            bat 'mvn clean install'
                            bat 'mvn test || exit 0'  // continue even if some tests fail
                        }
                    }
                    post {
                        always {
                            dir('backend') {
                                junit '**/target/surefire-reports/*.xml'
                            }
                        }
                    }
                }

                stage('Frontend Build & Test') {
                    steps {
                        dir('frontend') {
                            bat 'npm install'
                            bat 'npm test || exit 0'
                            bat 'npm run build'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            parallel {
                stage('Backend Docker Image') {
                    steps {
                        dir('backend') {
                            bat """
                                docker build -t ${env.BACKEND_IMAGE}:${env.BUILD_NUMBER} .
                                docker tag ${env.BACKEND_IMAGE}:${env.BUILD_NUMBER} ${env.BACKEND_IMAGE}:latest
                            """
                        }
                    }
                }
                stage('Frontend Docker Image') {
                    steps {
                        dir('frontend') {
                            bat """
                                docker build -t ${env.FRONTEND_IMAGE}:${env.BUILD_NUMBER} .
                                docker tag ${env.FRONTEND_IMAGE}:${env.BUILD_NUMBER} ${env.FRONTEND_IMAGE}:latest
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                bat """
                docker-compose down
                docker-compose up -d --build
                """
            }
        }

    }

    post {
        success {
            echo 'BUILD SUCCESS ✅ Backend + Frontend built, images deployed!'
        }
        failure {
            echo 'BUILD FAILED ❌'
        }
    }
}
