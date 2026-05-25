package br.ifsp.demo.security.auth;

import br.ifsp.demo.support.BaseApiIntegrationTest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.security.user.User;
import io.restassured.http.ContentType;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

@Tag("ApiTest")
@Tag("IntegrationTest")
class UserControllerApiTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("POST /api/v1/register should create user and return 201 with id payload")
    void registerShouldCreateUserAndReturn201WithIdPayload() {
        RegisterUserRequest request = EntityBuilder.createRandomRegisterUserRequest("123password");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/register")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("id", notNullValue())
                .body("id", matchesPattern(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                ));
    }

    @Test
    @DisplayName("POST /api/v1/authenticate should login with valid credentials")
    void authenticateShouldLoginWithValidCredentials() {
        String password = "123password";
        User user = registerUser(password);
        AuthRequest request = new AuthRequest(user.getEmail(), password);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("POST /api/v1/register should return 409 when email is already registered")
    void registerShouldReturn409WhenEmailIsAlreadyRegistered() {
        User existingUser = registerUser("123password");
        RegisterUserRequest request = new RegisterUserRequest(
                existingUser.getName(),
                existingUser.getLastname(),
                existingUser.getEmail(),
                "anotherPassword"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/register")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(409)
                .body("status", equalTo("CONFLICT"))
                .body("message", equalTo("Email already registered: " + existingUser.getEmail()));
    }

    @Test
    @DisplayName("POST /api/v1/authenticate should return 401 with invalid password")
    void authenticateShouldReturn401WithInvalidPassword() {
        User user = registerUser("123password");
        AuthRequest request = new AuthRequest(user.getEmail(), "wrongPassword");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }

    @Test
    @DisplayName("POST /api/v1/authenticate should return 401 when email is not registered")
    void authenticateShouldReturn401WhenEmailIsNotRegistered() {
        RegisterUserRequest randomUser = EntityBuilder.createRandomRegisterUserRequest("123password");
        AuthRequest request = new AuthRequest(randomUser.email(), "123password");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/authenticate")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);
    }
}
