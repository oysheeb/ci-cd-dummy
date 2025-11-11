pipeline {
    agent any  // runs on any available Jenkins agent

    environment {
        APP_NAME = "PipelineTest"
        DEPLOY_DIR = "/var/www/cicdtest1"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Cloning the repo..."
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building the application..."
                // Example for Maven build
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo "Running unit tests..."
                // Example for running tests
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo "Packaging artifacts..."
                sh 'mvn package'
            }
        }

        stage('Deploy') {
            when {
                branch 'main' // only deploy from main branch
            }
            steps {
                echo "Deploying application..."
                // Example: copy jar to deploy folder
                sh 'cp target/*.jar ${DEPLOY_DIR}/'
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deploy Successful!'
        }
        failure {
            echo '❌ Build Failed!'
        }
    }
}
