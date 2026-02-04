import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;;

public class ContinuousIntegrationServer {
    private static final int PORT = 8080 + 18;
    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        ServletContextHandler context = new ServletContextHandler("/");

        // Register servlets
        context.addServlet(RootServlet.class, "/");

        server.setHandler(context);

        server.start();
        server.join();
    }
}
