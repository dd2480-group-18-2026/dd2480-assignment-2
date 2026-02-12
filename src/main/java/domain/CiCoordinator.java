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
        String repoOwner = event.getRepository().getOwner();
        String repoName = event.getRepository().getName();
        String commitSha = event.getHeadCommit().getSha();

        String repsonseBody = client.createCheckRun(repoOwner, repoName, "CI - Compile and Test", commitSha);
        BigInteger runId = getRunId(repsonseBody);
        BuildResult buildResult = runner.runBuild(event.getRepository(), event.getHeadCommit());

        storage.storeBuildResult(buildResult);

        if (buildResult.success) {
            client.updateCheckRun(repoOwner, repoName, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, baseBuildUrl, runId, buildResult.buildOutput);
    
        } else {
            client.updateCheckRun(repoOwner, repoName, CheckStatus.COMPLETED, CheckConclusion.FAILURE, baseBuildUrl, runId, buildResult.buildOutput);
        }
    }
    
    private static BigInteger getRunId(String body) {
        System.out.println(body);
        JsonNode jsonBody = mapper.readTree(body);
        return new BigInteger(jsonBody.get("id").asString());
    }

}
