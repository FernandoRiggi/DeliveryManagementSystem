package br.ifsp.demo.security.auth;

import br.ifsp.demo.support.BaseApiIntegrationTest;
import br.ifsp.demo.support.EntityBuilder;
import io.restassured.http.ContentType;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
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
}
