package io.spring.gradle.convention

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringDependencyManagementConventionPlugin implements Plugin<Project> {


	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(DependencyManagementPlugin)

		project.dependencyManagement {
			dependencies {
				["org.springframework:spring-aop:4.3.5.RELEASE",
"org.springframework:spring-aspects:4.3.5.RELEASE",
"org.springframework:spring-beans:4.3.5.RELEASE",
"org.springframework:spring-context:4.3.5.RELEASE",
"org.springframework:spring-context-support:4.3.5.RELEASE",
"org.springframework:spring-core:4.3.5.RELEASE",
"org.springframework:spring-expression:4.3.5.RELEASE",
"org.springframework:spring-instrument:4.3.5.RELEASE",
"org.springframework:spring-instrument-tomcat:4.3.5.RELEASE",
"org.springframework:spring-jdbc:4.3.5.RELEASE",
"org.springframework:spring-jms:4.3.5.RELEASE",
"org.springframework:spring-messaging:4.3.5.RELEASE",
"org.springframework:spring-orm:4.3.5.RELEASE",
"org.springframework:spring-oxm:4.3.5.RELEASE",
"org.springframework:spring-test:4.3.5.RELEASE",
"org.springframework:spring-tx:4.3.5.RELEASE",
"org.springframework:spring-web:4.3.5.RELEASE",
"org.springframework:spring-webmvc:4.3.5.RELEASE",
"org.springframework:spring-webmvc-portlet:4.3.5.RELEASE",
"org.springframework:spring-websocket:4.3.5.RELEASE",
"org.springframework:springloaded:1.2.6.RELEASE"].each {
					dependency it
				}
			}
		}
	}
}