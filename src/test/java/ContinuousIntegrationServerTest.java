import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.containsString;

/**
 * Unit test for simple App.
 */
public class ContinuousIntegrationServerTest {

    static { RestAssured.baseURI = "http://localhost:8098"; }

    @Test
    void getRoot_returnsOkStatusAndStringMessage() {
        RestAssured.get("/")
                   .then()
                   .statusCode(200)
                   .body(containsString("Hello from root endpoint!"));
    }
}
