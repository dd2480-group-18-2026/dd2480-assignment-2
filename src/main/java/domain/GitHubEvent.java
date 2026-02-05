package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEvent {

    private final Commit headCommit;
    private final Repository repository;

    @JsonCreator
    public GitHubEvent(
        @JsonProperty(value = "head_commit", required = true) Commit headCommit,
        @JsonProperty(value = "repository", required = true) Repository repository
    ) {
        this.headCommit = headCommit;
        this.repository = repository;
    }

    public Commit getHeadCommit() {
        return headCommit;
    }

    public Repository getRepository() {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private final String url;

        @JsonCreator
        public Repository(@JsonProperty(value = "clone_url", required = true) String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
