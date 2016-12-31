package io.spring.gradle.convention;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringMavenPlugin implements Plugin<Project> {
	private static final String ARCHIVES = "archives";
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void apply(Project project) {
		project.getPluginManager().apply(JavaPlugin.class);
		project.getPluginManager().apply(MavenPlugin.class);
		project.getPluginManager().apply(SigningPlugin.class);

		Javadoc javadoc = (Javadoc) project.getTasks().findByPath("javadoc");
		Jar javadocJar = project.getTasks().create("javadocJar", Jar.class);
		javadocJar.setClassifier("javadoc");
		javadocJar.from(javadoc);

		JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet mainSourceSet = java.getSourceSets().getByName("main");
		Jar sourcesJar = project.getTasks().create("sourcesJar", Jar.class);
		sourcesJar.setClassifier("sources");
		sourcesJar.from(mainSourceSet.getAllSource());

		project.getArtifacts().add(ARCHIVES, javadocJar);
		project.getArtifacts().add(ARCHIVES, sourcesJar);

		SigningExtension sign = project.getExtensions().findByType(SigningExtension.class);
		sign.sign(project.getConfigurations().getByName(ARCHIVES));
	}

}
