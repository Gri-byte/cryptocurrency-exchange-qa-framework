package com.trading.api;

import com.trading.base.APIBaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class TradingAPITests extends APIBaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @Test(description = "Get users successfully", groups = {"smoke", "api"})
    public void testGetUsersAPI() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        System.out.println("TEST PASSED: Get users successful");
    }

    @Test(description = "Get specific user by ID", groups = {"smoke", "api"})
    public void testGetUserById() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getInt("id"), 1, "User ID should be 1");
        System.out.println("TEST PASSED: Get user by ID successful");
    }

    @Test(description = "Create new post", groups = {"api"})
    public void testCreatePost() {
        String requestBody = "{\n" +
                "  \"title\": \"Test Post\",\n" +
                "  \"body\": \"This is a test post\",\n" +
                "  \"userId\": 1\n" +
                "}";

        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .statusCode(201)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 201, "Status code should be 201");
        System.out.println("TEST PASSED: Create post successful");
    }

    @Test(description = "Get posts for user", groups = {"api"})
    public void testGetUserPosts() {
        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0, "Should have posts");
        System.out.println("TEST PASSED: Get user posts successful");
    }

    @Test(description = "Update post", groups = {"api"})
    public void testUpdatePost() {
        String updateBody = "{\n" +
                "  \"id\": 1,\n" +
                "  \"title\": \"Updated Title\",\n" +
                "  \"body\": \"Updated body\",\n" +
                "  \"userId\": 1\n" +
                "}";

        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(updateBody)
                .when()
                .put("/posts/1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("title"), "Updated Title", "Title should be updated");
        System.out.println("TEST PASSED: Update post successful");
    }

    @Test(description = "Delete post", groups = {"api"})
    public void testDeletePost() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .delete("/posts/1")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        System.out.println("TEST PASSED: Delete post successful");
    }

    @Test(description = "Get non-existent user returns 404", groups = {"regression", "api"})
    public void testGetNonExistentUserReturns404() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/users/9999")
                .then()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 404, "Non-existent user should return 404");
        System.out.println("TEST PASSED: Non-existent user returns 404");
    }

    @Test(description = "Get non-existent post returns 404", groups = {"regression", "api"})
    public void testGetNonExistentPostReturns404() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/posts/9999")
                .then()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 404, "Non-existent post should return 404");
        System.out.println("TEST PASSED: Non-existent post returns 404");
    }

    @Test(description = "Get user with invalid string ID returns 404", groups = {"regression", "api"})
    public void testGetUserWithInvalidIdReturns404() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .get("/users/invalid-id")
                .then()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 404, "Invalid user ID should return 404");
        System.out.println("TEST PASSED: Invalid user ID returns 404");
    }

    @Test(description = "Create post without Content-Type header returns error or non-201", groups = {"regression", "api"})
    public void testCreatePostWithoutContentType() {
        String requestBody = "{\"title\": \"No Content-Type\", \"body\": \"test\", \"userId\": 1}";

        Response response = given()
                .baseUri(BASE_URL)
                .body(requestBody)
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();

        Assert.assertNotEquals(response.statusCode(), 500,
                "Server should not return 500 for a missing Content-Type header");
        System.out.println("TEST PASSED: Create post without Content-Type does not cause server error, status: "
                + response.statusCode());
    }

    @Test(description = "Create post with empty body returns a response", groups = {"regression", "api"})
    public void testCreatePostWithEmptyBody() {
        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{}")
                .when()
                .post("/posts")
                .then()
                .extract()
                .response();

        Assert.assertNotEquals(response.statusCode(), 500,
                "Server should not return 500 for an empty post body");
        System.out.println("TEST PASSED: Create post with empty body returned status: " + response.statusCode());
    }

    @Test(description = "Update non-existent post does not return a 2xx success", groups = {"regression", "api"})
    public void testUpdateNonExistentPost() {
        String updateBody = "{\"id\": 9999, \"title\": \"Ghost Post\", \"body\": \"nothing\", \"userId\": 1}";

        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(updateBody)
                .when()
                .put("/posts/9999")
                .then()
                .extract()
                .response();

        int status = response.statusCode();
        Assert.assertTrue(status < 200 || status >= 300,
                "Updating a non-existent post should not return 2xx success, but got: " + status);
        System.out.println("TEST PASSED: Update non-existent post returned non-success status: " + status);
    }

    @Test(description = "Get comments on post", groups = {"api"})
    public void testGetPostComments() {
        Response response = given()
                .baseUri(BASE_URL)
                .queryParam("postId", 1)
                .when()
                .get("/comments")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0, "Should have comments");
        System.out.println("TEST PASSED: Get post comments successful");
    }
}