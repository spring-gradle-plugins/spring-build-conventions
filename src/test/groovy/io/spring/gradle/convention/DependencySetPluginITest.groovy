package io.spring.gradle.convention

import io.spring.gradle.testkit.junit.rules.TestKit
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DependencySetPluginITest extends Specification {
	@Rule final TestKit testKit = new TestKit()

	def "dependencies"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/dependencyset")
			.withArguments('dependencies')
			.build();
		then:
		result.task(":dependencies").outcome == SUCCESS
		!result.output.contains("FAILED")
	}
}
