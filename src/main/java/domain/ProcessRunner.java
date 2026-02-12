package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.lang.ProcessBuilder;

/**
 * Implementation of the IProcessRunner interface. Uses ProcessBuilder internally.
 */
final public class ProcessRunner implements IProcessRunner {
	/**
	 * Runs a given process using ProcessBuilder
	 * 
	 * @param command The program and parameters to run
	 * @return The output from running the process and whether it succeeded
	 */
	public ProcessResult runProcess(String[] command) throws IOException, InterruptedException {
		boolean success = false;
		StringBuilder outputString = new StringBuilder();
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;

		while((line = reader.readLine()) != null) {
			outputString.append(line + "\n");
		}
		
		int result = process.waitFor();
		
		if (result == 0) {
			success = true;
		}

		return new ProcessResult(outputString.toString(), success);
	}
}
