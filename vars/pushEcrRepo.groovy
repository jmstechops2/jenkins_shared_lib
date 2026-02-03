def call(Map pipelineParams){
def projectName = "${pipelineParams.ecrRepoName}"

pipeline {
   agent any
   environment
    {
        VERSION = "${BUILD_NUMBER}"
        PROJECT = "${projectName}"
        IMAGE = "$PROJECT:$VERSION"
        ECRURL = 'https://713363449239.dkr.ecr.ap-south-1.amazonaws.com/${projectName}'
        ECRCRED = 'ecr:ap-south-1:aws_credentials'
    }
    stages {

       stage('build'){
            steps{      
                sh "mvn package"
             }
         }
          stage('Image Build'){
             steps{
                 script{
				 
                      sh 'docker build -t $IMAGE .'
                 }

             }
         }
         stage('Push Image'){
         steps{
             script
                {
                    docker.withRegistry(ECRURL, ECRCRED)
                    {
                        docker.image(IMAGE).push()
                    }
                }
            }
         }
    }
    post
    {
        always
        {
            // make sure that the Docker image is removed

            sh "docker rmi $IMAGE | true"
        }
    }
}
}
