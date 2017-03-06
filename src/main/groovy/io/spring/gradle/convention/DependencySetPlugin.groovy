package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project

public class DependencySetPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {

		project.ext.spockDependencies = [
			project.dependencies.create("org.spockframework:spock-spring") {
				exclude group: 'junit', module: 'junit-dep'
			},
			project.dependencies.create("org.spockframework:spock-core") {
				exclude group: 'junit', module: 'junit-dep'
			}
		]

		project.ext.seleniumDependencies = [
				"org.seleniumhq.selenium:htmlunit-driver",
				"org.seleniumhq.selenium:selenium-support"
		]

		project.ext.gebDependencies = project.spockDependencies +
			project.seleniumDependencies + [
			"org.gebish:geb-spock",
			'commons-httpclient:commons-httpclient',
			"org.codehaus.groovy:groovy",
			"org.codehaus.groovy:groovy-all"
		]

		project.ext.powerMockDependencies = [
			"org.powermock:powermock-core",
			"org.powermock:powermock-api-support",
			"org.powermock:powermock-module-junit4-common",
			"org.powermock:powermock-module-junit4",
			project.dependencies.create("org.powermock:powermock-api-mockito") {
				exclude group: 'org.mockito', module: 'mockito-all'
			},
			"org.powermock:powermock-reflect"
		]

		project.ext.slf4jDependencies = [
			"org.slf4j:slf4j-api",
			"org.slf4j:jcl-over-slf4j",
			"org.slf4j:log4j-over-slf4j",
			"ch.qos.logback:logback-classic"
		]

		project.ext.springCoreDependency = [
			project.dependencies.create("org.springframework:spring-core") {
				exclude(group: 'commons-logging', module: 'commons-logging')
			}
		]

		project.ext.jstlDependencies = [
				"javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api",
				"org.apache.taglibs:taglibs-standard-jstlel"
		]

		project.ext.apachedsDependencies = [
				"org.apache.directory.server:apacheds-core",
				"org.apache.directory.server:apacheds-core-entry",
				"org.apache.directory.server:apacheds-protocol-shared",
				"org.apache.directory.server:apacheds-protocol-ldap",
				"org.apache.directory.server:apacheds-server-jndi",
				'org.apache.directory.shared:shared-ldap'
		]
	}
}
