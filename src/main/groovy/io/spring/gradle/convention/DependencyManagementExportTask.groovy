package io.spring.gradle.convention;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;

public class DependencyManagementExportTask extends DefaultTask {

	@TaskAction
	public void dependencyManagementExport() throws IOException {
		def projects = project.subprojects + project
		def dependenciesToCollect = projects*.configurations*.findAll { it.canBeResolved }*.resolvedConfiguration.firstLevelModuleDependencies.flatten()

		def projectDependencies = projects.collect { p-> "${p.group}:${p.name}:${p.version}".toString() } as Set
		def dependencies = dependenciesToCollect.collect { d ->
			"${d.moduleGroup}:${d.moduleName}:${d.moduleVersion}".toString()
		}.sort() as Set

		println ''
		println ''
		println 'dependencyManagement {'
		println '\tdependencies {'
		dependencies.findAll { d-> !projectDependencies.contains(d)}.each {
			println "\t\tdependency '$it'"
		}
		println '\t}'
		println '}'
		println ''
		println ''
	}

	void setOutputFile(File file) throws IOException {
		this.output = new FileOutputStream(file);
	}
}
