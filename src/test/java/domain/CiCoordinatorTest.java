package domain;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import github.clients.CheckConclusion;
import github.clients.CheckStatus;
import github.clients.GitHubChecksClient;

@ExtendWith(MockitoExtension.class)
public class CiCoordinatorTest {
    private static final String BASE_BUILD_URL = "url";
    private static final GitHubEvent event = new GitHubEvent(
        new GitHubEvent.Commit("ABC123"), 
        new GitHubEvent.Repository("repoUrl", "owner/name")
    );

    @Mock
    private Storage storage;
    @Mock
    private GitHubChecksClient client;

    private Thread thread;
    private BlockingQueue<GitHubEvent> queue;
    private CiCoordinator worker;

    @BeforeEach
    void setup() {
        queue = new LinkedBlockingQueue<>();
        worker = new CiCoordinator(queue, storage, client, BASE_BUILD_URL);

        thread = new Thread(worker);
        thread.start();
    }

    /**
     * Checks that run results in
     * - taking event from queue
     * - creating check
     * - building
     * - completing check with success
     * - storing successful build result
     * @throws Exception
     */
    @Test
    void run_handlesCorrectly_whenSuccessfulBuild() throws Exception {
        when(client.createCheckRun(any(), any(), any(), any())).thenReturn("{\"id\": 1}");
        queue.offer(event);

        // Wait until handle() has executed
        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(client).createCheckRun("owner", "name", "CI - Compile and Test", "ABC123");
                    verify(storage).storeBuildResult(any()); 
                    verify(client).updateCheckRun("owner", "name", CheckStatus.COMPLETED, CheckConclusion.SUCCESS, BASE_BUILD_URL, new BigInteger("1"));
                });

        thread.interrupt();
        thread.join(1000);

        assertTrue(queue.isEmpty());
    }

    /**
     * Checks that run results in
     * - taking event from queue
     * - creating check
     * - building
     * - completing check with failure
     * - storing failed build result
     * @throws Exception
     */
    @Test
    void run_handlesCorrectly_whenFailedBuild() throws Exception {
        when(client.createCheckRun(any(), any(), any(), any())).thenReturn("{\"id\": 1}");
        queue.offer(event);

        // Wait until handle() has executed
        Awaitility.await()
                .atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(client).createCheckRun("owner", "name", "CI - Compile and Test", "ABC123");
                    verify(storage).storeBuildResult(any()); 
                    verify(client).updateCheckRun("owner", "name", CheckStatus.COMPLETED, CheckConclusion.SUCCESS, BASE_BUILD_URL, new BigInteger("1"));
                });

        thread.interrupt();
        thread.join(1000);

        assertTrue(queue.isEmpty());
    }

}
