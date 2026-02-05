package domain;
public class BuildResult {
    public final String commitSHA;
    public final String buildOutput;

    BuildResult(String commitSHA, String buildOutput) {
        this.commitSHA = commitSHA;
        this.buildOutput = buildOutput;
    }
}
