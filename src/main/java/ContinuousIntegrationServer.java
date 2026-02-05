import org.eclipse.jetty.server.Server;

import java.io.IOException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;;

public class ContinuousIntegrationServer { 
    private static Server server;

    public ContinuousIntegrationServer(int port) {
        server = new Server(port);
    
        ServletContextHandler context = new ServletContextHandler("/");

        try {
            var storage = new Storage("build_history.sqlite");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

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
