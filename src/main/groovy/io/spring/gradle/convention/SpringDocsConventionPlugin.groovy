package io.spring.gradle.convention

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SpringDocsConventionPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply("org.asciidoctor.convert");
		pluginManager.apply(DeployDocsPlugin);
		pluginManager.apply(JavadocApiPlugin);
		pluginManager.apply('docbook-reference')

		project.asciidoctorj {
			version = '1.5.4'
		}

		project.tasks.create('docsZip', Zip) {
			dependsOn 'api', 'asciidoctor'

			from(project.tasks.asciidoctor.outputs) {
				into 'reference'
			}
			from(project.tasks.api.outputs) {
				into 'api'
			}
			into 'docs'
		}

		project.tasks.asciidoctor {
			backends = ['docbook5']
			def ghTag = snapshotBuild ? 'master' : project.version
			def ghUrl = "https://github.com/spring-projects/spring-security/tree/$ghTag"
			options = [
			  eruby: 'erubis',
			  attributes: [
				  copycss : '',
				  icons : 'font',
				  'source-highlighter': 'prettify',
				  sectanchors : '',
				  toc2: '',
				  idprefix: '',
				  idseparator: '-',
				  doctype: 'book',
				  numbered: '',
				  'spring-security-version' : project.version,
				  'spring-version' : springVersion,
				  revnumber : project.version,
				  'gh-url': ghUrl,
				  'gh-samples-url': "$ghUrl/samples",
				  docinfo : ""
			  ]
			]
		}

		project.reference {
			sourceDir = new File(asciidoctor.outputDir , 'docbook5')
			pdfFilename = "spring-security-reference.pdf"
			epubFilename = "spring-security-reference.epub"
			expandPlaceholders = ""
		}
	}
}