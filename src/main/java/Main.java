public class Main {
    private static final int PORT = 8080 + 18;

    public static void main(String[] args) throws Exception {
        ContinuousIntegrationServer server = new ContinuousIntegrationServer(PORT);
        server.start();
        server.join();
    }
}
