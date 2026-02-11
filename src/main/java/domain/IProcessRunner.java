package domain;

import java.io.IOException;

public interface IProcessRunner {
	public ProcessResult runProcess(String[] command) throws IOException, InterruptedException;
}
