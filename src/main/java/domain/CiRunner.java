package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CiRunner {
	public CiRunner(String repoURL, String repoLOC) throws IOException, InterruptedException {
		cloneRepo(repoURL, repoLOC);
		compileRepo(repoLOC);
		
		File file = new File(repoLOC);
		deleteDir(file);
	}

	private boolean cloneRepo(String repoURL, String repoLOC) throws IOException, InterruptedException {
		ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoURL, repoLOC);
		cloneBuilder.redirectErrorStream(true);
		Process cloneProcess = cloneBuilder.start();

		int result = cloneProcess.waitFor();
		if (result == 0) {
			return true;
		}
		return false;
	}

	private void compileRepo(String repoLOC) throws IOException, InterruptedException {
		String mavenLOC = repoLOC + "/pom.xml";
		ProcessBuilder compileBuilder = new ProcessBuilder("mvn.cmd", "compile", "-f", mavenLOC);
		compileBuilder.redirectErrorStream(true);
		Process cloneProcess = compileBuilder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));
		int res = cloneProcess.waitFor();
		System.out.println(res);
		String line;
		while((line = reader.readLine()) != null) {
			System.out.println(line);
		}
	}

	private void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (! Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}
}