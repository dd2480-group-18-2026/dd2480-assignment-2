package servlets;

import java.io.IOException;

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
}
