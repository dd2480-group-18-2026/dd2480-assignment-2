package app;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import domain.GitHubEvent;

public class AppState {
    private final BlockingQueue<GitHubEvent> queue;
    
    public AppState() {
        this.queue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<GitHubEvent> getQueue() {
        return this.queue;
    }
}
