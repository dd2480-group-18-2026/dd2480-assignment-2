package domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a GitHub webhook event payload for push events.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubEvent {

    private final Commit headCommit;
    private final Repository repository;

    /**
     * Creates a GitHub event from a webhook payload.
     *
     * @param headCommit the head commit information
     * @param repository the repository information
     */
    @JsonCreator
    public GitHubEvent(
        @JsonProperty(value = "head_commit", required = true) Commit headCommit,
        @JsonProperty(value = "repository", required = true) Repository repository
    ) {
        this.headCommit = headCommit;
        this.repository = repository;
    }

    /**
     * Returns the head commit information.
     *
     * @return the head commit
     */
    public Commit getHeadCommit() {
        return headCommit;
    }

    /**
     * Returns the repository information.
     *
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Commit {
        private final String sha;

        /**
         * Creates a commit representation.
         *
         * @param sha the commit SHA
         */
        @JsonCreator
        public Commit(@JsonProperty(value = "id", required = true) String sha) {
            this.sha = sha;
        }

        /**
         * Returns the commit SHA.
         *
         * @return the commit SHA
         */
        public String getSha() {
            return sha;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private final String url;
        private final String name;
        private final String owner;

        /**
         * Creates a repository representation.
         *
         * @param url the repository clone URL
         * @param fullName The full name of the repository in format <owner>/<name>
         */
        @JsonCreator
        public Repository(
            @JsonProperty(value = "clone_url", required = true) String url,
            @JsonProperty(value = "full_name", required = true) String fullName
        ) {
            this.url = url;

            String[] fullNameSplit = fullName.split("/");
            this.name = fullNameSplit[1];
            this.owner = fullNameSplit[0];
        }

        /**
         * Returns the repository clone URL.
         *
         * @return the repository clone URL
         */
        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }
    }
}
