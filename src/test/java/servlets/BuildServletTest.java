package servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import domain.BuildResult;
import domain.Storage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class BuildServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StringWriter responseBody;
    private Storage storage;
    private ObjectMapper mapper = new ObjectMapper(); // To read JSON responses

    @BeforeEach
    void setup() throws Exception {
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void doGet_returnsCorrectBuildNumbers() throws Exception {
        var file = File.createTempFile("build-servlet-test1", "sqlite");
        file.deleteOnExit();

        storage = new Storage(file.getAbsolutePath());

        int BUILD_NB = 5;

        for (int i = 0; i < BUILD_NB; i++) {
            storage.storeBuildResult(
                new BuildResult("commit " + i, new Date(), "build output", true)
            );
        }

        BuildServlet servlet = new BuildServlet(storage);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");

        String json = responseBody.toString();

        assertNotNull(json);
        assertTrue(json.contains("\"builds\""));

        assertEquals(BUILD_NB, countOccurrences(json, "/builds/")); // We count the number of URLs
    }

    @Test
    void doGet_buildById_returnsCorrectBuild_whenIdIsCorrect() throws Exception {
        var file = File.createTempFile("build-servlet-test2", "sqlite");
        file.deleteOnExit();

        storage = new Storage(file.getAbsolutePath());

        BuildResult br = new BuildResult("abc123", new Date(), "Build output here", true);
        storage.storeBuildResult(br);

        // We request the first and only build
        when(request.getPathInfo()).thenReturn("/" + 1);

        BuildServlet servlet = new BuildServlet(storage);

        servlet.doGet(request, response);

        // Assert HTTP status and content type
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json");

        // Parse response JSON
        JsonNode root = mapper.readTree(responseBody.toString());

        assertNotNull(root);
        assertEquals(br.commitSHA, root.get("commitSHA").asString());
        assertEquals(br.buildOutput, root.get("buildOutput").asString());
        assertEquals(br.success, root.get("success").asBoolean());

        // Verify date equality through the JSON conversion
        Instant originalInstant = br.date.toInstant();
        Instant returnedInstant = Instant.parse(root.get("date").asString());
        assertEquals(originalInstant, returnedInstant);
    }

    void doGet_buildById_returns404_whenIdIsIncorrect() throws Exception {
        var file = File.createTempFile("build-servlet-test3", "sqlite");
        file.deleteOnExit();

        storage = new Storage(file.getAbsolutePath());

        BuildResult br = new BuildResult("abc123", new Date(), "Build output here", true);
        storage.storeBuildResult(br);

        // We request a wrong build number
        when(request.getPathInfo()).thenReturn("/" + 2);

        BuildServlet servlet = new BuildServlet(storage);

        servlet.doGet(request, response);

        // Assert HTTP status and content type
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response).setContentType("application/json");
    }

    private int countOccurrences(String text, String substring) {
        return text.split(substring, -1).length - 1;
    }
}
