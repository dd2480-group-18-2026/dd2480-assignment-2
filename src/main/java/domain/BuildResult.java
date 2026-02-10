package domain;

import java.util.Date;

public class BuildResult {
    public final String commitSHA;
    public final Date date;
    public final String buildOutput;
    public final boolean success;

    public BuildResult(String commitSHA, Date date, String buildOutput, boolean success) {
        this.commitSHA = commitSHA;
        this.date = date;
        this.buildOutput = buildOutput;
        this.success = success;
    }
}
