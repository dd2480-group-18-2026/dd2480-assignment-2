package servlets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.AppState;
import domain.GitHubEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class RunServletTest {
    @Mock
    private AppState appState;
    @Mock
    private static HttpServletRequest request;
    @Mock
    private static HttpServletResponse response;

    private RunServlet servlet;
    private static String validRequestBody = """
        {
            "head_commit": {
                "id": "ABC123"
            },
            "repository": "someUrl"
        }
    """;

    private static String invalidRequestBody = """
        {
            "head_commit": {
                "id": "ABC123"
            },
            "repositor": "someUrl"
        }
    """;

    private static void setRequestBody(String body) throws IOException {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
    }


    @BeforeEach
    void setup() throws IOException {
        servlet = new RunServlet(appState);
        when(appState.getQueue()).thenReturn(new LinkedBlockingQueue<>());
    }

    @Test
    void doPost_addsEventToQueue_whenValidRequest() throws IOException {
        setRequestBody(validRequestBody);
        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.doPost(request, response);

        assertEquals(1, appState.getQueue().size());
        GitHubEvent event = appState.getQueue().poll();
        assertEquals("someUrl", event.getRepository());
        assertEquals("ABC123", event.getHeadCommit().getSha());
    }

    @Test
    void doPost_doesNotaddEventToQueue_whenInvalidRequest() throws IOException {
        setRequestBody(invalidRequestBody);

        servlet.doPost(request, response);

        assertEquals(0, appState.getQueue().size());
    }
}
