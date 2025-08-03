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
        git branch: 'main', url: 'https://github.com/kalki123884/sampetest.git'
    }

    stage("Install dependencies"){
        echo "Installing Python dependencies"
        sh 'pip install --upgrade fastapi'
        sh 'pip install --upgrade uvicorn'
        
    }
    stage("Sonarqube"){
        withSonarQubeEnv('SonarQube Cloud') {
              def scannerHome = tool 'sampetest-sonar'
               sh """
                ${scannerHome}/bin/sonar-scanner \
                -Dsonar.projectKey=sampetest \
                -Dsonar.sources=. \
                -Dsonar.organization=kalki123884
            """
        }
    }
    stage("prepare image"){
        echo "Deploying application"
        sh 'docker build -t sample:v1 .'
    }
    stage("Stop and remove app container"){
        echo "Removing container"
        sh '''
           #!/bin/bash
           set -e
           if docker ps | grep -i sampleapp; then
              docker stop sampleapp && docker rm sampleapp
           elif docker ps -a | grep -i sampleapp; then
              docker rm sampleapp
           else
              echo "no sample app found to remove"
           fi
        '''
    }
    stage("Deploy application"){
        echo "Deploy sample app"
        sh 'docker run -d --name sampleapp -p 8000:8000 sample:v1'
    }
}
