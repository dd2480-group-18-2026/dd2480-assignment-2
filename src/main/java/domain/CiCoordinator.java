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
    private final String baseBuildUrl;

    public CiCoordinator(
        BlockingQueue<GitHubEvent> gitHubEvents, 
        Storage storage, 
        GitHubChecksClient client, 
        String baseBuildUrl
    ) {
        this.gitHubEvents = gitHubEvents;
        this.storage = storage;
        this.client = client;
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
        String repoUrl = event.getRepository().getUrl();
        String commitSha = event.getHeadCommit().getSha();
        System.out.println("HERE 1");
        String repsonseBody = client.createCheckRun(repoOwner, repoName, "CI - Compile and Test", commitSha);
        System.out.println("HERE");
        BigInteger runId = getRunId(repsonseBody);
        System.out.println("HERE 2");
        //result = compile
        BuildResult buildResult = new BuildResult(commitSha, null, null, true);

        storage.storeBuildResult(buildResult);

        if (buildResult.success) {
            //update test checkRun success
            client.updateCheckRun(repoOwner, repoName, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, baseBuildUrl, runId);
    
        } else {
            //update test checkRun failed
            client.updateCheckRun(repoOwner, repoUrl, CheckStatus.COMPLETED, CheckConclusion.FAILURE, baseBuildUrl, runId);
        }
    }
    
    private static BigInteger getRunId(String body) {
        System.out.println(body);
        JsonNode jsonBody = mapper.readTree(body);
        return new BigInteger(jsonBody.get("id").asString());
    }

}
