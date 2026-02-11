import java.io.IOError;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import domain.GitHubEvent.Repository;
import domain.GitHubEvent.Commit;
import domain.BuildResult;
import domain.CiRunner;

class TempTest {

    @Test
    void testRuns() throws IOException {
		CiRunner runner = new CiRunner(
			new Repository("https://github.com/dd2480-group-18-2026/dd2480-assignment-2.git"), 
			new Commit("19cdd87")
		);
		BuildResult res = runner.runBuild();
		System.out.println("BUILD OUTPUT");
		System.out.println("---------------------------------------------------");
		System.out.println(res.buildOutput);
		System.out.println("---------------------------------------------------");
		System.out.println("BUILD OUTPUT");
    }

}
