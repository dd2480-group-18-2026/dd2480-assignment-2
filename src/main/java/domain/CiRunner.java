package domain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import domain.GitHubEvent.Repository;
import domain.GitHubEvent.Commit;

import tools.Cleanup;

/**
 * Class that handles running the CI service and getting the results for a given repository
 * 
 */
public class CiRunner {
	private final String repoURL;
	private final String commitHash;
	private final String repoLOC = "./Temp_CiRunner_Output";
	private final IProcessRunner runner;

	/**
	 * Constant indicating output when repository could not be obtained
	 */
	public static final String GetRepoFailure = "Error: Failure getting repository";
	/**
	 * Constant indicating output when commit could not be obtained
	 */
	public static final String GetCommitFailure = "Error: Failure getting commit";


	/**
	 * 
	 * @param repo The external repository that the CI will run on
	 * @param commit The specific commit to be inspected
	 * @throws IOException
	 */
	public CiRunner(Repository repo, Commit commit, IProcessRunner runner) throws IOException {
		this.repoURL = repo.getUrl();
		this.commitHash = commit.getSha();
		this.runner = runner;
	}

	/**
	 * Runs the CI service, getting and building/testing a maven project from a GitHub source
	 * 
	 * @return BuildResult containing the commit SHA hash, date method was called, 
	 * text output from the build, and boolean success status. If repository could not be obtained,
	 * this is indicated in the text output
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

		ProcessResult build = buildRepo(repoLOC);
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
			success = true;
		}
		else {
			if (!cloneSuccess) buildOutput = new String(GetRepoFailure + "\n") + buildOutput;
			if (!getCommitSuccess) buildOutput = new String(GetCommitFailure + "\n") + buildOutput;
		}
		
		BuildResult buildResult = new BuildResult(commitHash, currentDate, buildOutput, success);

		return buildResult;
	}

	/**
	 * Runs the CI service, getting and compiling a maven project from a GitHub source. Does not run tests
	 * 
	 * @return BuildResult containing the commit SHA hash, date method was called, 
	 * text output from the build, and boolean success status. If repository could not be obtained,
	 * this is indicated in the text output
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

		ProcessResult build = compileRepo(repoLOC);
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
			success = true;
		}
		else {
			if (!cloneSuccess) buildOutput = new String("Error: Failure getting repository\n") + buildOutput;
			if (!getCommitSuccess) buildOutput = new String("Error: Failure getting commit\n") + buildOutput;
		}
		
		BuildResult buildResult = new BuildResult(commitHash, currentDate, buildOutput, success);

		return buildResult;
	}

	private boolean cloneRepo(String repoURL, String repoLOC) {
		ProcessResult result = new ProcessResult("", false);
		try {
			result = runner.runProcess(new String[]{"git", "clone", repoURL, repoLOC});
		} catch(IOException | InterruptedException e) {
			System.err.println("Error cloning repository");
		}

		return result.success;
	} 

	private boolean getCommit(String repoLOC, String commitHash) {
		ProcessResult result = new ProcessResult("", false);
		try {
			result = runner.runProcess(new String[]{"git", "-C", repoLOC, "switch", "--detach", commitHash});
		} catch(IOException | InterruptedException e) {
			System.err.println("Error getting commit");
		}

		return result.success;
	} 

	private ProcessResult buildRepo(String repoLOC) {
		ProcessResult result = new ProcessResult("", false);
		try {
			String mavenLOC = repoLOC + "/pom.xml";
			result = runner.runProcess(new String[]{"mvn", "test", "-B", "-f", mavenLOC});
		} catch(IOException | InterruptedException e) {
			System.err.println("Error building");
		}

		return result;
	}

	private ProcessResult compileRepo(String repoLOC) {
		ProcessResult result = new ProcessResult("", false);
		try {
			String mavenLOC = repoLOC + "/pom.xml";
			result = runner.runProcess(new String[]{"mvn", "compile", "-B", "-f", mavenLOC});
		} catch(IOException | InterruptedException e) {
			System.err.println("Error compiling");
		}

		return result;
	}
}