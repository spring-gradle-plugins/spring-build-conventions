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
	public void isSnapshotValidWithDot() {
		when(project.getVersion()).thenReturn("1.0.0.BUILD-SNAPSHOT");

		assertThat(Utils.isSnapshot(project)).isTrue();
	}

	@Test
	public void isSnapshotValidWithDash() {
		when(project.getVersion()).thenReturn("Theme-BUILD-SNAPSHOT");

		assertThat(Utils.isSnapshot(project)).isTrue();
	}

	@Test
	public void isSnapshotInvalid() {
		when(project.getVersion()).thenReturn("1.0.0.SNAPSHOT");

		assertThat(Utils.isSnapshot(project)).isFalse();
	}

	@Test
	public void isMilestoneValidWithDot() {
		when(project.getVersion()).thenReturn("1.0.0.M1");

		assertThat(Utils.isMilestone(project)).isTrue();
	}

	@Test
	public void isMilestoneValidWithDash() {
		when(project.getVersion()).thenReturn("Theme-M1");

		assertThat(Utils.isMilestone(project)).isTrue();
	}

	@Test
	public void isMilestoneInvalid() {
		when(project.getVersion()).thenReturn("1.0.0.M");

		assertThat(Utils.isMilestone(project)).isFalse();
	}

	@Test
	public void isReleaseCandidateValidWithDot() {
		when(project.getVersion()).thenReturn("1.0.0.RC1");

		assertThat(Utils.isMilestone(project)).isTrue();
	}

	@Test
	public void isReleaseCandidateValidWithDash() {
		when(project.getVersion()).thenReturn("Theme-RC1");

		assertThat(Utils.isMilestone(project)).isTrue();
	}

	@Test
	public void isReleaseCandidateInvalid() {
		when(project.getVersion()).thenReturn("1.0.0.RC");

		assertThat(Utils.isMilestone(project)).isFalse();
	}

	@Test
	public void isReleaseValidWithDot() {
		when(project.getVersion()).thenReturn("1.0.0.RELEASE");

		assertThat(Utils.isRelease(project)).isTrue();
	}

	@Test
	public void isReleaseValidWithDash() {
		when(project.getVersion()).thenReturn("Theme-RELEASE");

		assertThat(Utils.isRelease(project)).isTrue();
	}

	@Test
	public void isServiceReleaseValid() {
		when(project.getVersion()).thenReturn("Theme-SR1");

		assertThat(Utils.isRelease(project)).isTrue();
	}

	@Test
	public void isServiceReleaseInvalid() {
		when(project.getVersion()).thenReturn("Theme.SR1");

		assertThat(Utils.isRelease(project)).isFalse();
	}

}
