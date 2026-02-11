package app;
import org.eclipse.jetty.server.Server;

import domain.Storage;

import java.io.IOException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;

import servlets.BuildServlet;
import servlets.RootServlet;
import servlets.RunServlet;

/**
 * Configures and runs the continuous integration HTTP server.
 */
public class ContinuousIntegrationServer { 
    private static Server server;
    private static AppState appState;
    private Storage storage;

    /**
     * Creates a server instance listening on the given port.
     *
     * @param port the port to bind the server to
     */
    public ContinuousIntegrationServer(int port) {
        server = new Server(port);
        appState = new AppState();
    
        ServletContextHandler context = new ServletContextHandler("/");

        try {
            storage = new Storage("build_history.sqlite");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Register servlets
        context.addServlet(RootServlet.class, "/");
        context.addServlet(new RunServlet(appState), "/run");
        context.addServlet(new BuildServlet(storage), "/builds/*");

        server.setHandler(context);
    }
 
    /**
     * Starts the server.
     *
     * @throws Exception if the server fails to start
     */
    public void start() throws Exception {

        server.start();
    }

    /**
     * Waits for the server to stop.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void join() throws InterruptedException {
        server.join();
    }

    /**
     * Stops the server.
     * @throws Exception
     */
    public void stop() throws Exception {
        server.stop();
    }
}
