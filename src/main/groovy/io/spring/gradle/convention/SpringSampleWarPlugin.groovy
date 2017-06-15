/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.spring.gradle.convention;

import org.gradle.api.Project
import org.gradle.api.Task;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test

/**
 * @author Rob Winch
 */
public class SpringSampleWarPlugin extends SpringSamplePlugin {

	@Override
	public void additionalPlugins(Project project) {
		super.additionalPlugins(project);

		PluginManager pluginManager = project.getPluginManager();

		pluginManager.apply("war");
		pluginManager.apply("org.akhikhl.gretty");

		project.gretty {
			servletContainer = 'tomcat8'
			contextPath = '/'
			fileLogEnabled = false
		}

		project.tasks.withType(Test).all { task ->
			if("integrationTest".equals(task.name)) {
				applyForIntegrationTest(project, task)
			}
		}
	}

	def applyForIntegrationTest(Project project, Task integrationTest) {
		project.gretty.integrationTestTask = 'integrationTest'

		def prepareAppServerForIntegrationTests = project.tasks.create('prepareAppServerForIntegrationTests') {
			group = 'Verification'
			description = 'Prepares the app server for integration tests'
			doFirst {
				project.gretty {
					httpPort = randomPort()
					servicePort = randomPort()
					statusPort = randomPort()
				}
			}
		}

		project.tasks.withType(org.akhikhl.gretty.AppBeforeIntegrationTestTask).all { task ->
			task.dependsOn prepareAppServerForIntegrationTests
		}

		integrationTest.doFirst {
			int httpPort = project.gretty.httpPort
			String host = project.gretty.host ?: 'localhost'
			String contextPath = project.gretty.contextPath
			String httpBaseURI = "http://${host}:${httpPort}${contextPath}"
			integrationTest.systemProperty 'app.port', httpPort
			integrationTest.systemProperty 'app.httpPort', httpPort
			integrationTest.systemProperty 'app.baseURI', httpBaseURI
			integrationTest.systemProperty 'app.httpBaseURI', httpBaseURI

			integrationTest.systemProperty 'geb.build.baseUrl', httpBaseURI
			integrationTest.systemProperty 'geb.build.reportsDir', 'build/geb-reports'
		}
	}

	def randomPort() {
		def socket = new ServerSocket(0)
		int result = socket.localPort
		socket.close()
		result
	}
}
