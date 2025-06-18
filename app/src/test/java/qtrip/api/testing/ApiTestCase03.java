package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApiTestCase03 {

    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";

    private String email;
    private String password;
    private String token;
    private String userId;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        this.email = "booker_" + uuid + "@example.com";
        this.password = "Pass" + uuid;
        RestAssured.baseURI = BASE_URI;
    }

    @Test(description = "E2E Booking Flow - Register, Login, Book, and Validate Reservation")
    public void verifyBookingFlow() {
        SoftAssert softly = new SoftAssert();

        registerUser(softly);
        loginUser(softly);
        Map<String, String> adventure = fetchFirstAdventure("bengaluru", softly);
        bookAdventure(adventure.get("id"), softly);
        verifyBookingInReservations(adventure.get("id"), adventure.get("name"), softly);

        softly.assertAll();
    }

    private void registerUser(SoftAssert softly) {
        JSONObject regBody = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("confirmpassword", password);

        Response regResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(regBody.toString())
                .log().all()
                .post("/api/v1/register");

        softly.assertEquals(regResp.statusCode(), 201, "User registration should return 201");
    }

    private void loginUser(SoftAssert softly) {
        JSONObject loginBody = new JSONObject()
                .put("email", email)
                .put("password", password);

        Response loginResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginBody.toString())
                .log().all()
                .post("/api/v1/login");

        softly.assertEquals(loginResp.statusCode(), 201, "User login should return 201");

        JsonPath jp = loginResp.jsonPath();
        token = jp.getString("data.token");
        userId = jp.getString("data.id");

        softly.assertNotNull(token, "JWT token should not be null");
        softly.assertNotNull(userId, "User ID should not be null");
    }

    private Map<String, String> fetchFirstAdventure(String city, SoftAssert softly) {
        Response resp = RestAssured.given()
                .accept(ContentType.JSON)
                .queryParam("city", city)
                .log().all()
                .get("/api/v1/adventures");

        softly.assertEquals(resp.statusCode(), 200, "Adventure fetch should return 200");

        List<Map<String, Object>> adventures = resp.jsonPath().getList(".");
        softly.assertTrue(adventures.size() > 0, "At least one adventure should be available");

        Map<String, Object> firstAdventure = adventures.get(0);
        return Map.of(
                "id", firstAdventure.get("id").toString(),
                "name", firstAdventure.get("name").toString()
        );
    }

    private void bookAdventure(String adventureId, SoftAssert softly) {
        String bookingDate = LocalDate.now().plusDays(10).toString();

        JSONObject bookingBody = new JSONObject()
                .put("userId", userId)
                .put("name", "Test Booker")
                .put("date", bookingDate)
                .put("person", "1")
                .put("adventure", adventureId);

        Response bookingResp = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(bookingBody.toString())
                .log().all()
                .post("/api/v1/reservations/new");

        softly.assertEquals(bookingResp.statusCode(), 200, "Booking should return 200");
    }

    private void verifyBookingInReservations(String adventureId, String adventureName, SoftAssert softly) {
        Response resp = RestAssured.given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .queryParam("id", userId)
                .log().all()
                .get("/api/v1/reservations");

        softly.assertEquals(resp.statusCode(), 200, "Reservation fetch should return 200");

        List<Map<String, Object>> reservations = resp.jsonPath().getList(".");
        softly.assertTrue(reservations.size() > 0, "Reservation list should not be empty");

        boolean found = reservations.stream()
                .anyMatch(r -> adventureId.equals(r.get("adventure")) && !Boolean.TRUE.equals(r.get("isCancelled")));

        softly.assertTrue(found, "Reservation should be found for adventure: " + adventureName);
    }
}
