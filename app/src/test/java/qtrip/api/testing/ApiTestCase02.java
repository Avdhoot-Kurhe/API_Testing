package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Map;

public class ApiTestCase02 {

    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
    private static final String QUERY = "beng";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test(description = "Verify that searching cities with query 'beng' returns exactly one Bengaluru result having '100+ Places' in description")
    public void verifyCitySearchResults() {
        SoftAssert softly = new SoftAssert();

        Response resp = RestAssured
                .given()
                .accept(ContentType.JSON)
                .queryParam("q", QUERY)
                .log().uri()
                .log().method()
                .log().headers()
                .get("/api/v1/cities");

        // ✅ 1. Status code check
        softly.assertEquals(resp.statusCode(), 200, "Expected HTTP 200 for search query");

        // ✅ 2. Parse array from response
        List<Map<String, Object>> cities = resp.jsonPath().getList(".");

        softly.assertEquals(cities.size(), 1, "Expected exactly one city for query 'beng'");

        // ✅ 3. Validate description
        if (!cities.isEmpty()) {
            Object descObj = cities.get(0).get("description");
            String description = descObj != null ? descObj.toString() : "";
            softly.assertTrue(description.contains("100+ Places"), 
                "Expected city description to contain '100+ Places'");
        }

        // ✅ 4. Final assertion
        softly.assertAll();
    }
}
