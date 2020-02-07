package io.spring.gradle.convention

import org.asciidoctor.gradle.jvm.AsciidoctorJExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

/**
 * Aggregates asciidoc, javadoc, and deploying of the docs into a single plugin
 */
public class DocsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("org.asciidoctor.jvm.convert");
		pluginManager.apply("org.asciidoctor.jvm.pdf");
		pluginManager.apply(DeployDocsPlugin);
		pluginManager.apply(JavadocApiPlugin);

		String projectName = Utils.getProjectName(project);
		String pdfFilename = projectName + "-reference.pdf";

		project.getExtensions().getByType(AsciidoctorJExtension.class).fatalWarnings(".*");

		Task docsZip = project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'asciidoctor'
			group = 'Distribution'
			baseName = project.rootProject.name
			classifier = 'docs'
			description = "Builds -${classifier} archive containing all " +
				"Docs for deployment at docs.spring.io"

			from(project.tasks.asciidoctor.outputs) {
				into 'reference/html5'
				include '**'
			}
			from(project.tasks.asciidoctorPdf.outputs) {
				into 'reference/pdf'
				include '**'
				rename "index.pdf", pdfFilename
			}
			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
			duplicatesStrategy 'exclude'
		}

		project.repositories {
		  maven { url 'https://repo.spring.io/plugins-release' }
		}

		project.configurations {
			asciidoctorExtensions
		}

		project.dependencies {
			asciidoctorExtensions 'io.spring.asciidoctor:spring-asciidoctor-extensions-block-switch:0.4.0.RELEASE'
		}

		def docResourcesVersion = "0.1.3.RELEASE"

		final Configuration config = project.getConfigurations().create("docResources");
		config.defaultDependencies(new Action<DependencySet>() {
			public void execute(DependencySet dependencies) {
				dependencies.add(project.getDependencies().create("io.spring.docresources:spring-doc-resources:${docResourcesVersion}@zip"));
			}
		});

		project.tasks.asciidoctor {
			dependsOn 'assembleDocs', 'syncDocumentationSource'
			sourceDir "${project.buildDir}/work/docs/asciidoc/"
			sources {
				include "**/*.adoc"
				exclude '_*/**'
			}
			configurations 'asciidoctorExtensions'
			options doctype: 'book', eruby: 'erubis'
			attributes([icons: 'font',
						idprefix: '',
						idseparator: '-',
						docinfo: 'shared',
						revnumber: project.version,
						sectanchors: '',
						sectnums: '',
						'source-highlighter': 'highlight.js',
						highlightjsdir: 'js/highlight',
						'highlightjs-theme': 'github',
						stylesheet: 'css/spring.css',
						"linkcss": true,
						'spring-version': project.version])
			baseDirFollowsSourceDir()
		}

		project.tasks.asciidoctorPdf {
			options doctype: 'book', eruby: 'erubis'
			sources {
				include "**/*.adoc"
				exclude '_*/**'
			}
			baseDirFollowsSourceDir()
		}

		Task assembleDocs = project.tasks.create("assembleDocs", Sync.class) {
			from {
				project.configurations.docResources.collect { project.zipTree(it) }
			}
			from "src/docs/asciidoc"
			into "${project.asciidoctor.sourceDir}/"
		}

		Task syncDocumentationSource = project.tasks.create("syncDocumentationSource", Sync.class) {
			from {
				project.configurations.docResources.collect { project.zipTree(it) }
			}
			into "${project.asciidoctor.outputDir}"
		}

		Task docs = project.tasks.create("docs") {
			group = 'Documentation'
			description 'An aggregator task to generate all the documentation'
			dependsOn docsZip
		}
		project.tasks.assemble.dependsOn docs
	}
}
