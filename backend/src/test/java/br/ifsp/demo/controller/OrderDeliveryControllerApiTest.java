package br.ifsp.demo.controller;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.infrastructure.persistence.entity.DeliveryManEntity;
import br.ifsp.demo.infrastructure.persistence.repository.DeliveryManJpaRepository;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.oneOf;

@Tag("ApiTest")
@Tag("IntegrationTest")
class OrderDeliveryControllerApiTest extends BaseApiIntegrationTest {

    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private OrderDeliveryJpaRepository orderRepository;

    @Autowired
    private DeliveryManJpaRepository deliveryManRepository;

    private final List<UUID> createdOrderIds = new ArrayList<>();
    private final List<UUID> createdDeliveryManIds = new ArrayList<>();

    @AfterEach
    void cleanOrders() {
        createdOrderIds.forEach(orderRepository::deleteById);
        createdOrderIds.clear();
        createdDeliveryManIds.forEach(deliveryManRepository::deleteById);
        createdDeliveryManIds.clear();
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
    @DisplayName("POST /api/v1/orders should return 404 when customer does not exist")
    void createOrderShouldReturn404WhenCustomerDoesNotExist() {
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
                .statusCode(404)
                .body("message", equalTo("Customer not found"));
    }

    @Test
    @DisplayName("POST /api/v1/orders should return 400 when distance is not positive")
    void createOrderShouldReturn400WhenDistanceIsNotPositive() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 0.0);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("message", equalTo("Distance cannot be zero or negative"));
    }

    @Test
    @DisplayName("POST /api/v1/orders should return 400 when customer id is missing")
    void createOrderShouldReturn400WhenCustomerIdIsMissing() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        String request = """
                {
                  "pickupStreet": "Rua A",
                  "pickupNumber": "100",
                  "pickupNeighborhood": "Centro",
                  "pickupCity": "Sao Carlos",
                  "pickupState": "SP",
                  "pickupCountry": "Brasil",
                  "pickupCep": "13500000",
                  "deliveryStreet": "Rua B",
                  "deliveryNumber": "200",
                  "deliveryNeighborhood": "Jardim",
                  "deliveryCity": "Araraquara",
                  "deliveryState": "SP",
                  "deliveryCountry": "Brasil",
                  "deliveryCep": "14800000",
                  "distanceKm": 12.5
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("status", equalTo("BAD_REQUEST"))
                .body("message", containsString("customerId: Customer ID is required"));
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

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/cancel should cancel order and return 204")
    void cancelOrderShouldReturn204AndUpdateOrderStatus() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 9.0);
        String orderId = createOrder(token, request);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("CANCELED"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/cancel should return 404 when order does not exist")
    void cancelOrderShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel", unknownOrderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("[OrderDelivery Not Found]"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/dispatch/{deliverymanId} should dispatch order and return 204")
    void dispatchOrderShouldReturn204AndUpdateOrderStatus() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 11.0);
        String orderId = createOrder(token, request);
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, deliveryManId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("DISPATCHED"))
                .body("deliveryman.id", equalTo(deliveryManId.toString()));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/dispatch/{deliverymanId} should return 404 when deliveryman does not exist")
    void dispatchOrderShouldReturn404WhenDeliveryManDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 11.0);
        String orderId = createOrder(token, request);
        UUID unknownDeliveryManId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, unknownDeliveryManId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("Delivery Man not found"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/dispatch/{deliverymanId} should return 404 when order does not exist")
    void dispatchOrderShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", unknownOrderId, deliveryManId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("Order not found"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/dispatch/{deliverymanId} should return 400 when order is canceled")
    void dispatchOrderShouldReturn400WhenOrderIsCanceled() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 11.0);
        String orderId = createOrder(token, request);
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel", orderId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, deliveryManId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("message", equalTo("[Order already cancelled]"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/cancel-route should cancel route and return 204")
    void cancelRouteShouldReturn204AndUpdateOrderStatus() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 13.0);
        String orderId = createOrder(token, request);
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, deliveryManId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel-route", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("CREATED"))
                .body("deliveryman", nullValue());
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/cancel-route should return 404 when order does not exist")
    void cancelRouteShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel-route", unknownOrderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("[OrderDelivery Not Found]"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/customer/{customerId} should return customer orders")
    void listByCustomerShouldReturnCustomerOrders() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        String firstOrderId = createOrder(
                token,
                EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 5.0)
        );
        String secondOrderId = createOrder(
                token,
                EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 8.0)
        );

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/customer/{customerId}", SEEDED_CUSTOMER_ID)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", hasItems(firstOrderId, secondOrderId))
                .body("customer.customerId", everyItem(equalTo(SEEDED_CUSTOMER_ID.toString())));
    }

    @Test
    @DisplayName("GET /api/v1/orders/customer/{customerId} should return 404 when customer does not exist")
    void listByCustomerShouldReturn404WhenCustomerDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownCustomerId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/customer/{customerId}", unknownCustomerId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("Customer not found"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/start-route should start route and return 204")
    void startRouteShouldReturn204AndUpdateOrderStatus() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 14.0);
        String orderId = createOrder(token, request);
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, deliveryManId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/start-route", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("EN_ROUTE"))
                .body("deliveryman.id", equalTo(deliveryManId.toString()));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/start-route should return 404 when order does not exist")
    void startRouteShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/start-route", unknownOrderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("[OrderDelivery Not Found]"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/start-route should return 400 when order is not dispatched")
    void startRouteShouldReturn400WhenOrderIsNotDispatched() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 14.0);
        String orderId = createOrder(token, request);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/start-route", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("message", equalTo("[OrderStatus is not DISPATCHED]"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/conclude should conclude order and return 204")
    void concludeOrderShouldReturn204AndUpdateOrderStatus() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 16.0);
        String orderId = createOrder(token, request);
        UUID deliveryManId = createDeliveryMan();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, deliveryManId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/start-route", orderId)
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/conclude", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("id", equalTo(orderId))
                .body("status", equalTo("CONCLUDED"))
                .body("deliveryman.id", equalTo(deliveryManId.toString()));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/conclude should return 404 when order does not exist")
    void concludeOrderShouldReturn404WhenOrderDoesNotExist() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        UUID unknownOrderId = UUID.randomUUID();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/conclude", unknownOrderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(404)
                .body("message", equalTo("[OrderDelivery Not Found]"));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{orderId}/conclude should return 400 when order is not en route")
    void concludeOrderShouldReturn400WhenOrderIsNotEnRoute() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 16.0);
        String orderId = createOrder(token, request);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/conclude", orderId)
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400)
                .body("message", equalTo("[OrderStatus is not EN_ROUTE]"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/queue should return priority queue with created orders")
    void getPriorityQueueShouldReturnCreatedOrders() {
        String password = "123password";
        User user = registerUser(password);
        String token = authenticate(user.getEmail(), password);
        String firstOrderId = createOrder(
                token,
                EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 6.0)
        );
        String secondOrderId = createOrder(
                token,
                EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 15.0)
        );

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/queue")
                .then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("orderId", hasItems(firstOrderId, secondOrderId))
                .body("customerName", everyItem(equalTo("Acme Corp")))
                .body("customerType", everyItem(equalTo("BUSINESS")))
                .body("score", everyItem(greaterThanOrEqualTo(0)))
                .body("priorityLevel", everyItem(oneOf("NORMAL", "CRITICAL", "URGENT")))
                .body("waitMinutes", everyItem(greaterThanOrEqualTo(0)));
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

    private UUID createDeliveryMan() {
        DeliveryManEntity deliveryMan = new DeliveryManEntity();
        deliveryMan.setId(UUID.randomUUID());
        deliveryMan.setName("API Test Deliveryman");
        deliveryMan.setCapacity(10);

        deliveryManRepository.save(deliveryMan);
        createdDeliveryManIds.add(deliveryMan.getId());
        return deliveryMan.getId();
    }
}
