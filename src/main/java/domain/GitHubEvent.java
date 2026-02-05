package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEvent {

    private final Commit headCommit;
    private final String repository;

    @JsonCreator
    public GitHubEvent(
        @JsonProperty(value = "head_commit", required = true) Commit headCommit,
        @JsonProperty(value = "repository", required = true) String repository
    ) {
        this.headCommit = headCommit;
        this.repository = repository;
    }

    public Commit getHeadCommit() {
        return headCommit;
    }

    public String getRepository() {
        return repository;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private final String sha;

        @JsonCreator
        public Commit(@JsonProperty(value = "id", required = true) String sha) {
            this.sha = sha;
        }

        public String getSha() {
            return sha;
        }
    }
}
