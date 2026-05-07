def nexusRegistry = "172.28.51.108:8081"
def javaBuildImage = "${nexusRegistry}/docker-hosted/local-java-maven:latest"

pipeline {
    agent { label 'linux-docker' }

    options {
        ansiColor('xterm')
        timestamps()
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    parameters {
        booleanParam(name: 'BUILD_APP', defaultValue: true, description: 'Build Hello World Java app')
    }

    stages {
        stage('Build Java Artifact') {
            when {
                expression { return params.BUILD_APP }
            }

            agent {
                docker {
                    image "${javaBuildImage}"
                    registryUrl "http://${nexusRegistry}"
                    registryCredentialsId 'nexus-creds'
                    reuseNode true
                    alwaysPull true
                    label 'linux-docker'

                    /*
                     * Run container as Jenkins user to avoid root-owned workspace files.
                     * Current Jenkins UID/GID from previous lab: 972:969
                     */
                    args '-u 972:969 -e HOME=/tmp -e MAVEN_CONFIG=/tmp/.m2 -v /var/lib/jenkins/.m2:/tmp/.m2'
                }
            }

            stages {
                stage('Checkout Java Code') {
                    steps {
                        checkout scm

                        sh '''
                            echo "Current workspace:"
                            pwd
                            echo "Repo files:"
                            ls -la
                        '''
                    }
                }

                stage('Show Java and Maven Versions') {
                    steps {
                        sh '''
                            java -version
                            mvn -version
                        '''
                    }
                }

stage('Run Tests and Package') {
    steps {
        withCredentials([
            usernamePassword(
                credentialsId: 'nexus-creds',
                usernameVariable: 'NEXUS_USER',
                passwordVariable: 'NEXUS_PASSWORD'
            )
        ]) {
            sh '''
                set -e

                echo "Running Maven clean test package using Nexus mirror and persistent Maven cache..."

                mvn -s .mvn/settings.xml \
                    -Dmaven.repo.local=/tmp/.m2/repository \
                    clean test package

                echo "Generated target files:"
                ls -lh target
            '''
        }
    }
}

                stage('Archive and Stash JAR') {
                    steps {
                        sh '''
                            set -e

                            mkdir -p artifacts

                            JAR_FILE=$(ls target/*.jar | grep -v original | head -1)

                            echo "Selected JAR: $JAR_FILE"

                            cp "$JAR_FILE" artifacts/hello-world-java.jar

                            echo "Final artifact:"
                            ls -lh artifacts/
                        '''

                        archiveArtifacts artifacts: 'artifacts/*.jar', fingerprint: true
                        stash name: 'HelloWorldJavaJar', includes: 'artifacts/hello-world-java.jar', allowEmpty: false
                    }
                }
            }

            post {
                always {
                    echo "Cleaning Java build workspace"
                    deleteDir()
                }
            }
        }
    }

    post {
        always {
            echo "Java build-only pipeline finished"
            deleteDir()
        }

        failure {
            echo "Java build failed. Check Docker image pull, Maven build, tests, or artifact archive logs."
        }

        success {
            echo "Java build completed successfully. JAR was archived and stashed."
        }
    }
}