package domain;

import java.io.IOException;

interface IProcessRunner {
	/**
	 * @param command The program and parameters to run
	 * @return The output from running the process and whether it succeeded
	 */
	ProcessResult runProcess(String[] command) throws IOException, InterruptedException;
}
