pipeline {
    agent any

    environment {
        APP_NAME = "ci-cd-dummy"
        DEPLOY_DIR = "C:/Deployments/${APP_NAME}"
    }

    triggers {
        githubPush()  // ✅ Automatically triggers on GitHub push
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/oysheeb/ci-cd-dummy.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building ${APP_NAME}..."
                // Example build command
                bat 'mvn clean package' // use sh for Linux
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                // Example test command
                bat 'mvn test'
            }
        }

         stage('Code Analysis') {
            steps {
                withSonarQubeEnv('MySonar')
                bat 'sonar-scanner'
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying ${APP_NAME} to ${DEPLOY_DIR}"
                bat """
                    mkdir "${DEPLOY_DIR}" 2>nul
                    copy target\\*.jar "${DEPLOY_DIR}\\"
                """
            }
        }
    }

    post {
        success {
            echo "✅ Deployment successful!"
        }
        failure {
            echo "❌ Build or deployment failed."
        }
    }
}