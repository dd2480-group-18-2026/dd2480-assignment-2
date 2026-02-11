package github.clients;

/**
 * Provides the allowed conclusion to set on a check.
 */
public enum CheckConclusion {
    SUCCESS("success"),
    FAILURE("failure"),
    NEUTRAL("neutral"),
    CANCELLED("cancelled"),
    TIMED_OUT("timed_out"),
    ACTION_REQUIRED("action_required");

    private final String apiValue;

    private CheckConclusion(String apiValue) {
        this.apiValue = apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}

