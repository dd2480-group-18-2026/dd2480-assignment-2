package domain;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	void runBuild_succeeds_whenAllProcessesSucceed() throws IOException, InterruptedException {
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

	@Test 
	void runBuild_fails_whenAllProcessesFail() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertFalse(result.success);
	}

	@Test 
	void runBuild_returnsBuildOutput_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("build failed", false));
		BuildResult result = runner.runBuild();
		assertEquals(result.buildOutput, "build failed");
	}

	@Test
	void runBuild_returnsCorrectCommitHash_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runBuild();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runBuild_returnsCorrectCommitHash_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runBuild_returnsExpectedDate_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		Date date = new Date();
		BuildResult result = runner.runBuild();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}

	@Test
	void runBuild_returnsExpectedDate_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		Date date = new Date();
		BuildResult result = runner.runBuild();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}
		
	@Test 
	void runCompile_succeeds_whenAllProcessesSucceed() throws IOException, InterruptedException {
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
	void runCompile_returnsBuildOutput_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("successful build", true));
		BuildResult result = runner.runBuild();
		assertEquals(result.buildOutput, "successful build");
	}

	@Test 
	void runCompile_fails_whenAllProcessesFail() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runBuild();
		assertFalse(result.success);
	}

	@Test 
	void runCompile_returnsBuildOutput_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("build failed", false));
		BuildResult result = runner.runBuild();
		assertEquals(result.buildOutput, "build failed");
	}

	@Test
	void runCompile_returnsCorrectCommitHash_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		BuildResult result = runner.runCompile();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runCompile_returnsCorrectCommitHash_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		BuildResult result = runner.runCompile();
		assertEquals(result.commitSHA, sha);
	}

	@Test
	void runCompile_returnsExpectedDate_onSuccess() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", true));
		Date date = new Date();
		BuildResult result = runner.runCompile();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}

	@Test
	void runCompile_returnsExpectedDate_onFailure() throws IOException, InterruptedException {
		CiRunner runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
		when(mockProcessRunner.runProcess(any())).thenReturn(new ProcessResult("", false));
		Date date = new Date();
		BuildResult result = runner.runCompile();
		long dateDelta = result.date.getTime() - date.getTime();
		assertTrue(dateDelta < 100);
	}
}