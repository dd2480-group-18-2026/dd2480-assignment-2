package app;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeAll;
/**
 * Integration tests for {@link ContinuousIntegrationServer}.
 *
 * <p>Verifies HTTP responses for root and webhook endpoints.
 */
public class ContinuousIntegrationServerTest {

    private static final int PORT = 8098;
    private static final String WEBHOOK_HEADER_NAME = "X-GitHub-Event";
    private static ContinuousIntegrationServer server;
    private static final String validWebhookBody = """
            {
                "head_commit": {
                    "id": "ABC123"
                },
                "repository": {
                    "clone_url": "someUrl",
                    "full_name": "owner/repoName"
                }
            }
            """;
    private static final String invalidWebhookBody = """
            {
                "head_commit": {
                    "id": "ABC123"
                },
                "repositor": {
                    "clone_url": "someUrl",
                    "full_name": "owner/repoName"
                }
            }
            """;

    @BeforeAll
    private static void setup() throws Exception {
        server = new ContinuousIntegrationServer(PORT);
        server.start();
    }

    @AfterAll
    private static void shutdown() throws Exception {
        server.stop();
    }

    static { RestAssured.baseURI = "http://localhost:" + PORT; }


    @Test
    void getRoot_returnsOkStatusAndStringMessage() {
        RestAssured.get("/")
                   .then()
                   .statusCode(200)
                   .body(containsString("Hello from root endpoint!"));
    }

    @Test
    void postRun_returnsAcceptedStatusAndStringMessage_whenValidPushEventRequest() {
        RestAssured.with()
                   .header(WEBHOOK_HEADER_NAME, "push")
                   .body(validWebhookBody)
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(202)
                   .body(containsString("Run triggered!"));
    }

    @Test
    void postRun_returnsBadRequestStatus_whenBadJsonPushEvent() {

        RestAssured.with()
                   .header(WEBHOOK_HEADER_NAME, "push")
                   .body(invalidWebhookBody)
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(400);
    }

    @Test
    void postRun_returnsOkStatus_whenPingEventRequest() {
        RestAssured.with().header(WEBHOOK_HEADER_NAME, "ping")
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(200);
    }

    @Test
    void postRun_returnsNotImplementedStatus_whenNotPingOrPushEvent() {
        RestAssured.with().header(WEBHOOK_HEADER_NAME, "merge")
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(501);
    }
}
