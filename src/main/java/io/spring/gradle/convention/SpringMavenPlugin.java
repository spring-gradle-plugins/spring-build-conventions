package io.spring.gradle.convention;

import java.util.Collections;
import java.util.Comparator;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.maven.MavenPom;
import org.gradle.api.artifacts.maven.MavenResolver;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.plugins.MavenRepositoryHandlerConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.Upload;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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

		Upload uploadArchives = (Upload) project.getTasks().findByName("install");
		RepositoryHandler uploadRepositories = uploadArchives.getRepositories();

		MavenRepositoryHandlerConvention mavenRepositories= new DslObject(uploadRepositories).getConvention().getPlugin(MavenRepositoryHandlerConvention.class);
		MavenResolver mavenInstaller = mavenRepositories.mavenInstaller();
		MavenPom pom = mavenInstaller.getPom();

		for(Object o : pom.getDependencies()) {
			PropertyEditor p = new PropertyEditor(o);
			String scope = p.getProperty("getScope");
			if("optional".equals(scope)) {
				p.setProperty("setScope", "compile");
				p.setProperty("setOptional", "true");
			}
		}
		pom.whenConfigured(new Action<MavenPom>() {
			@Override
			public void execute(MavenPom pom) {
				Collections.sort(pom.getDependencies(), new Comparator<Object>() {
					@Override
					public int compare(Object l, Object r) {
						return mavenCompare(l).compareTo(mavenCompare(r));
					}
				});
			}
		});

		pom.withXml(new Action<XmlProvider>() {
			@Override
			public void execute(XmlProvider xml) {
				String rootProjectName = project.getRootProject().getName();
				XmlDocument doc = new XmlDocument((Document) xml.asElement().getParentNode());
				doc.findNode("version").insertElementWithText("name", project.getName());

				XmlNode dependencies = doc.findNode("dependencies");
				dependencies.insertElementWithText("description", project.getName());
				dependencies.insertElementWithText("url", "https://spring.io/" + rootProjectName);

				XmlNode organization = dependencies.insertElement("organization");
				organization.addChild("name", "spring.io");
				organization.addChild("url", "https://spring.io");

				XmlNode apacheLicense = dependencies.insertElement("licenses").addChild("license", null);
				apacheLicense.addChild("name", "The Apache Software License, Version 2.0");
				apacheLicense.addChild("url", "https://www.apache.org/licenses/LICENSE-2.0.txt");
				apacheLicense.addChild("distribution", "repo");

				XmlNode scm = dependencies.insertElement("scm");
				scm.addChild("url", "https://github.com/spring-projects/" + rootProjectName);
				scm.addChild("connection", "scm:git:git://github.com/spring-projects/" + rootProjectName);
				scm.addChild("developerConnection", "scm:git:git://github.com/spring-projects/" + rootProjectName);

			}
		});
	}

	private static class XmlDocument {
		private final Document doc;

		public XmlDocument(Document doc) {
			this.doc = doc;
		}

		public Element elementWithText(String name, String text) {
			Element newElement = doc.createElement(name);
			if(text != null) {
				newElement.appendChild(doc.createTextNode(text));
			}
			return newElement;
		}

		public XmlNode findNode(String name) {
			NodeList existingNodes = doc.getElementsByTagName(name);
			Node node = existingNodes.item(0);
			return new XmlNode(this, node);
		}
	}

	private static class XmlNode {
		private final XmlDocument doc;
		private final Node node;

		XmlNode(XmlDocument doc, Node node) {
			this.doc = doc;
			this.node = node;
		}

		public XmlNode insertElement(String newElementName) {
			Element newElement = doc.elementWithText(newElementName, null);
			node.getParentNode().insertBefore(newElement, node);
			return new XmlNode(doc, newElement);
		}

		public XmlNode addChild(String newElementName, String newElementValue) {
			Element newElement = doc.elementWithText(newElementName, newElementValue);
			node.appendChild(newElement);
			return new XmlNode(doc, newElement);
		}

		public XmlNode insertElementWithText(String newElementName, String newElementValue) {
			Element newElement = doc.elementWithText(newElementName, newElementValue);
			node.getParentNode().insertBefore(newElement, node);
			return this;
		}
	}

	private String mavenCompare(Object o) {
		PropertyEditor p = new PropertyEditor(o);
		return "" + p.getProperty("getScope") + p.getProperty("isOptional") + p.getProperty("getGroupId") + p.getProperty("getArtifactId"); // "$dep.scope:$dep.optional:$dep.groupId:$dep.artifactId";
	}


	private static class PropertyEditor {
		private final Object obj;

		PropertyEditor(Object obj) {
			this.obj = obj;
		}

		<T> T getProperty(String propertyName) {
			try {
				return (T) obj.getClass().getMethod(propertyName).invoke(obj);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		void setProperty(String propertyName, Object value) {
			try {
				obj.getClass().getMethod(propertyName, value.getClass()).invoke(obj, value);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
