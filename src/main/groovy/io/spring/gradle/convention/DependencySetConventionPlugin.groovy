package io.spring.gradle.convention;

import org.gradle.api.Plugin
import org.gradle.api.Project

public class DependencySetConventionPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {

		project.ext.apacheDsVersion = '1.5.5'
		project.ext.jstlVersion = '1.2.1'
		project.ext.spockVersion = '0.7-groovy-2.0'
		project.ext.groovyVersion = '2.4.4'
		project.ext.gebVersion = '0.10.0'
		project.ext.seleniumVersion = '2.44.0'
		project.ext.powerMockVersion = '1.6.2'

		project.ext.spockDependencies = [
			project.dependencies.create("org.spockframework:spock-spring:${project.spockVersion}") {
				exclude group: 'junit', module: 'junit-dep'
			},
			project.dependencies.create("org.spockframework:spock-core:${project.spockVersion}") {
				exclude group: 'junit', module: 'junit-dep'
			}
		]

		project.ext.gebDependencies = project.spockDependencies + [
			"org.seleniumhq.selenium:selenium-htmlunit-driver:${project.seleniumVersion}",
			"org.gebish:geb-spock:${project.gebVersion}",
			'commons-httpclient:commons-httpclient:3.1',
			"org.codehaus.groovy:groovy:${project.groovyVersion}",
			"org.codehaus.groovy:groovy-all:${project.groovyVersion}"
		]

		project.ext.powerMockDependencies = [
			"org.powermock:powermock-core:${project.powerMockVersion}",
			"org.powermock:powermock-api-support:${project.powerMockVersion}",
			"org.powermock:powermock-module-junit4-common:${project.powerMockVersion}",
			"org.powermock:powermock-module-junit4:${project.powerMockVersion}",
			project.dependencies.create("org.powermock:powermock-api-mockito:${project.powerMockVersion}") {
				exclude group: 'org.mockito', module: 'mockito-all'
			},
			"org.powermock:powermock-reflect:${project.powerMockVersion}"
		]

		project.ext.seleniumDependencies = [
			"org.seleniumhq.selenium:htmlunit-driver",
			"org.seleniumhq.selenium:selenium-support"
		]


		project.ext.springCoreDependency = [
			project.dependencies.create("org.springframework:spring-core") {
				exclude(group: 'commons-logging', module: 'commons-logging')
			}
		]

		project.ext.jstlDependencies = [
				"javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api:${project.jstlVersion}",
				"org.apache.taglibs:taglibs-standard-jstlel:1.2.1"
		]

		project.ext.apachedsDependencies = [
				"org.apache.directory.server:apacheds-core:${project.apacheDsVersion}",
				"org.apache.directory.server:apacheds-core-entry:${project.apacheDsVersion}",
				"org.apache.directory.server:apacheds-protocol-shared:${project.apacheDsVersion}",
				"org.apache.directory.server:apacheds-protocol-ldap:${project.apacheDsVersion}",
				"org.apache.directory.server:apacheds-server-jndi:${project.apacheDsVersion}",
				'org.apache.directory.shared:shared-ldap:0.9.15'
		]
	}
}
