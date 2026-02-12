package servlets;

import java.io.IOException;
import java.util.Enumeration;

import app.AppState;
import domain.GitHubEvent;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Servlet handling GitHub webhook requests for triggering CI runs.
 */
public class RunServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private AppState appState;

    public RunServlet(AppState appState) {
        this.appState = appState;
    }

    /**
     * Handles GitHub webhook POST requests.
     *
     * <p>Accepts {@code push} events, ignores {@code ping} events, and rejects
     * unsupported event types. Valid push payloads are added to the application
     * event queue.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs while reading the request
     */
    @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GitHubEvent event;

        System.out.println(request);
        
        String eventType = getEventType(request);
        if (eventType.equals("ping")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        } else if (!eventType.equals("push")) {
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return;
        }

        try {
            event = mapper.readValue(request.getReader(), GitHubEvent.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println(e.toString());
            return;
        }

        appState.getQueue().add(event);
        
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.getWriter().println("Run triggered!");
    }

    private String getEventType(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders("X-GitHub-Event");
        String eventType = "";
        if (headers.hasMoreElements()) {
            eventType = headers.nextElement();
            System.out.println("GitHub Event: " + eventType);
        }
        return eventType;
    }
}
