package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import domain.GitHubEvent.Repository;
import domain.GitHubEvent.Commit;
import tools.Cleanup;


public class CiRunner {
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
	public boolean run() throws IOException {
		boolean cloneSuccess = false;
		boolean getCommitSuccess = false;
		boolean compileSucess = false; 

		cloneSuccess = cloneRepo(repoURL, repoLOC);
		getCommitSuccess = getCommit(repoLOC, commitHash);
		compileSucess = compileRepo(repoLOC);

		Path path = Paths.get(repoLOC);
		try {
			Cleanup.deleteRecursively(path);
		} catch (IOException e) {
			System.err.println("Error deleting temporary files");
			throw new IOException();
		}

		if (cloneSuccess && getCommitSuccess && compileSucess) {
			return true;
		}
		else {
			if (!cloneSuccess) System.out.println("Clone failure");
			if (!cloneSuccess) System.out.println("Get commit failure");
			if (!cloneSuccess) System.out.println("Compile failure");
		}
		return false;
	}

	private boolean cloneRepo(String repoURL, String repoLOC) {
		try {
			ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoURL, repoLOC);
			cloneBuilder.redirectErrorStream(true);
			Process cloneProcess = cloneBuilder.start();

			int result = cloneProcess.waitFor();
			if (result == 0) {
				return true;
			}
		}
		catch (IOException | InterruptedException e) {
			System.err.println("Error cloning");
			return false;
		}
		return false;
	} 

	private boolean getCommit(String repoLOC, String commitHash) {
		try {
			ProcessBuilder checkoutBuilder = new ProcessBuilder("git", "switch", "--detach", commitHash);
			checkoutBuilder.redirectErrorStream(true);
			Process checkoutProcess = checkoutBuilder.start();

			int result = checkoutProcess.waitFor();
			if (result == 0) {
				return true;
			}
		}
		catch (IOException | InterruptedException e) {
			System.err.println("Error getting commit");
			return false;
		}
		return false;
	} 


	private boolean compileRepo(String repoLOC) {
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
			} 
			if (result == 0) {
				return true;
			}
		} catch (IOException | InterruptedException e) {
			System.err.println("Error compiling");
			return false;
		}
		return false;
	}
}