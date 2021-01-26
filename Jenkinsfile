properties([
		buildDiscarder(logRotator(numToKeepStr: '10')),
		pipelineTriggers([
				cron('@daily')
		]),
])

stage('Artifactory Deploy') {
	node('linux') {
		checkout scm
		sh 'git clean -dfx'
		withCredentials([usernamePassword(credentialsId: '02bd1690-b54f-4c9f-819d-a77cb7a9822c', usernameVariable: 'ARTIFACTORY_USERNAME', passwordVariable: 'ARTIFACTORY_PASSWORD')]) {
			withEnv(["JAVA_HOME=${tool 'jdk8'}"]) {
				sh './gradlew check artifactoryPublish --no-daemon --refresh-dependencies --stacktrace -PartifactoryUsername=$ARTIFACTORY_USERNAME -PartifactoryPassword=$ARTIFACTORY_PASSWORD'
			}
		}
	}
}
