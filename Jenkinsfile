stage('Artifactory Deploy') {
	node {
		checkout scm
		withCredentials([usernamePassword(credentialsId: '02bd1690-b54f-4c9f-819d-a77cb7a9822c', artifactoryPassword: 'ARTIFACTORY_PASSWORD', artifactoryUsername: 'ARTIFACTORY_USERNAME')]) {
			sh "./gradlew artifactoryPublish -PartifactoryUsername=$ARTIFACTORY_USERNAME -PartifactoryPassword=$ARTIFACTORY_PASSWORD --no-daemon --stacktrace"
		}
	}
}