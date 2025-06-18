package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.UUID;

/**
 * Test‑case 04 – Duplicate user registration should fail.
 * 
 * Steps:
 * 1. Registers a fresh user (expect 201)
 * 2. Attempts to register with the same email again (expect 400)
 */
public class ApiTestCase04 {

    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";

    private String email;
    private String password;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        email = "dup_" + uuid + "@example.com";
        password = "Pass" + uuid;

        RestAssured.baseURI = BASE_URI;
    }

    @Test(description = "Verify duplicate registration fails with 400 Bad Request")
    public void verifyDuplicateRegistrationFails() {
        SoftAssert softly = new SoftAssert();

        // 🔹 Step 1: First registration (should succeed)
        Response firstResponse = registerUser(email, password);
        softly.assertEquals(firstResponse.statusCode(), 201, "First registration should return 201 Created");

        // 🔹 Step 2: Second registration (same email, should fail)
        Response secondResponse = registerUser(email, password);
        softly.assertEquals(secondResponse.statusCode(), 400, "Second registration (duplicate) should return 400 Bad Request");

        // Optional: Validate error message
        String errorMsg = secondResponse.jsonPath().getString("message");
        if (errorMsg != null) {
            softly.assertTrue(errorMsg.toLowerCase().contains("email"), 
                "Expected error message to mention 'email' – actual: " + errorMsg);
        }

        softly.assertAll();
    }

    private Response registerUser(String email, String password) {
        JSONObject body = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("confirmpassword", password);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body.toString())
                .log().all()
                .post("/api/v1/register");
    }
}
