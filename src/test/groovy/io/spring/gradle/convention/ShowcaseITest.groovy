/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.spring.gradle.convention

import io.spring.gradle.testkit.junit.rules.TestKit
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ShowcaseITest extends Specification {
	@Rule final TestKit testKit = new TestKit()

	def "build"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/showcase/")
				.withArguments('build','--stacktrace')
				.forwardOutput()
				.build();
		then: 'entire build passes'
		result.output.contains("BUILD SUCCESSFUL")

		and: 'javadoc api works'

		and: 'integration tests run'
		new File(testKit.getRootDir(), 'samples/sgbcs-sample-war/build/integration-test-results/').exists()
		new File(testKit.getRootDir(), 'samples/sgbcs-sample-war/build/reports/integration-tests/').exists()
	}

	def "springio"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/showcase/")
				.withArguments('-PplatformVersion=Brussels-RELEASE','springIoTest','--stacktrace')
				.forwardOutput()
				.build();
		then:
		result.output.contains("SUCCESS")
	}

	def "install"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/showcase/")
				.withArguments('install','--stacktrace')
				.build();
		then:
		result.output.contains("SUCCESS")

		and: 'pom exists'
		File pom = new File(testKit.getRootDir(), 'sgbcs-core/build/poms/pom-default.xml')
		pom.exists()
		String pomText = pom.getText()

		and: 'pom does not contain <dependencyManagement>'
		!pomText.contains('<dependencyManagement>')

		and: 'creates optional dependencies correctly'
		pomText.replaceAll('\\s','').contains("""<dependency>
		<groupId>ch.qos.logback</groupId>
		<artifactId>logback-classic</artifactId>
		<scope>compile</scope>
		<optional>true</optional>
		<version>1.1.9</version>
	</dependency>""".replaceAll('\\s',''))

		and: 'adds author'
		pomText.replaceAll('\\s','').contains("""<developers>
			<developer>
				<id>rwinch</id>
				<name>Rob Winch</name>
				<email>rwinch@pivotal.io</email>
			</developer>
			<developer>
				<id>jgrandja</id>
				<name>Joe Grandja</name>
				<email>jgrandja@pivotal.io</email>
			</developer>
		</developers>""".replaceAll('\\s',''))

		and: 'adds repositories'
		pomText.replaceAll('\\s','').contains("""<scm>
			<connection>scm:git:git://github.com/spring-projects/spring-security</connection>
			<developerConnection>scm:git:git://github.com/spring-projects/spring-security</developerConnection>
			<url>https://github.com/spring-projects/spring-security</url>
		</scm>""".replaceAll('\\s',''))

		and: 'adds description & url'
		pomText.contains('<description>sgbcs-core</description>')
		pomText.contains('<url>http://spring.io/spring-security</url>')

		and: 'adds organization'
		pomText.replaceAll('\\s','').contains('''<organization>
			<name>spring.io</name>
			<url>http://spring.io/</url>
		</organization>'''.replaceAll('\\s',''))

		and: 'adds licenses'
		pomText.replaceAll('\\s','').contains('''	<licenses>
			<license>
				<name>The Apache Software License, Version 2.0</name>
				<url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
				<distribution>repo</distribution>
			</license>
		</licenses>'''.replaceAll('\\s',''))

		and: 'adds scm'
		pomText.replaceAll('\\s','').replaceAll('\\s','').contains("""<scm>
			<connection>scm:git:git://github.com/spring-projects/spring-security</connection>
			<developerConnection>scm:git:git://github.com/spring-projects/spring-security</developerConnection>
			<url>https://github.com/spring-projects/spring-security</url>
		</scm>""".replaceAll('\\s',''))
	}
}
