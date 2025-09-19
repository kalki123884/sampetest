#!groovy
def imagetag
def fullimage
def scmVars
def image
properties([
    buildDiscarder(
        logRotator(
            numToKeepStr: '5',      // Keep last 10 builds
            //daysToKeepStr: '0'      // Keep builds from last 30 days
        )
    )
])

node {
    env.nameprefix = "siva"
    
    stage("Checkout") {
        scmVars = checkout(scm)
    }

    stage("Install dependencies"){
        echo "Installing Python dependencies" 
    }
    stage("prepare"){
      echo "${scmVars}"
      imagetag = "${scmVars.GIT_COMMIT}".take(7)
      fullimage  = "sivasankarinkollu1/sample:${imagetag}"
      echo "${fullimage}" 
    }
    //stage("Sonarqube"){
    //    withSonarQubeEnv('SonarQube Cloud') {
    //          def scannerHome = tool 'sampetest-sonar'
    //           sh """
    //            ${scannerHome}/bin/sonar-scanner \
    //            -Dsonar.projectKey=sampetest \
    //            -Dsonar.sources=. \
    //            -Dsonar.organization=kalki123884
    //        """
    //    }
    //}
    stage("prepare docker image"){
      echo "testing..."
      image = docker.build("${fullimage}")
      echo "testing...."
    }
    stage("Lint") {
      echo "Performing Lint check"
    }
    stage("push image"){
      echo "push image to repo"
      docker.withRegistry("https://index.docker.io/v1/","sivasankar-docker-login"){
        image.push("${imagetag}")
        image.push("latest")
      }
    }
    stage("deploy application in k8s cluster"){
      sh 'kubectl get po'
      helm list
    }
}
