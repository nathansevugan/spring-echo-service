#!groovy
package pipelines

pipeline {
    agent {
//        node { label 'springboot-echo-service-build' }
        node { label 'maven' }

    }

//    environment {
//        JAVA_HOME = "/usr/lib/jdk10/jdk-1.8.0"
//    }
    options {
        // set a timeout of 20 minutes for this pipeline
        timeout(time: 2, unit: 'MINUTES')
    }
    stages {

        stage('Info') {
            steps {
                script {
                    projectId = "dev"
                    baseImageName = ("springboot-echo-service").toLowerCase()
                    rcImageName = "${baseImageName}"
                    echo("building base image:" + baseImageName)
                    echo "ENV"
                }
            }
        }

        stage('Authenticate to project') {
            steps {
                script {
                    sessionToken = ocStartSession()
                    useProject(projectId)
                    sourceRepoCredentials = "openshift:\\" + sessionToken
                }
            }
        }

        stage('Build App') {
            steps {
                echo "building application"
                sh "mvn install"
                stash name: "jar", includes: "target/springboot-echo-service-0.0.1-SNAPSHOT.jar"
            }
        }
        stage('Build image') {
            steps {
                unstash name: "jar"
                sh "ls target"
                script {
                    sh "oc start-build bc/${rcImageName}-image --from-file=target/springboot-echo-service-0.0.1-SNAPSHOT.jar -n ${projectId} --wait --follow"
                }
            }
        }
    }
}


def ocStartSession() {
    println("Requesting OC session")
    echo("getting session token")
    def sessionToken = sh script: "set +x; oc sa get-token robot -n dev", returnStdout: true
    echo("attempting to login to the service using the session token: " + sessionToken)
    sh script: "set +x; oc login kubernetes.default.svc --insecure-skip-tls-verify --token=$sessionToken"
    return sessionToken
}

def useProject(def project) {
    println("Switching to project ${project}")
    sh script: "set +x; oc project ${project}"
}