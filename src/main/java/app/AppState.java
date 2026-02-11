package app;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import domain.GitHubEvent;

/**
 * Holds shared application state.
 */
public class AppState {
    private final BlockingQueue<GitHubEvent> queue;
    
    /**
     * Creates a new application state instance.
     */
    public AppState() {
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Returns the event queue.
     *
     * @return the event queue
     */
    public BlockingQueue<GitHubEvent> getQueue() {
        return this.queue;
    }
}
