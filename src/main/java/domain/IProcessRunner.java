package domain;

import java.io.IOException;

/**
 * Interface for running programs
 */
interface IProcessRunner {
	/**
	 * Method used to run a given program 
	 * 
	 * @param command The program and parameters to run
	 * @return The output from running the process and whether it succeeded
	 */
	ProcessResult runProcess(String[] command) throws IOException, InterruptedException;
}
