package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.ProcessBuilder;

/**
 * Class that handles running processes and getting their output
 */
final public class ProcessRunner implements IProcessRunner {
	public ProcessResult runProcess(String[] command) throws IOException, InterruptedException {
		boolean success = false;
		StringBuilder outputString = new StringBuilder();
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		int result = process.waitFor();
		String line;

		while((line = reader.readLine()) != null) {
			outputString.append(line + "\n");
		} 
		if (result == 0) {
			success = true;
		}

		return new ProcessResult(outputString.toString(), success);
	}
}
