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

    @Test
    void getRoot_returnsOkStatusAndStringMessage() {
        RestAssured.get("/")
                   .then()
                   .statusCode(200)
                   .body(containsString("Hello from root endpoint!"));
    }   
}
