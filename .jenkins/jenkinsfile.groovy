#!groovy
package pipelines

pipeline {
    agent {
//        node { label 'springboot-echo-service-build' }
        node { label 'maven' }

    }

    environment {
        JAVA_HOME = "/usr/lib/jdk10/jdk-1.8.0"
    }
    options {
        // set a timeout of 20 minutes for this pipeline
        timeout(time: 2, unit: 'MINUTES')
    }
    stages {

        stage('Info') {
            steps {
                script {
                    //Global variable section
                    //project that holds images
                    projectId = "dev"

                    //Staging reposiroty credentials [aprm cicd]
//                    repositoryCredentials = "HmVFwNO3:etaWeJoi/639yLO7RnBOrHs6Miy1ihX2joLNl0IOd5rX"
//
//                    shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
//                    branch = scm.branches[0].name
//                    buildNumber = env.getEnvironment().get("BUILD_NUMBER")
//                    repoName = sh(script: 'git remote -v|grep \'(fetch)\'|awk \'{print $2}\' | gawk \'match($0, /([^/]+).git$/, ary) {print ary[1]}\'', returnStdout: true).trim()

//                    muleVersion = "4.3.2.1"
//                    imageSuffix = muleVersion //branch

                    baseImageName = ("springboot-echo-service-").toLowerCase()
//                    rcImageName = "${baseImageName}-rc-${shortCommit}-${buildNumber}"


                    echo("building base image:" + baseImageName)

                    echo "ENV"
                    environment = env.getEnvironment()
//                            .collect { envvar -> "${envvar.key}=${envvar.value}" }.join("\n")
                    echo environment

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
            }
        }
        stage('Build image') {
            steps {
                script {
//                    createImageBuild(rcImageName, buildNumber)
                    runImageBuild(rcImageName)
                }
            }
        }
    }
}

def runImageBuild(def imageName) {
    sh script: "oc start-build bc/${imageName}-image --wait --follow"
}

//def createImageBuild() {
//    sh script: "set +x; oc process -f build.yml" + " | oc create -f -"
//}

def ocStartSession() {
    println("Requesting OC session")
    def sessionToken = sh script: "set +x; oc sa get-token nate -n cicd", returnStdout: true
    sh script: "set +x; oc login kubernetes.default.svc --insecure-skip-tls-verify --token=$sessionToken"
    return sessionToken
}

def useProject(def project) {
    println("Switching to project ${project}")
    sh script: "set +x; oc project ${project}"
}