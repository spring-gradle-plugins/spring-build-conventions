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
	public void getProjectNameWhenEndsWithBuildThenStrippedOut() {
		when(rootProject.getName()).thenReturn("spring-security-build");

		assertThat(Utils.getProjectName(project)).isEqualTo("spring-security");
	}

	@Test
	public void isSnapshot() {
		when(project.getVersion()).thenReturn("1.0.0.BUILD-SNAPSHOT");

		assertThat(Utils.isSnapshot(project)).isTrue();
	}

	@Test
	public void isMilestone() {
		when(project.getVersion()).thenReturn("1.0.0.M1");

		assertThat(Utils.isMilestone(project)).isTrue();
	}

	@Test
	public void isReleaseWithDot() {
		when(project.getVersion()).thenReturn("1.0.0.RELEASE");

		assertThat(Utils.isRelease(project)).isTrue();
	}

	@Test
	public void isReleaseWithDash() {
		when(project.getVersion()).thenReturn("Theme-RELEASE");

		assertThat(Utils.isRelease(project)).isTrue();
	}

}
