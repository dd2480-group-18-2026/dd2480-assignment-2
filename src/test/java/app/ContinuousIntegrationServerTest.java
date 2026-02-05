package app;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeAll;
/**
 * Unit test for simple App.
 */
public class ContinuousIntegrationServerTest {

    private static final int PORT = 8098;
    private static ContinuousIntegrationServer server;

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

    private static String validWebhookBody = """
            {
                "head_commit": {
                    "id": "ABC123"
                },
                "repository": "someUrl"
            }
            """;

    private static String invalidWebhookBody = """
            {
                "head_commit": {
                    "id": "ABC123"
                },
                "repositor": "someUrl"
            }
            """;

    @Test
    void getRoot_returnsOkStatusAndStringMessage() {
        RestAssured.get("/")
                   .then()
                   .statusCode(200)
                   .body(containsString("Hello from root endpoint!"));
    }

    @Test
    void postRun_returnsAcceptedStatusAndStringMessage_whenValidRequest() {
        RestAssured.with()
                   .body(validWebhookBody)
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(202)
                   .body(containsString("Run triggered!"));
    }

    @Test
    void postRun_returnsBadRequestStatus_whenBadJson() {

        RestAssured.with()
                   .body(invalidWebhookBody)
                   .when()
                   .request("POST", "/run")
                   .then()
                   .statusCode(400);
    }
}
