package servlets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
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
/**
 * Unit tests for {@link RunServlet}.
 *
 * <p>Verifies that valid {@code push} webhook requests are enqueued and that invalid payloads or
 * non-{@code push} events are ignored.
 */
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
                "repository": {
                    "clone_url": "someUrl"
                }
            }
            """;
    private static String invalidRequestBody = """
            {
                "head_commit": {
                    "id": "ABC123"
                },
                "repositor": {
                    "clone_url": "someUrl"
                }
            }
            """;

    private static void setRequestBody(String body) throws IOException {
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(body)));
    }

    private static void setGitHubEventHeader(String event) throws IOException {        
        Enumeration<String> strEnumeration = new Enumeration<String>() {

            @Override
            public boolean hasMoreElements() {
                return true;
            }

            @Override
            public String nextElement() {
                return event;
            }
            
        };
        when(request.getHeaders("X-GitHub-Event")).thenReturn(strEnumeration);
    }


    @BeforeEach
    void setup() throws IOException {
        servlet = new RunServlet(appState);
        when(appState.getQueue()).thenReturn(new LinkedBlockingQueue<>());
    }

    @Test
    void doPost_addsEventToQueue_whenValidRequest() throws IOException {
        setRequestBody(validRequestBody);
        setGitHubEventHeader("push");
        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.doPost(request, response);

        assertEquals(1, appState.getQueue().size());
        GitHubEvent event = appState.getQueue().peek();
        assertEquals("someUrl", event.getRepository().getUrl());
        assertEquals("ABC123", event.getHeadCommit().getSha());
    }

    @Test
    void doPost_doesNotaddEventToQueue_whenInvalidRequestForPushEvent() throws IOException {
        setRequestBody(invalidRequestBody);
        setGitHubEventHeader("push");

        servlet.doPost(request, response);

        assertEquals(0, appState.getQueue().size());
    }
    
    @Test
    void doPost_doesNotaddEventToQueue_whenPingEvent() throws IOException {
        setGitHubEventHeader("ping");

        servlet.doPost(request, response);

        assertEquals(0, appState.getQueue().size());
    }

    @Test
    void doPost_doesNotaddEventToQueue_whenNotPingOrPushEvent() throws IOException {
        setGitHubEventHeader("merge");

        servlet.doPost(request, response);

        assertEquals(0, appState.getQueue().size());
    }
}
