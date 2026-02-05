import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;;

public class ContinuousIntegrationServer { 
    private static Server server;

    public ContinuousIntegrationServer(int port) {
        server = new Server(port);
    
        ServletContextHandler context = new ServletContextHandler("/");

        var storage = new Storage("build_history.sqlite");

        // Register servlets
        context.addServlet(RootServlet.class, "/");
    
        server.setHandler(context);
    }
 
    public void start() throws Exception {

        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
