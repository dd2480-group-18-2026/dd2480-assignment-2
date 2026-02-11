package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import domain.GitHubEvent.Repository;
import domain.GitHubEvent.Commit;
import tools.Cleanup;

/**
 * Class that handles running the CI service and getting the results for a given repository
 */
public class CiRunner {
	private class CommandResult {
		public final String output;
		public final boolean success;

		public CommandResult(String output, boolean success) {
			this.output = output;
			this.success = success;
		}
	};

	private final String repoURL;
	private final String commitHash;
	private final String repoLOC = "./Temp_CiRunner_Output";

	/**
	 * 
	 * @param repo The external repository that the CI will run on
	 * @param commit The specific commit to be inspected
	 * @throws IOException
	 */
	public CiRunner(Repository repo, Commit commit) throws IOException {
		this.repoURL = repo.getUrl();
		this.commitHash = commit.getSha();
	}

	/**
	 * Runs the CI service, getting and building/testing a maven project from a GitHub source
	 * 
	 * @return BuildResult containing the commit SHA hash, date method was called, 
	 * text output from the build, and boolean success status
	 * @throws IOException When temporary files created could not be fully deleted
	 */
	public BuildResult runBuild() throws IOException {
		Date currentDate = new Date();
		boolean cloneSuccess = false;
		boolean getCommitSuccess = false;
		boolean buildSuccess = false; 
		String buildOutput;

		cloneSuccess = cloneRepo(repoURL, repoLOC);
		getCommitSuccess = getCommit(repoLOC, commitHash);

		CommandResult build = buildRepo(repoLOC);
		buildSuccess = build.success;
		buildOutput = build.output;

		Path path = Paths.get(repoLOC);
		try {
			Cleanup.deleteRecursively(path);
		} catch (IOException e) {
			System.err.println("Error deleting temporary files");
			throw new IOException();
		}

		boolean success = false; 

		if (cloneSuccess && getCommitSuccess && buildSuccess) {
			success =  true;
		}
		else {
			if (!cloneSuccess) throw new RuntimeException("Failure getting repository");
			if (!getCommitSuccess) throw new RuntimeException("Failure getting commit");
		}
		
		BuildResult buildResult = new BuildResult(commitHash, currentDate, buildOutput, success);

		return buildResult;
	}

	/**
	 * Runs the CI service, getting and compiling a maven project from a GitHub source
	 * Does not run tests
	 * 
	 * @return BuildResult containing the commit SHA hash, date method was called, 
	 * text output from the build, and boolean success status
	 * @throws IOException When temporary files created could not be fully deleted
	 */
	public BuildResult runCompile() throws IOException {
		Date currentDate = new Date();
		boolean cloneSuccess = false;
		boolean getCommitSuccess = false;
		boolean buildSuccess = false; 
		String buildOutput;

		cloneSuccess = cloneRepo(repoURL, repoLOC);
		getCommitSuccess = getCommit(repoLOC, commitHash);

		CommandResult build = compileRepo(repoLOC);
		buildSuccess = build.success;
		buildOutput = build.output;

		Path path = Paths.get(repoLOC);
		try {
			Cleanup.deleteRecursively(path);
		} catch (IOException e) {
			System.err.println("Error deleting temporary files");
			throw new IOException();
		}

		boolean success = false; 

		if (cloneSuccess && getCommitSuccess && buildSuccess) {
			success =  true;
		}
		else {
			if (!cloneSuccess) throw new RuntimeException("Failure getting repository");
			if (!getCommitSuccess) throw new RuntimeException("Failure getting commit");
		}
		
		BuildResult buildResult = new BuildResult(commitHash, currentDate, buildOutput, success);

		return buildResult;
	}

	private boolean cloneRepo(String repoURL, String repoLOC) {
		boolean success = false;

		try {
			ProcessBuilder cloneBuilder = new ProcessBuilder("git", "-C", repoLOC, "clone", repoURL, repoLOC);
			cloneBuilder.redirectErrorStream(true);
			Process cloneProcess = cloneBuilder.start();

			int result = cloneProcess.waitFor();
			if (result == 0) {
				success = true;
			}
		}
		catch (IOException | InterruptedException e) {
			System.err.println("Error cloning");
		}
		return success;
	} 

	private boolean getCommit(String repoLOC, String commitHash) {
		boolean success = false;

		try {
			ProcessBuilder switchBuilder = new ProcessBuilder("git", "-C", repoLOC, "switch", "--detach", commitHash);
			switchBuilder.redirectErrorStream(true);
			Process switchProcess = switchBuilder.start();

			int result = switchProcess.waitFor();
			if (result == 0) {
				success = true;
			}
		}
		catch (IOException | InterruptedException e) {
			System.err.println("Error getting commit");
		}
		return success;
	} 

	private CommandResult buildRepo(String repoLOC) {
		boolean success = false;
		StringBuilder outputString = new StringBuilder();

		try {
			String mavenLOC = repoLOC + "/pom.xml";
			ProcessBuilder compileBuilder = new ProcessBuilder("mvn", "test", "-f", mavenLOC);
			compileBuilder.redirectErrorStream(true);
			Process cloneProcess = compileBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));

			int result = cloneProcess.waitFor();
			String line;

			while((line = reader.readLine()) != null) {
				System.out.println(line);
				outputString.append(line + "\n");
			} 
			if (result == 0) {
				success = true;
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Error building");
		}
		return new CommandResult(outputString.toString(), success);
	}

	private CommandResult compileRepo(String repoLOC) {
		boolean success = false;
		StringBuilder outputString = new StringBuilder();

		try {
			String mavenLOC = repoLOC + "/pom.xml";
			ProcessBuilder compileBuilder = new ProcessBuilder("mvn", "compile", "-B", "-f", mavenLOC);
			compileBuilder.redirectErrorStream(true);
			Process cloneProcess = compileBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));

			int result = cloneProcess.waitFor();
			String line;

			while((line = reader.readLine()) != null) {
				System.out.println(line);
				outputString.append(line + "\n");
			} 
			if (result == 0) {
				success = true;
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Error building");
		}
		return new CommandResult(outputString.toString(), success);
	}
}