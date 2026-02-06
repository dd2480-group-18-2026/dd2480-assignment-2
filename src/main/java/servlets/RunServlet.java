package servlets;

import java.io.IOException;
import java.util.Enumeration;

import app.AppState;
import domain.GitHubEvent;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class RunServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private AppState appState;

    public RunServlet(AppState appState) {
        this.appState = appState;
    }

    @Override
     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GitHubEvent event;
        
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
