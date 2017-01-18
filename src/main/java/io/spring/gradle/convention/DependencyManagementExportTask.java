package io.spring.gradle.convention;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;

public class DependencyManagementExportTask extends DefaultTask {
	PrintWriter writer = new PrintWriter(System.out);

	@TaskAction
	public void dependencyManagementExport() throws IOException {
		DependencyManagementExtension dependencyMgmt = (DependencyManagementExtension) getProject().getExtensions().getByName("dependencyManagement");

		Configuration springIoTestRuntime = getProject().getConfigurations().getByName("springIoTestRuntime");
		Map<String, String> versionsForConfiguration = dependencyMgmt.getManagedVersionsForConfiguration(springIoTestRuntime);
		Map<String, String> managedVersions = new TreeMap<String,String>(versionsForConfiguration);

		writer.println("# Export from " + getProject().findProperty("springIoVersion"));
		for(Map.Entry<String, String> entry : managedVersions.entrySet()) {
			writer.println(entry.getKey() + "=" + entry.getValue());
		}
		writer.flush();
	}

	void setOutputFile(File file) throws IOException {
		this.writer = new PrintWriter(new FileWriter(file));
	}
}
