package domain;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
	static CiRunner runner;
	@Mock
	static ProcessRunner mockProcessRunner;
	final static String sha = "a1b2c3d";
	
	@BeforeAll
	static void setup() throws IOException {
		runner = new CiRunner(
			new Repository("made/up/repo"),
			new Commit(sha),
			mockProcessRunner
		);
	}
	
	@Test 
	void runBuild_succeeds_whenRepoIsBuildableAndAllParametersCorrect() throws IOException {
		assertTrue(true);
	}
}
