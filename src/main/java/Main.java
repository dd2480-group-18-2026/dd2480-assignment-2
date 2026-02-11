import app.ContinuousIntegrationServer;
/**
 *  * Application entry point.
 *
 * <p>Starts the {@link ContinuousIntegrationServer} on the configured port.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8098"));
        ContinuousIntegrationServer server = new ContinuousIntegrationServer(port);
        server.start();
        server.join();
    }
}
