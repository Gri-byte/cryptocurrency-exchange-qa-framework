package com.trading.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

public abstract class APIBaseTest {
    protected static RequestSpecification spec;
    protected static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";
    protected static final int TIMEOUT_SECONDS = 10;

    @BeforeSuite
    public static void setupAPI() {
        spec = new RequestSpecBuilder()
                .setBaseUri(API_BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        RestAssured.requestSpecification = spec;
        System.out.println("API tests configured for: " + API_BASE_URL);
    }
}