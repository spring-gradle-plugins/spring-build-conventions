package io.spring.gradle.convention

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.*;

import io.spring.gradle.testkit.junit.rules.TestKit
import org.apache.commons.io.FileUtils

class JavadocApiPluginITest extends Specification {
	@Rule final TestKit testKit = new TestKit()

	def "multimodule api"() {
		when:
		BuildResult result = testKit.withProjectResource("samples/javadocapi/multimodule/")
			.withArguments('api')
			.build();
		then:
		result.task(":api").outcome == SUCCESS
		and:
		File allClasses = new File(testKit.getRootDir(), 'build/api/allclasses-noframe.html');
		allClasses.text.contains('sample/Api.html')
		allClasses.text.contains('sample/Impl.html')
		!allClasses.text.contains('sample/Sample.html')
	}
}
