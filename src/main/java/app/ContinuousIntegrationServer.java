package app;
import org.eclipse.jetty.server.Server;

import domain.Storage;

import java.io.IOException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;

import servlets.BuildServlet;
import servlets.RootServlet;
import servlets.RunServlet;

public class ContinuousIntegrationServer { 
    private static Server server;
    private static AppState appState;
    private Storage storage;

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
