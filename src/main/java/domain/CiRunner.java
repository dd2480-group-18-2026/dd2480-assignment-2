package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CiRunner {
	public CiRunner(String repoURL) throws IOException, InterruptedException {
		ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoURL, "../../../tempRepo");
		cloneBuilder.redirectErrorStream(true);
		Process cloneProcess = cloneBuilder.start();
		BufferedReader cloneReader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));
		String line;
		cloneProcess.waitFor();

		while ((line = cloneReader.readLine()) != null) {
			System.out.println(line);
		}

		System.out.println("Runs");
		ProcessBuilder cloneBuilder = new ProcessBuilder("git", "clone", repoURL, "../../../tempRepo");
		cloneBuilder.redirectErrorStream(true);
		Process cloneProcess = cloneBuilder.start();
		BufferedReader cloneReader = new BufferedReader(new InputStreamReader(cloneProcess.getInputStream()));
		String line;
		cloneProcess.waitFor();

		while ((line = cloneReader.readLine()) != null) {
			System.out.println(line);
		}



		
		File file = new File("../../../tempRepo");
		deleteDir(file);
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