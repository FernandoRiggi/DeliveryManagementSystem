package br.ifsp.demo.controller;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.infrastructure.persistence.repository.OrderDeliveryJpaRepository;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.support.BaseApiIntegrationTest;
import br.ifsp.demo.support.EntityBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

@Tag("ApiTest")
@Tag("IntegrationTest")
class OrderDeliveryControllerApiTest extends BaseApiIntegrationTest {

    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private OrderDeliveryJpaRepository orderRepository;

    private final List<UUID> createdOrderIds = new ArrayList<>();

    @AfterEach
    void cleanOrders() {
        createdOrderIds.forEach(orderRepository::deleteById);
        createdOrderIds.clear();
    }

    @Test
    @DisplayName("POST /api/v1/orders should create order and return 201 with order id payload")
    void createOrderShouldReturn201WithOrderIdPayload() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 12.5);

        String orderId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(201)
                .body("orderId", notNullValue())
                .body("orderId", matchesPattern(
                        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
                ))
                .extract()
                .path("orderId");

        createdOrderIds.add(UUID.fromString(orderId));
    }

    @Test
    @DisplayName("POST /api/v1/orders should return 400 when customer does not exist")
    void createOrderShouldReturn400WhenCustomerDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(UUID.randomUUID(), 12.5);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("message", equalTo("Customer not found"));
    }
}
