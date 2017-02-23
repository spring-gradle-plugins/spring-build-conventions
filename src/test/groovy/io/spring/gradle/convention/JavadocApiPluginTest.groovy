package io.spring.gradle.convention

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.*;


class JavadocApiPluginTest extends Specification {
	@Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
	File buildFile

	def setup() {
		buildFile = testProjectDir.newFile('build.gradle')
	}

	def "hello world task prints hello world"() {
		given:
		buildFile << """
            task helloWorld {
                doLast {
                    println 'Hello world!'
                }
            }
        """

		when:
		def result = GradleRunner.create()
			.withProjectDir(testProjectDir.root)
			.withArguments('helloWorld')
			.build()

		then:
		result.output.contains('Hello world!')
		result.task(":helloWorld").outcome == SUCCESS
	}
}
