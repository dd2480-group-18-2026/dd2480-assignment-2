package domain;

import java.util.Date;

/**
 * Represents the result of a build execution.
 */
public class BuildResult {
    public final String commitSHA;
    public final Date date;
    public final String buildOutput;
    public final boolean success;

    /**
     * Creates a build result.
     *
     * @param commitSHA the commit SHA associated with the build
     * @param date the build date
     * @param buildOutput the build output
     * @param success whether the build succeeded
     */
    public BuildResult(String commitSHA, Date date, String buildOutput, boolean success) {
        this.commitSHA = commitSHA;
        this.date = date;
        this.buildOutput = buildOutput;
        this.success = success;
    }
}
