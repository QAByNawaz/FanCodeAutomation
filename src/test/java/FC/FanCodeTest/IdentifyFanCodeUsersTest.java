package FC.FanCodeTest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import FC.Utility.UrlBuilder;
import FC.Utility.fancodeutility.GeoConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class IdentifyFanCodeUsersTest {

    private List<Map<String, Object>> fanCodeUsers;
    public List<Integer> fanCodeUserIds; // Changed to public

    @Test(groups = "testFanCodeUsersIdentification")
    public void setup() {
        try {
            // Perform GET request and validate status code
            Response response = given()
                    .log().all() // Log all details of the request
                    .when()
                    .get(UrlBuilder.buildUserUrl()) // Construct URL dynamically
                    .then()
                    .log().all() // Log all details of the response
                    .statusCode(200) // Ensure status code is 200
                    .extract().response(); // Extract the response object

            // Initialize lists to store FanCode city users and their IDs
            fanCodeUsers = new ArrayList<>();
            fanCodeUserIds = new ArrayList<>();

            // Parse the JSON response using JsonPath
            JsonPath jsonPath = response.jsonPath();

            // Retrieve all users from JSON response
            List<Map<String, Object>> allUsers = jsonPath.getList("");

            // Iterate through users and filter based on city criteria
            for (Map<String, Object> user : allUsers) {
                Map<String, Object> address = (Map<String, Object>) user.get("address");
                Map<String, Object> geo = (Map<String, Object>) address.get("geo");

                double latitude = Double.parseDouble(geo.get("lat").toString());
                double longitude = Double.parseDouble(geo.get("lng").toString());

                // Check if user falls within FanCode city coordinates
                if (latitude >= GeoConstants.MIN_LATITUDE && latitude <= GeoConstants.MAX_LATITUDE &&
                        longitude >= GeoConstants.MIN_LONGITUDE && longitude <= GeoConstants.MAX_LONGITUDE) {
                    // Add user to list
                    fanCodeUsers.add(user);
                    fanCodeUserIds.add((int) user.get("id")); // Add user ID to the list
                }
            }

            // Log the number of FanCode city users and their IDs
            System.out.println("Number of FanCode city users: " + fanCodeUsers.size());
            System.out.println("FanCode city user IDs: " + fanCodeUserIds);

            // Fail the setup if no FanCode city users are found
            Assert.assertTrue(fanCodeUsers.size() > 0, "No FanCode city users found");

        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
            Assert.fail("Failed to identify FanCode city users: " + e.getMessage());
        }
    }
}
