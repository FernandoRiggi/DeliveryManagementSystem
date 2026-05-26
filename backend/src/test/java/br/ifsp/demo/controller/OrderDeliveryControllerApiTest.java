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

    @Test
    @DisplayName("GET /api/v1/orders/{orderId} should return order payload when order exists")
    void getOrderByIdShouldReturnOrderPayloadWhenOrderExists() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        double distanceKm = 7.5;
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, distanceKm);
        String orderId = createOrder(token, request);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("CREATED"))
                .body("customer.customerId", equalTo(SEEDED_CUSTOMER_ID.toString()))
                .body("distanceKm", equalTo((float) distanceKm))
                .body("pickupAddress.cep", equalTo(request.pickupCep()))
                .body("deliveryAddress.cep", equalTo(request.deliveryCep()));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderId} should return 404 when order does not exist")
    void getOrderByIdShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", unknownOrderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("[OrderDelivery Not Found]"));
    }

    private String createOrder(String token, CreateOrderHttpRequest request) {
        String orderId = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("orderId");

        createdOrderIds.add(UUID.fromString(orderId));
        return orderId;
    }
}
