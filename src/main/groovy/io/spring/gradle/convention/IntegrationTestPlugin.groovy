package io.spring.gradle.convention

import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

public class IntegrationTestPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.configurations {
			integrationTestCompile {
				extendsFrom testCompile, optional, provided
			}
			integrationTestRuntime {
				extendsFrom integrationTestCompile, testRuntime
			}
		}
		project.sourceSets {
			integrationTest {
				java.srcDir project.file('src/integration-test/java')
				resources.srcDir project.file('src/integration-test/resources')
				compileClasspath = project.sourceSets.main.output + project.sourceSets.test.output + project.configurations.integrationTestCompile
				runtimeClasspath = output + compileClasspath + project.configurations.integrationTestRuntime
			}
		}

		Task integrationTest = project.tasks.create("integrationTest", Test) {
			dependsOn 'jar'
			testClassesDir = project.sourceSets.integrationTest.output.classesDir
			classpath = project.sourceSets.integrationTest.runtimeClasspath
			reports {
				html.destination = project.file("$project.buildDir/reports/integration-tests/")
				junitXml.destination = project.file("$project.buildDir/integration-test-results/")
			}
		}
		project.tasks.check.dependsOn integrationTest

		project.tasks.withType(GroovyPlugin) {
			project.sourceSets {
				integrationTest {
					groovy.srcDirs project.file('src/integration-test/groovy')
				}
			}
		}
	}
}
