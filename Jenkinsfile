pipeline {
    // Defines the execution environment. 'any' means Jenkins can run it on any available agent.
    agent any

    // Defines global environment variables used throughout the pipeline.
    environment {
        APP_NAME = "ci-cd-dummy"
        // Using forward slashes is generally safer even on Windows agents for consistency
        DEPLOY_DIR = "C:/Deployments/${APP_NAME}" 
        
        // Ensure SonarQube properties are set for the scanner
        // Replace with your actual project key/name if needed.
        SONAR_PROJECT_KEY = "${APP_NAME}" 
    }

    // Defines how the pipeline is triggered.
    triggers {
        // githubPush() requires specific configuration. pollSCM() is a simpler alternative, 
        // or ensure you set up a webhook in GitHub for proper push events.
        githubPush() 
    }

    // The core steps of the pipeline 
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                // Recommended practice is to use the credentials binding for private repos
                git branch: 'main',
                    url: 'https://github.com/oysheeb/ci-cd-dummy.git'
            }
        }

        stage('Build & Install') {
            steps {
                echo "Building ${APP_NAME} and installing artifact locally..."
                // Use 'install' to compile, test, package, and place the artifact in the local Maven repo (.m2)
                // This is generally better practice in a full CI/CD process.
                bat 'mvn clean install -DskipTests' // Skipping tests here to run them in the dedicated 'Test' stage
            }
        }

        stage('Unit & Integration Test') {
            steps {
                echo "Running unit and integration tests..."
                // Runs tests and generates test reports
                bat 'mvn test'
            }
        }

        stage('Code Analysis (SonarQube)') {
             steps {
                 echo "Starting SonarQube analysis..."
                 // The 'withSonarQubeEnv' wrapper sets up credentials/server URL.
                 // For a Maven project, running 'mvn sonar:sonar' is the standard way to trigger the analysis
                 // as it uses the project's POM configuration.
                 withSonarQubeEnv('MySonar') {
                     // Note: We run install first, so the classes are available for SonarQube
                     bat "mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
                 }
             }
         }

        stage('Deploy Artifact') {
            steps {
                echo "Deploying ${APP_NAME} to local directory ${DEPLOY_DIR}"
                
                // Using a script block for multi-line Windows commands is cleaner
                script {
                    // Create the directory if it doesn't exist
                    bat "mkdir \"${DEPLOY_DIR}\" 2>nul || (if exist \"${DEPLOY_DIR}\" echo Target directory already exists.)"
                    
                    // Copy the packaged JAR file (assumes standard Maven output)
                    // The 'target' directory is relative to the workspace.
                    bat "copy target\\*.jar \"${DEPLOY_DIR}\\"
                }
            }
        }
    }

    post {
        always {
            // Cleanup the workspace after the build to save disk space
            cleanWs()
        }
        success {
            // Send notification or update status on success
            echo "✅ Pipeline completed successfully. Artifact is at ${DEPLOY_DIR}"
        }
        failure {
            // Send detailed failure notification
            echo "❌ Pipeline failed at stage: ${env.STAGE_NAME}. Check logs for details."
        }
    }
}