package github.clients;

/**
 * Provides the allowed statuses to set on a check.
 */
public enum CheckStatus {
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    private final String status;

    private CheckStatus(String status) {
        this.status =  status;
    }

    @Override
    public String toString() {
        return status;
    }
}
