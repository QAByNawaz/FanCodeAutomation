package FC.FanCodeTest;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import FC.Utility.UrlBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;

public class FanCodeTodosTest {

    /**
     * Test to verify todos completion status for FanCode users.
     * Depends on the successful identification of FanCode users.
     * Priority ensures this test runs early in the test suite.
     */
    @Test(dependsOnGroups = "testFanCodeUsersIdentification", priority = 1)
    public void testTodosForFanCodeUsers() {
        IdentifyFanCodeUsersTest identifyTest = new IdentifyFanCodeUsersTest();
        
        // Try setting up FanCode users, fail test if setup fails
        try {
            identifyTest.setup();
        } catch (Exception e) {
            Assert.fail("Failed to set up FanCode users: " + e.getMessage());
        }

        List<Integer> fanCodeUserIds = identifyTest.fanCodeUserIds;

        String todosUrl = UrlBuilder.buildTodosUrl();

        Response todosResponse;
        try {
            // Retrieve todos from API
            todosResponse = given()
                    .log().all()
                    .when()
                    .get(todosUrl)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().response();
        } catch (Exception e) {
            Assert.fail("Failed to retrieve todos: " + e.getMessage());
            return; // Stop execution if todos retrieval fails
        }

        JsonPath jsonPath = todosResponse.jsonPath();
        List<Map<String, Object>> todos = jsonPath.getList("");

        // Maps to store completed and uncompleted todos counts for each user
        Map<Integer, Integer> completedTodos = new HashMap<>();
        Map<Integer, Integer> uncompletedTodos = new HashMap<>();

        // Initialize counts for each user
        for (Integer userId : fanCodeUserIds) {
            completedTodos.put(userId, 0);
            uncompletedTodos.put(userId, 0);
        }

        // Count completed and uncompleted todos for each user
        for (Map<String, Object> todo : todos) {
            boolean completed = (boolean) todo.get("completed");
            int userId = (int) todo.get("userId");

            if (fanCodeUserIds.contains(userId)) {
                if (completed) {
                    completedTodos.put(userId, completedTodos.get(userId) + 1);
                } else {
                    uncompletedTodos.put(userId, uncompletedTodos.get(userId) + 1);
                }
            }
        }

        // Lists to store user IDs based on completion percentage
        List<Integer> userIdsCompletedMoreThan50Percent = new ArrayList<>();
        List<Integer> userIdsNotCompletedMoreThan50Percent = new ArrayList<>();

        // Populate lists based on completion percentage
        for (Integer userId : fanCodeUserIds) {
            int totalTodos = completedTodos.get(userId) + uncompletedTodos.get(userId);
            double completedPercentage = (double) completedTodos.get(userId) / totalTodos * 100;

            System.out.println("User ID: " + userId);
            System.out.println("Total Todos: " + totalTodos);
            System.out.println("Completed Todos: " + completedTodos.get(userId) + " (" + completedPercentage + "%)");
            System.out.println("Uncompleted Todos: " + uncompletedTodos.get(userId) + " (" + (100 - completedPercentage) + "%)");
            System.out.println();

            // Determine which list to add the user ID based on completion percentage
            if (completedPercentage > 50) {
                userIdsCompletedMoreThan50Percent.add(userId);
            } else {
                userIdsNotCompletedMoreThan50Percent.add(userId);
            }
        }

        // Print lists of users who completed more than 50% and those who did not
        System.out.println("Users who have completed more than 50%:");
        if (userIdsCompletedMoreThan50Percent.isEmpty()) {
            System.out.println("No users have completed more than 50% of their todos.");
        } else {
            for (Integer userId : userIdsCompletedMoreThan50Percent) {
                System.out.println(userId);
            }
        }
        System.out.println();

        System.out.println("Users who have not completed more than 50%:");
        if (userIdsNotCompletedMoreThan50Percent.isEmpty()) {
            System.out.println("All users have completed more than 50% of their todos.");
        } else {
            for (Integer userId : userIdsNotCompletedMoreThan50Percent) {
                System.out.println(userId);
            }
        }
        System.out.println();

        // Assert for each user individually if needed
        for (Integer userId : userIdsCompletedMoreThan50Percent) {
            assertTrue(completedTodos.get(userId) > 0, "User ID " + userId + " did not complete any todos.");
        }
    }
}
