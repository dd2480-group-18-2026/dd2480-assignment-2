package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import domain.GitHubEvent.Repository;
import domain.GitHubEvent.Commit;
import domain.BuildResult;
import tools.Cleanup;


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
	private final String repoLOC = "./";

	public CiRunner(Repository repo, Commit commit) throws IOException {
		this.repoURL = repo.getUrl();
		this.commitHash = commit.getSha();
	}

	/**
	 * Runs the CI service
	 * 
	 * @return True if all actions succeed, false otherwise
	 * @throws IOException When temporary files created could not be fully deleted
	 */
	public BuildResult run() throws IOException {
		boolean cloneSuccess = false;
		boolean getCommitSuccess = false;
		boolean compileSucess = false; 
		String compileOutput;

		cloneSuccess = cloneRepo(repoURL, repoLOC);
		getCommitSuccess = getCommit(repoLOC, commitHash);
		CommandResult compileResult = compileRepo(repoLOC);
		compileSucess = compileResult.success;
		compileOutput = compileResult.output;

		Path path = Paths.get(repoLOC);
		try {
			Cleanup.deleteRecursively(path);
		} catch (IOException e) {
			System.err.println("Error deleting temporary files");
			throw new IOException();
		}

		boolean success = false; 

		if (cloneSuccess && getCommitSuccess && compileSucess) {
			success =  true;
		}
		else {
			if (!cloneSuccess) System.err.println("Clone failure");
			if (!cloneSuccess) System.err.println("Get commit failure");
		}
		
		BuildResult buildResult = new BuildResult(commitHash, new Date(), compileOutput, success);

		return buildResult;
	}

	private boolean cloneRepo(String repoURL, String repoLOC) {
		boolean success = false;
		try {
			ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoURL, repoLOC);
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
			ProcessBuilder checkoutBuilder = new ProcessBuilder("git", "switch", "--detach", commitHash);
			checkoutBuilder.redirectErrorStream(true);
			Process checkoutProcess = checkoutBuilder.start();

			int result = checkoutProcess.waitFor();
			if (result == 0) {
				success = true;
			}
		}
		catch (IOException | InterruptedException e) {
			System.err.println("Error getting commit");
		}
		return success;
	} 


	private CommandResult compileRepo(String repoLOC) {
		boolean success = false;
		StringBuilder outputString = new StringBuilder();

		try {
			String mavenLOC = repoLOC + "/pom.xml";
			ProcessBuilder compileBuilder = new ProcessBuilder("mvn", "compile", "-f", mavenLOC);
			compileBuilder.redirectErrorStream(true);
			Process cloneProcess = compileBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));

			int result = cloneProcess.waitFor();
			String line;

			while((line = reader.readLine()) != null) {
				System.out.println(line);
				outputString.append(line);
			} 
			if (result == 0) {
				success = true;
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Error compiling");
		}
		return new CommandResult(outputString.toString(), success);
	}

	private CommandResult testRepo(String repoLOC) {
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
				outputString.append(line);
			} 
			if (result == 0) {
				success = true;
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Error running tests");
		}
		return new CommandResult(outputString.toString(), success);
	}
}