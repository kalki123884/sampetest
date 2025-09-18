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
      def imagetag = "${scmVars.GIT_COMMIT}".take(7)
      def fullimage  = "sivasankarinkollu1/sample:${imagetag}"
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
      docker.build("${fullimage}")
    }
    stage("Lint") {
      echo "Performing Lint check"
    }
    stage("push image"){
      echo "push image to repo"
      docker.withRegistry("https://index.docker.io/v1/","siva-docker-login"){
        image.push("${fullimage}")
      }
    }
    //stage("prepare image"){
    //    echo "Deploying application"
    //    sh 'docker build -t sample:v1 .'
    //}
    //stage("Stop and remove app container"){
    //    echo "Removing container"
    //    sh '''
    //       #!/bin/bash
    //       set -e
    //       if docker ps | grep -i sampleapp; then
    //          docker stop sampleapp && docker rm sampleapp
    //       elif docker ps -a | grep -i sampleapp; then
    //          docker rm sampleapp
    //       else
    //          echo "no sample app found to remove"
    //       fi
    //    '''
    //}
    //stage("Deploy application"){
    //    echo "Deploy sample app"
    //    sh 'docker run -d --name sampleapp -p 8000:8000 sample:v1'
    //}
}
