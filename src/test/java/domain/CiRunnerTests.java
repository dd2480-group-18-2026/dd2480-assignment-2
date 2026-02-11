package domain;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.GitHubEvent.Commit;
import domain.GitHubEvent.Repository;


@ExtendWith(MockitoExtension.class)
public class CiRunnerTests {
	// Cloning/building is outside the scope of a unit test, so we use a mocked ProcessRunner
	@Mock
	private ProcessRunner mockProcessRunner;

	private CiRunner runner;
	final String sha = "a1b2c3d";

	@BeforeEach
	void setup() throws IOException {
		runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
	}
	
	@Test 
	void runBuild_succeeds_whenAllProcessesSucceed() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runBuild();
		assertTrue(result.success);
	}

	@Test 
	void runBuild_returnsOnlyBuildOutput_onSuccess() throws IOException, InterruptedException {
		String output = "successful build";
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult(output, true));
		BuildResult result = runner.runBuild();
		assertEquals(result.buildOutput, output);
	}

	@Test 
	void runBuild_fails_whenAllProcessesFail() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertFalse(result.success);
	}

	@Test 
	void runBuild_buildOutputContainsGetRepoFailure_onGetRepoFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertTrue(result.buildOutput.contains(CiRunner.GetRepoFailure));
	}

	@Test 
	void runBuild_buildOutputContainsGetCommitFailure_onGetCommitFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertTrue(result.buildOutput.contains(CiRunner.GetCommitFailure));
	}

	@Test 
	void runBuild_buildOutputContainsOutput_onFailure() throws IOException, InterruptedException {
		String output = "some output";
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult(output, false));
		BuildResult result = runner.runBuild();
		assertTrue(result.buildOutput.contains(output));
	}

	@Test
	void runBuild_returnsCorrectCommitHash_onSuccess() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runBuild();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runBuild_returnsCorrectCommitHash_onFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runBuild_returnsExpectedDate_onSuccess() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		Date date = new Date();
		BuildResult result = runner.runBuild();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}

	@Test
	void runBuild_returnsExpectedDate_onFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		Date date = new Date();
		BuildResult result = runner.runBuild();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}

	@Test 
	void runCompile_succeeds_whenAllProcessesSucceed() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runCompile();
		assertTrue(result.success);
	}

	@Test 
	void runCompile_returnsOnlyBuildOutput_onSuccess() throws IOException, InterruptedException {
		String output = "successful build";
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult(output, true));
		BuildResult result = runner.runCompile();
		assertEquals(result.buildOutput, output);
	}

	@Test 
	void runCompile_fails_whenAllProcessesFail() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runCompile();
		assertFalse(result.success);
	}

	@Test 
	void runCompile_buildOutputContainsGetRepoFailure_onGetRepoFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runCompile();
		assertTrue(result.buildOutput.contains(CiRunner.GetRepoFailure));
	}

	@Test 
	void runCompile_buildOutputContainsGetCommitFailure_onGetCommitFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runCompile();
		assertTrue(result.buildOutput.contains(CiRunner.GetCommitFailure));
	}

	@Test 
	void runCompile_buildOutputContainsOutput_onFailure() throws IOException, InterruptedException {
		String output = "some output";
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult(output, false));
		BuildResult result = runner.runCompile();
		assertTrue(result.buildOutput.contains(output));
	}

	@Test
	void runCompile_returnsCorrectCommitHash_onSuccess() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runCompile();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runCompile_returnsCorrectCommitHash_onFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runCompile();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runCompile_returnsExpectedDate_onSuccess() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		Date date = new Date();
		BuildResult result = runner.runCompile();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}

	@Test
	void runCompile_returnsExpectedDate_onFailure() throws IOException, InterruptedException {
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		Date date = new Date();
		BuildResult result = runner.runCompile();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}
}