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
	private OutputStream output;

	@TaskAction
	public void dependencyManagementExport() throws IOException {
		if(output == null) {
			output = new FileOutputStream(getProject().getRootProject().file("gradle/dependency-management.properties"));
		}

		DependencyManagementExtension dependencyMgmt = (DependencyManagementExtension) getProject().getExtensions().getByName("dependencyManagement");

		Configuration springIoTestRuntime = getProject().getConfigurations().getByName("springIoTestRuntime");
		Map<String, String> versionsForConfiguration = dependencyMgmt.getManagedVersionsForConfiguration(springIoTestRuntime);

		Properties properties = new SortedProperties();
		properties.putAll(versionsForConfiguration);

		properties.store(output, "Export from " + getProject().findProperty("springIoVersion"));
	}

	void setOutputFile(File file) throws IOException {
		this.output = new FileOutputStream(file);
	}
}
