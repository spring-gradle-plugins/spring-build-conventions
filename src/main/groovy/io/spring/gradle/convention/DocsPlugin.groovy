package io.spring.gradle.convention

import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.Action;
import org.gradle.api.tasks.Sync;

/**
 * Aggregates asciidoc, javadoc, and deploying of the docs into a single plugin
 */
public class DocsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("org.asciidoctor.convert");
		pluginManager.apply(DeployDocsPlugin);
		pluginManager.apply(JavadocApiPlugin);

		project.asciidoctorj {
			version = '1.5.6'
		}

    String projectName = Utils.getProjectName(project);
    String pdfFilename = projectName + "-reference.pdf";

		Task docsZip = project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'asciidoctor'
			group = 'Distribution'
			baseName = project.rootProject.name
			classifier = 'docs'
			description = "Builds -${classifier} archive containing all " +
				"Docs for deployment at docs.spring.io"

			from(project.tasks.asciidoctor.outputs) {
				into 'reference'
				include 'html5/**'
        include 'pdf/**'
        rename "index.pdf", pdfFilename
			}
			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
			duplicatesStrategy 'exclude'
		}

    def docResourcesVersion = '0.1.0.RELEASE'

    project.repositories {
      maven { url 'https://repo.spring.io/plugins-release' }
    }

    final Configuration config = project.getConfigurations().create("docResources");
    config.defaultDependencies(new Action<DependencySet>() {
      public void execute(DependencySet dependencies) {
      dependencies.add(project.getDependencies().create("io.spring.docresources:spring-doc-resources:${docResourcesVersion}@zip"));
      }
    });

    Task assembleDocs = project.tasks.create("assembleDocs", Sync.class) {
      from {
        project.configurations.docResources.collect { project.zipTree(it) }
      }
      // and doc sources
      from "src/docs/asciidoc/"
        // to a build directory of your choice
        into "${project.buildDir}/asciidoc/working"
    }

		project.tasks.asciidoctor {
      dependsOn 'assembleDocs'
      sourceDir "${project.buildDir}/asciidoc/working"
      outputDir "${project.buildDir}/asciidoc/output"
      sources {
        include '*.adoc'
      }
      resources {
        from(sourceDir) {
          include 'images/*', 'css/**', 'js/**'
        }
      }
      logDocuments = true
			backends = ['html5', 'pdf']
			def ghTag = 'master'//snapshotBuild ? 'master' : project.version
			def ghUrl = "https://github.com/spring-projects/${projectName}/tree/$ghTag"
			options = [
					eruby: 'erubis',
			]
			attributes 'linkcss' : true,
					'icons' : 'font',
          'stylesdir' : 'css/',
          'stylesheet' : 'spring.css',
          'source-highlighter=highlight.js',
          'highlightjsdir=js/highlight',
          'highlightjs-theme=atom-one-dark-reasonable',
					'sectanchors' : true,
					idprefix: '',
					idseparator: '-',
					doctype: 'book',
					numbered: '',
					'${projectName}-version' : project.version,
					revnumber : project.version,
					'gh-url': ghUrl,
					'gh-samples-url': "$ghUrl/samples",
					'docinfo' : 'shared'
		}

		Task docs = project.tasks.create("docs") {
			group = 'Documentation'
			description 'An aggregator task to generate all the documentation'
			dependsOn docsZip
		}
		project.tasks.assemble.dependsOn docs
	}
}
