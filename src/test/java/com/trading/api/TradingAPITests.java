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