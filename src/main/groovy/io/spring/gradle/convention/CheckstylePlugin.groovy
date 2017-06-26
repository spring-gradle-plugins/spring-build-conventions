/*
 * Copyright 2016-2017 the original author or authors.
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

package io.spring.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * Adds and configures Checkstyle plugin.
 *
 * @author Vedran Pavic
 */
class CheckstylePlugin implements Plugin<Project> {

	final CHECKSTYLE_DIR = 'config/checkstyle'

	@Override
	void apply(Project project) {
		project.plugins.withType(JavaPlugin) {
			def checkstyleDir = project.rootProject.file(CHECKSTYLE_DIR)
			if (checkstyleDir.exists() && checkstyleDir.directory) {
				project.getPluginManager().apply('checkstyle')

				project.checkstyle {
					configFile = project.rootProject.file("$CHECKSTYLE_DIR/checkstyle.xml").
					configProperties.configDir = configFile.parentFile
					toolVersion = '6.16.1'
				}
			}
		}
	}

}
