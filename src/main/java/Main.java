import app.ContinuousIntegrationServer;
import github.clients.GitHubChecksClient;
/**
 *  * Application entry point.
 *
 * <p>Starts the {@link ContinuousIntegrationServer} on the configured port.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8098"));
        long appId = Long.parseLong(System.getenv("APP_ID"));
        long installationId = Long.parseLong(System.getenv("INSTALLATION_ID"));
        String privateKeyPath = System.getenv("PRIVATE_KEY_PATH");
        String gitHubBaseUrl = System.getenv("GITHUB_CHECKS_BASE_URL");
        String baseBuildUrl = System.getenv("BASE_BUILD_URL");

        GitHubChecksClient client = new GitHubChecksClient(appId, installationId, privateKeyPath, gitHubBaseUrl);
        ContinuousIntegrationServer server = new ContinuousIntegrationServer(port, client, baseBuildUrl);
        server.start();
        server.join();
    }
}
