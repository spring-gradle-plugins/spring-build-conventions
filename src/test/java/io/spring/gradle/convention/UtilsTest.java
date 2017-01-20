package io.spring.gradle.convention;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {
	@Mock
	Project project;
	@Mock
	Project rootProject;

	@Before
	public void setup() {
		when(project.getRootProject()).thenReturn(rootProject);
	}

	@Test
	public void getProjectName() {
		when(rootProject.getName()).thenReturn("spring-security");

		assertThat(Utils.getProjectName(project)).isEqualTo("spring-security");
	}

	@Test
	public void getProjectNameWhenEndsWithBuildThenStippedOut() {
		when(rootProject.getName()).thenReturn("spring-security-build");

		assertThat(Utils.getProjectName(project)).isEqualTo("spring-security");
	}
}
