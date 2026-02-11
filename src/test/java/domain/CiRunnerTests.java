package domain;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.BeforeAll;

import domain.GitHubEvent.Commit;
import domain.GitHubEvent.Repository;


@ExtendWith(MockitoExtension.class)
public class CiRunnerTests {
	@Mock
	private ProcessRunner mockProcessRunner;
	final String sha = "a1b2c3d";
	
	@Test 
	void runBuild_succeeds_whenRepoIsBuildableAndAllParametersCorrect() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runBuild();
		assertTrue(result.success);
	}

	@Test 
	void runBuild_returnsBuildOutput_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("successful build", true));
		BuildResult result = runner.runBuild();
		assertEquals(result.buildOutput, "successful build");
	}
}
