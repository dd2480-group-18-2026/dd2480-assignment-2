package domain;

/**
 * Simple class used to return the outcome of a process
 */
public class ProcessResult {
		public final String output;
		public final boolean success;

		/**
		 * 
		 * @param output The output obtained when running the process
		 * @param success Whether the process succeeded or not
		 */
		public ProcessResult(String output, boolean success) {
			this.output = output;
			this.success = success;
		}
};
