package io.spring.gradle.convention

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class DeployDocsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply('org.hidetake.ssh')

		project.ssh.settings {
			knownHosts = allowAnyHosts
		}
		project.remotes {
			docs {
				role 'docs'
				host = 'docs.af.pivotal.io'
				user = project.findProperty('deployDocsSshUsername')
				if(project.hasProperty('deployDocsSshKeyPath')) {
					identity = project.file(project.findProperty('deployDocsSshKeyPath'))
				}
				if(project.hasProperty('deployDocsSshPassphrase')) {
					passphrase = project.findProperty('deployDocsSshPassphrase')
				}
			}
		}

		project.task('deployDocs') {
			doFirst {
				project.ssh.run {
					session(project.remotes.docs) {
						def now = System.currentTimeMillis()
						def name = project.rootProject.name
						def version = project.rootProject.version
						def tempPath = "/tmp/${name}-${now}-docs/".replaceAll(' ', '_')
						execute "mkdir -p $tempPath"

						project.tasks.docsZip.outputs.each { o ->
							put from: o.files, into: tempPath
						}

						execute "unzip $tempPath*.zip -d $tempPath"

						def extractPath = "/var/www/domains/springsource.org/www/htdocs/autorepo/docs/${name}/${version}/"

						execute "rm -rf $extractPath"
						execute "mkdir -p $extractPath"
						execute "mv $tempPath/docs/* $extractPath"
					}
				}
			}
		}
	}
}