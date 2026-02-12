package domain;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

import github.clients.CheckConclusion;
import github.clients.CheckStatus;
import github.clients.GitHubChecksClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class CiCoordinator implements Runnable {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final BlockingQueue<GitHubEvent> gitHubEvents;
    private final Storage storage;
    private final GitHubChecksClient client;
    private final CiRunner runner;
    private final String baseBuildUrl;

    /**
     * 
     * @param gitHubEvents
     * @param storage
     * @param client
     * @param baseBuildUrl
     */
    public CiCoordinator(
        BlockingQueue<GitHubEvent> gitHubEvents, 
        Storage storage, 
        GitHubChecksClient client,
        CiRunner runner,
        String baseBuildUrl
    ) {
        this.gitHubEvents = gitHubEvents;
        this.storage = storage;
        this.client = client;
        this.runner = runner;
        this.baseBuildUrl = baseBuildUrl;
    }
    
    /**
     * This causes the coordinator to continually check for webhook events from GitHub
     * and handle them by building, storing build result and creating a check on
     * the commit on GitHub.
     */
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            GitHubEvent event;
            try {
                event = gitHubEvents.take();
                handle(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private void handle(GitHubEvent event) throws Exception {
        System.out.println("Coordinator handling event for sha=" + event.getHeadCommit().getSha());
        String repoOwner = event.getRepository().getOwner();
        String repoName = event.getRepository().getName();
        String commitSha = event.getHeadCommit().getSha();
        System.out.println("1) Creating check run...");
        String responseBody = client.createCheckRun(repoOwner, repoName, "CI - Compile and Test", commitSha);
        BigInteger runId = getRunId(responseBody);
        System.out.println("2) Running build...");
        BuildResult buildResult = runner.runBuild(event.getRepository(), event.getHeadCommit());
        System.out.println("3) Storing build result...");
        storage.storeBuildResult(buildResult);

        if (buildResult.success) {
            System.out.println("4) Updating check run...");
            client.updateCheckRun(repoOwner, repoName, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, baseBuildUrl, runId, buildResult.buildOutput);
    
        } else {
            System.out.println("4) Updating check run...");
            client.updateCheckRun(repoOwner, repoName, CheckStatus.COMPLETED, CheckConclusion.FAILURE, baseBuildUrl, runId, buildResult.buildOutput);
        }
    }
    
    private static BigInteger getRunId(String body) {
        System.out.println(body);
        JsonNode jsonBody = mapper.readTree(body);
        return new BigInteger(jsonBody.get("id").asString());
    }

}
