package domain;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeAll;

import domain.GitHubEvent.Commit;
import domain.GitHubEvent.Repository;

@ExtendWith(MockitoExtension.class)
public class CiRunnerTests {

	static CiRunner runner;
	final static String success_sha = "f158fa9";
	
	@BeforeAll
	static void setup() throws IOException {
		String workingDir = System.getProperty("user.dir");
		runner = new CiRunner(
			new Repository("file://" + workingDir + "/src/test/resources/TestMavenRepo"),
			new Commit(success_sha)
		);
	}
	
	@Test 
	void runBuild_succeeds_whenRepoIsBuildableAndAllParametersCorrect() throws IOException {
		BuildResult output = runner.runBuild();
		assertTrue(output.success);
	}
}
