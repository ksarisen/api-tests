package com.example;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTest extends BaseApiTest {

    @Test
    void getPosts_shouldReturn200_andListNotEmpty() {

        given()
                .when()
                .get("/posts")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void createPostS_shouldReturn201_andContainId() {

        String body =
                """
                {
                    "title": "kerem test",
                    "body": "hello world",
                    "userId":   1
                }
                """;

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                    .post("/posts")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void createPost_ReadId() {

        String body =
                """
                { "title": "test", "body": demo", "userId": 1 }
                """;

        int id =
                given()
                        .spec(requestSpec)
                        .body(body)
                        .when()
                        .post("/posts")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        System.out.println("Created id = " + id);
    }

    @Test
    void creatPost_withoutBody_shouldFail() {
        given()
                .spec(requestSpec)
                .when()
                .post("/posts")
                .then()
                .statusCode(anyOf(is(400), is(415), is(500)));
    }

    @Test
    void getSinglePost_shouldReturnCorrectUser() {
        given()
                .spec(requestSpec)
                .when()
                .get("/posts/1")
                .then()
                .statusCode(200)
                .body("id", is((1)))
                .body("userId", is(1))
                .body("title", notNullValue());
    }

    @Test
    void fakeLogin_getToken() {
        String body = """
        {
          "email": "eve.holt@reqres.in",
          "password": "cityslicka"
        }
        """;

        String token =
                given()
                        .spec(requestSpec)
                        .body(body)
                        .when()
                        .post("/api/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");

        System.out.println("TOKEN = " + token);
    }

    @Test
    void getUsers_withToken() {
        String token =
                "exampleToken"; // normalde login testinden alınır

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/users?page=1")
                .then()
                .statusCode(200);
    }

    @Test
    void crudFlow() {
        // 1) CREATE
        int id =
                given()
                        .spec(requestSpec)
                        .body("""
                            { "title": "flow test", "body": "demo", "userId": 1 }
                         """)
                        .when()
                        .post("/posts")
                        .then()
                        .statusCode(201)
                        .extract().path("id");
        // 2) READ
        given()
                .spec(requestSpec)
                .when()
                .get("/posts/" + id)
                .then()
                .statusCode(anyOf(is(200), is(404)));

        // 3) DELETE
        given()
                .spec(requestSpec)
                .when().delete("/posts/" + id)
                .then().statusCode(anyOf(is(200), is(204)));

        // 4) DELETE
        given()
                .spec(requestSpec)
                .when().get("/posts/" + id)
                .then().statusCode(anyOf(is(404), is(200)));
    }

}


