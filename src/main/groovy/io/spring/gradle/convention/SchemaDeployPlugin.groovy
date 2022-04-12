package io.spring.gradle.convention

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.Plugin
import org.gradle.api.Project

public class SchemaDeployPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply('org.hidetake.ssh')

		project.ssh.settings {
			knownHosts = allowAnyHosts
		}
		project.remotes {
			docs {
				role 'docs'
				host = 'docs-ip.spring.io'
				user = project.findProperty('deployDocsSshUsername')
				if(project.hasProperty('deployDocsSshKeyPath')) {
					identity = project.file(project.findProperty('deployDocsSshKeyPath'))
				} else if (project.hasProperty('deployDocsSshKey')) {
					identity = project.findProperty('deployDocsSshKey')
				}
				if(project.hasProperty('deployDocsSshPassphrase')) {
					passphrase = project.findProperty('deployDocsSshPassphrase')
				}
			}
		}

		project.task('deploySchema') {
			dependsOn 'schemaZip'
			doFirst {
				project.ssh.run {
					session(project.remotes.docs) {
						def now = System.currentTimeMillis()
						def name = project.rootProject.name
						def version = project.rootProject.version
						def tempPath = "/tmp/${name}-${now}-schema/".replaceAll(' ', '_')

						execute "mkdir -p $tempPath"

						project.tasks.schemaZip.outputs.each { o ->
							println "Putting $o.files"
							put from: o.files, into: tempPath
						}

						execute "unzip $tempPath*.zip -d $tempPath"

						def extractPath = "/var/www/domains/springsource.org/www/htdocs/autorepo/schema/${name}/${version}/"

						execute "rm -rf $extractPath"
						execute "mkdir -p $extractPath"
						execute "rm -f $tempPath*.zip"
						execute "rm -rf $extractPath*"
						execute "mv $tempPath/* $extractPath"
					}
				}
			}
		}
	}
}