pipeline {
    agent any
    
    // Tool names MUST match your Jenkins Global Tool Configuration
    tools {
        maven 'Maven-3.x'      // ← Must match Maven name in Jenkins
        jdk 'JDK17'     // ← Must match JDK name in Jenkins
    }
    
    environment {
        // URLs (not secrets)
        NEXUS_REPO_HOST = 'localhost:8081'  // ← NO http:// prefix!
        SONAR_URL = 'http://localhost:9000'
        
        // Jenkins Credential IDs (these are REFERENCES, not actual credentials)
        NEXUS_CREDS_ID = 'nexus-credentials'   // ← Must match Jenkins credential ID
        SONAR_TOKEN_ID = 'sonarqube-token'     // ← Must match Jenkins credential ID
        
        // Nexus repositories
        NEXUS_SNAPSHOT_REPO = 'maven-snapshots'
        NEXUS_RELEASE_REPO = 'maven-releases'
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '📥 Checking out code from SCM...'
                checkout scm
                script {
                    // Get Git info for logging
                    env.GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    env.GIT_BRANCH = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    echo "Branch: ${env.GIT_BRANCH}, Commit: ${env.GIT_COMMIT?.take(7)}"
                }
            }
        }
        
        stage('Build') {
            steps {
                echo '🔨 Compiling the project...'
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publish test results
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo '📦 Packaging the application...'
                sh 'mvn package -DskipTests'
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                // withSonarQubeEnv uses the server configuration from Jenkins
                // The token is already configured in the SonarQube server settings
                withSonarQubeEnv('sonarscanner') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=ci-cd-dummy \
                        -Dsonar.host.url=${SONAR_URL}
                    """
                }
            }
        }
        
        stages {
        stage('Diagnostic Check') {
            steps {
                script {
                        echo '=== System Information ==='
                        sh 'java -version'
                        sh 'mvn -version'
                        sh 'echo "JAVA_HOME: $JAVA_HOME"'
                        sh 'echo "PATH: $PATH"'
                    
                        echo '\n=== Network Checks ==='
                        sh 'curl -I http://localhost:9000 || echo "SonarQube not reachable"'
                        sh 'curl -I http://localhost:8081 || echo "Nexus not reachable"'
                    
                        echo '\n=== Environment Variables ==='
                        sh 'printenv | grep -i java'
                        sh 'printenv | grep -i maven'
                        }
                    }
                }
            }


        stage('Quality Gate') {
            steps {
                echo '⏳ Waiting for SonarQube Quality Gate result...'
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "❌ Quality Gate failed with status: ${qg.status}"
                        } else {
                            echo "✅ Quality Gate passed!"
                        }
                    }
                }
            }
        }
        
        stage('Publish Artifact to Nexus') {
            steps {
                script {
                    echo '📤 Publishing artifact to Nexus Repository...'
                    
                    // Read POM file to get artifact details
                    def pom = readMavenPom file: 'pom.xml'
                    
                    // Determine if SNAPSHOT or RELEASE
                    def repoName = pom.version.contains('SNAPSHOT') ? 
                                   env.NEXUS_SNAPSHOT_REPO : 
                                   env.NEXUS_RELEASE_REPO
                    
                    echo """
                    ═══════════════════════════════════════
                    Artifact Information:
                    ───────────────────────────────────────
                    Group ID:    ${pom.groupId}
                    Artifact ID: ${pom.artifactId}
                    Version:     ${pom.version}
                    Packaging:   ${pom.packaging}
                    Repository:  ${repoName}
                    ═══════════════════════════════════════
                    """
                    
                    // Upload to Nexus
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: env.NEXUS_REPO_HOST,     // Just hostname:port
                        repository: repoName,
                        credentialsId: env.NEXUS_CREDS_ID, // Jenkins credential ID
                        groupId: pom.groupId,
                        artifactId: pom.artifactId,
                        version: pom.version,
                        packaging: pom.packaging,
                        artifacts: [
                            [
                                artifactId: pom.artifactId,
                                classifier: '',
                                file: "target/${pom.artifactId}-${pom.version}.${pom.packaging}",
                                type: pom.packaging
                            ]
                        ]
                    )
                    
                    echo "✅ Artifact uploaded successfully to ${repoName}!"
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ ════════════════════════════════════════'
            echo '✅  Pipeline completed successfully!'
            echo '✅ ════════════════════════════════════════'
        }
        failure {
            echo '❌ ════════════════════════════════════════'
            echo '❌  Pipeline failed!'
            echo '❌ ════════════════════════════════════════'
        }
        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
    }
}