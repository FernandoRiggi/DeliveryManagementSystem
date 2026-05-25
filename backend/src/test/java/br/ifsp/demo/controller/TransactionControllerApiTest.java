package br.ifsp.demo.controller;

import br.ifsp.demo.security.user.User;
import br.ifsp.demo.support.BaseApiIntegrationTest;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Tag("ApiTest")
@Tag("IntegrationTest")
class TransactionControllerApiTest extends BaseApiIntegrationTest {

    @Test
    @DisplayName("GET /api/v1/hello should return authenticated user id")
    void helloShouldReturnAuthenticatedUserId() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/hello")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body(equalTo("Hello: " + user.getId()));
    }
}
