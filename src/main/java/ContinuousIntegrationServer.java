import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;
/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends Handler.Abstract.NonBlocking
{   
    @Override
    public boolean handle(Request request, Response response, Callback callback)    {
        response.setStatus(200);
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/html; charset=UTF-8");

        // Write a Hello World response.
        Content.Sink.write(response, true, """
            <!DOCTYPE html>
            <html>
            <head>
              <title>Jetty Hello World Handler</title>
            </head>
            <body>
              <p>Hello World</p>
            </body>
            </html>
            """, callback);
        return true;
    }
 
    public static void main(String[] args) throws Exception {
        
        // used to start the CI server in command line
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080 + 18);
        server.addConnector(connector);
    
        // Set the Hello World Handler.
        server.setHandler(new ContinuousIntegrationServer());
    
        server.start();
    }
}