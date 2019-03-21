/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SpringIoConventionPluginITest extends Specification {
	@Rule final TestKit testKit = new TestKit()

	def "dependencies"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/springio/override")
			.withArguments('dependencyInsight','-PplatformVersion=Brussels-RELEASE','--configuration', 'springIoTestRuntime', '--dependency', 'mockito-core')
			.build();
		then:
		result.task(":dependencyInsight").outcome == SUCCESS
		result.output.contains("org.mockito:mockito-core:2.7.22 -> 1.10.19")
	}

	@Unroll
	def "Can Resolve Spring IO projectVersion=#projectVersion platformVersion=#platformVersion"(String projectVersion, String platformVersion) {
		when:
		BuildResult result = testKit.withProjectResource("samples/springio/noSpringIoVersion")
				.withArguments('dependencyInsight','--configuration', 'springIoTestRuntime', '--dependency', 'mockito-core', "-PplatformVersion=${platformVersion}", "-Pversion=${projectVersion}")
				.build();
		then:
		result.task(":dependencyInsight").outcome == SUCCESS
		result.output.contains("org.mockito:mockito-core:")

		where:
		projectVersion         | platformVersion
		'1.0.0.BUILD-SNAPSHOT' | 'Cairo-BUILD-SNAPSHOT'
		'1.0.0.M1'             | 'Cairo-BUILD-SNAPSHOT'
		'1.0.0.RC1'            | 'Cairo-BUILD-SNAPSHOT'
		'1.0.0.RELEASE'        | 'Cairo-BUILD-SNAPSHOT'
		'1.0.0.BUILD-SNAPSHOT' | 'Brussels-SR1'
		'1.0.0.M1'             | 'Brussels-SR1'
		'1.0.0.RC1'            | 'Brussels-SR1'
		'1.0.0.RELEASE'        | 'Brussels-SR1'
		'1.0.0.BUILD-SNAPSHOT' | 'Brussels-RELEASE'
		'1.0.0.M1'             | 'Brussels-RELEASE'
		'1.0.0.RC1'            | 'Brussels-RELEASE'
		'1.0.0.RELEASE'        | 'Brussels-RELEASE'
	}

	def "Adding springIoVersion does not update repositories"() {
		expect:
		BuildResult result = testKit.withProjectResource("samples/springio/noSpringIoVersion")
				.withArguments('dependencyInsight','--configuration', 'springIoTestRuntime', '--dependency', 'mockito-core', "-PspringIoVersion=Cairo-BUILD-SNAPSHOT", "-Pversion=1.0.0.RELEASE")
				.buildAndFail();
	}

	def "Spring IO Not Required"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/springio/springIoNotNecessary")
				.withArguments('dependencyInsight','--configuration', 'testRuntime', '--dependency', 'mockito-core')
				.build();
		then:
		result.task(":dependencyInsight").outcome == SUCCESS
		result.output.contains("org.mockito:mockito-core:2.7.22")
	}
}
