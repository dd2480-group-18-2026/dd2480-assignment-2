package github.clients;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@ExtendWith(MockitoExtension.class)
public class GitHubChecksClientTest {

    private static final String BASE_URL = "https://BASE_URL";
    private static final String OWNER = "OWNER";
    private static final String REPO = "REPO";
    private static final String CHECK_NAME = "CHECK_NAME";
    private static final String HEAD_SHA = "HEAD_SHA";
    private static final String ACTUAL_TOKEN = "ACTUAL_TOKEN";
    private static final BigInteger RUN_ID = new BigInteger("839303");
    private static final String BODY = """
            {"id": 8484003983822}
            """;
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String ACCEPT_HEADER_NAME = "Accept";
    private static final String VERSION_HEADER_NAME = "X-GitHub-Api-Version";
    private static final String AUTHORIZATION_HEADER_VALUE = "token " + ACTUAL_TOKEN;
    private static final String ACCEPT_HEADER_VALUE = "application/vnd.github+json";
    private static final String VERSION_HEADER_VALUE = "2022-11-28";
    private static final String CREATE_PAYLOAD = """
            {"head_sha": "HEAD_SHA","name": "CHECK_NAME","status: "in_progress"}
            """;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private GitHubAuth auth;

    private GitHubChecksClient checksClient;
    private MockWebServer server;
    HttpUrl url;

    @BeforeEach
    private void setup() throws IOException {
        
        try {
            when(auth.getInstallationToken()).thenReturn(ACTUAL_TOKEN);
        } catch (Exception e) {
            e.printStackTrace();
        }

        server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(BODY).setResponseCode(201));
        server.start();

        url = server.url("/");
        checksClient = new GitHubChecksClient(auth, url.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void createCheckRun_sendsRequestCorrectly() throws Exception {
        String path = OWNER + "/" + REPO + "/check-runs";
        
        checksClient.createCheckRun(OWNER, REPO, CHECK_NAME, HEAD_SHA);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readUtf8();
        JsonNode jsonBody = mapper.readTree(body);

        assertEquals(url.toString() + path, request.getRequestUrl().toString());
        assertEquals("POST", request.getMethod());
        assertEquals(AUTHORIZATION_HEADER_VALUE, request.getHeader(AUTHORIZATION_HEADER_NAME));
        assertEquals(ACCEPT_HEADER_VALUE, request.getHeader(ACCEPT_HEADER_NAME));
        assertEquals(VERSION_HEADER_VALUE, request.getHeader(VERSION_HEADER_NAME));
        assertEquals(HEAD_SHA, jsonBody.get("head_sha").asText());
        assertEquals(CHECK_NAME, jsonBody.get("name").asText());
        assertEquals("in_progress", jsonBody.get("status").asText());
    }

    @Test
    void createCheckRun_returnsSuccessfulResponseBody() throws Exception {        
        String body = checksClient.createCheckRun(OWNER, REPO, CHECK_NAME, HEAD_SHA);
        
        assertEquals(BODY, body);
    }

    @Test
    void updateCheckRun_sendsRequestCorrectly() throws Exception {
        String path = OWNER + "/" + REPO + "/check-runs/" + RUN_ID;

        checksClient.updateCheckRun(OWNER, REPO, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, RUN_ID);
        RecordedRequest request = server.takeRequest();
        String body = request.getBody().readUtf8();
        JsonNode jsonBody = mapper.readTree(body);

        assertEquals(url.toString() + path, request.getRequestUrl().toString());
        assertEquals("PATCH", request.getMethod());
        assertEquals(AUTHORIZATION_HEADER_VALUE, request.getHeader(AUTHORIZATION_HEADER_NAME));
        assertEquals(ACCEPT_HEADER_VALUE, request.getHeader(ACCEPT_HEADER_NAME));
        assertEquals(VERSION_HEADER_VALUE, request.getHeader(VERSION_HEADER_NAME));
        assertEquals("success", jsonBody.get("conclusion").asText());
        assertEquals("completed", jsonBody.get("status").asText());
    }
    
    @Test
    void updateCheckRun_returnsSuccessfulResponseBody() throws Exception {
        String body = checksClient.updateCheckRun(OWNER, REPO, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, RUN_ID);
        
        assertEquals(BODY, body);
    }
    
    @Test
    void updateCheckRun_throwsExceptionWhenFailedResponse() throws Exception {
        String errorBody = "{\"error\": \"this is error\"";
        server.shutdown();
        server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setResponseCode(404).setBody(errorBody));
        url = server.url("/");
        checksClient = new GitHubChecksClient(auth, url.toString());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {

            checksClient.updateCheckRun(OWNER, REPO, CheckStatus.COMPLETED, CheckConclusion.SUCCESS, RUN_ID);
        });

        assertEquals(errorBody, exception.getMessage());;
    }
}
