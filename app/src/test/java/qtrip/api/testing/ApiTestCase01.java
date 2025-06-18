package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.UUID;

public class ApiTestCase01{

    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
    private String email;
    private String password;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        this.email = "testuser_" + uuid + "@example.com";
        this.password = "Pass" + uuid;
        RestAssured.baseURI = BASE_URI;
    }

    @Test(description = "Verify that a new user can register and then login via API")
    public void verifyUserRegistrationAndLogin() {
        SoftAssert softly = new SoftAssert();

        // -------- Register User --------
        JSONObject registerBody = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("confirmpassword", password);

        Response registerResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerBody.toString())
                .log().all()
                .post("/api/v1/register");

        softly.assertEquals(registerResp.statusCode(), 201, "Register: expected HTTP 201");

        // -------- Login User --------
        JSONObject loginBody = new JSONObject()
                .put("email", email)
                .put("password", password);

        Response loginResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginBody.toString())
                .log().all()
                .post("/api/v1/login");

        softly.assertEquals(loginResp.statusCode(), 201, "Login: expected HTTP 201");

        // -------- Validate Response --------
        JsonPath jp = loginResp.jsonPath();
        softly.assertTrue(jp.getBoolean("success"), "Login success flag should be true");

        String token = jp.getString("data.token");
        String userId = jp.getString("data.id");

        softly.assertNotNull(token, "JWT token must be present");
        softly.assertFalse(token.trim().isEmpty(), "JWT token must not be empty");

        softly.assertNotNull(userId, "User ID must be present");
        softly.assertFalse(userId.trim().isEmpty(), "User ID must not be empty");

        // -------- Complete --------
        softly.assertAll();
    }
}
