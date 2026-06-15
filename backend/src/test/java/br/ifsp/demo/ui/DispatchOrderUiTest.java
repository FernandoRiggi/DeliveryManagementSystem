package br.ifsp.demo.ui;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.DispatchOrderPage;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Tag("UiTest")
class DispatchOrderUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();
    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String DELIVERYMAN_ID = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee";

    @Test
    @DisplayName("Should render dispatch order page with its main elements")
    void shouldRenderDispatchOrderPageWithItsMainElements() {
        DispatchOrderPage dispatchOrderPage = openAuthenticatedDispatchOrderPage();

        assertThat(dispatchOrderPage.title().getText()).isEqualTo("Despachar Pedido");
        assertThat(dispatchOrderPage.subtitle().isDisplayed()).isTrue();
        assertThat(dispatchOrderPage.backLink().isDisplayed()).isTrue();
        dispatchOrderPage.waitForQueueLoaded();
        assertThat(dispatchOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show created order in dispatch queue")
    void shouldShowCreatedOrderInDispatchQueue() {
        String token = authenticateRegisteredUser();
        Distance distance = uniqueDistance();
        createOrder(token, distance.value());
        DispatchOrderPage dispatchOrderPage = openDispatchOrderPage(token);

        dispatchOrderPage.waitForQueueItemContaining(distance.label());

        assertThat(dispatchOrderPage.queueText()).contains("Acme Corp");
        assertThat(dispatchOrderPage.queueText()).contains(distance.label());
    }

    @Test
    @DisplayName("Should select order and require deliveryman before dispatch")
    void shouldSelectOrderAndRequireDeliverymanBeforeDispatch() {
        String token = authenticateRegisteredUser();
        Distance distance = uniqueDistance();
        createOrder(token, distance.value());
        DispatchOrderPage dispatchOrderPage = openDispatchOrderPage(token);

        dispatchOrderPage.selectQueueItemContaining(distance.label());
        dispatchOrderPage.orderCard();

        assertThat(dispatchOrderPage.selectedQueueItem().getText()).contains(distance.label());
        assertThat(dispatchOrderPage.orderCardText()).contains("Acme Corp");
        assertThat(dispatchOrderPage.confirmDispatchButton().isEnabled()).isFalse();

        dispatchOrderPage.selectDeliverymanContaining("Victor");

        assertThat(dispatchOrderPage.confirmDispatchButton().isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should cancel selected order and clear dispatch details")
    void shouldCancelSelectedOrderAndClearDispatchDetails() {
        String token = authenticateRegisteredUser();
        Distance distance = uniqueDistance();
        createOrder(token, distance.value());
        DispatchOrderPage dispatchOrderPage = openDispatchOrderPage(token);

        dispatchOrderPage.selectQueueItemContaining(distance.label());
        dispatchOrderPage.orderCard();
        dispatchOrderPage.cancelSelectionButton().click();

        assertThat(dispatchOrderPage.isOrderCardVisible()).isFalse();
        assertThat(dispatchOrderPage.isConfirmDispatchVisible()).isFalse();
    }

    @Test
    @DisplayName("Should dispatch selected order with deliveryman and show success")
    void shouldDispatchSelectedOrderWithDeliverymanAndShowSuccess() {
        String token = authenticateRegisteredUser();
        Distance distance = uniqueDistance();
        String orderId = createOrder(token, distance.value());
        DispatchOrderPage dispatchOrderPage = openDispatchOrderPage(token);

        dispatchOrderPage.selectQueueItemContaining(distance.label());
        dispatchOrderPage.orderCard();
        dispatchOrderPage.selectDeliverymanContaining("Victor");
        dispatchOrderPage.confirmDispatch();

        assertThat(dispatchOrderPage.successText()).containsIgnoringCase("Pedido despachado");
        assertDispatchedOrder(token, orderId);
    }

    @Test
    @DisplayName("Should remove dispatched order from queue after reset")
    void shouldRemoveDispatchedOrderFromQueueAfterReset() {
        String token = authenticateRegisteredUser();
        Distance distance = uniqueDistance();
        createOrder(token, distance.value());
        DispatchOrderPage dispatchOrderPage = openDispatchOrderPage(token);

        dispatchOrderPage.selectQueueItemContaining(distance.label());
        dispatchOrderPage.orderCard();
        dispatchOrderPage.selectDeliverymanContaining("Victor");
        dispatchOrderPage.confirmDispatch();
        dispatchOrderPage.successText();
        dispatchOrderPage.dispatchAnotherButton().click();
        dispatchOrderPage.waitForQueueLoaded();

        assertThat(dispatchOrderPage.queueText()).doesNotContain(distance.label());
    }

    @Test
    @DisplayName("Should navigate back to dashboard from back link")
    void shouldNavigateBackToDashboardFromBackLink() {
        DispatchOrderPage dispatchOrderPage = openAuthenticatedDispatchOrderPage();

        dispatchOrderPage.backLink().click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("Should keep dispatch order page usable on mobile viewport")
    void shouldKeepDispatchOrderPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        DispatchOrderPage dispatchOrderPage = openAuthenticatedDispatchOrderPage();

        assertThat(dispatchOrderPage.title().isDisplayed()).isTrue();
        assertThat(dispatchOrderPage.backLink().isDisplayed()).isTrue();
        dispatchOrderPage.waitForQueueLoaded();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private DispatchOrderPage openAuthenticatedDispatchOrderPage() {
        return openDispatchOrderPage(authenticateRegisteredUser());
    }

    private DispatchOrderPage openDispatchOrderPage(String token) {
        DispatchOrderPage dispatchOrderPage = new DispatchOrderPage(driver, wait, baseUrl);
        dispatchOrderPage.openAuthenticated(token);
        dispatchOrderPage.waitUntilLoaded();
        return dispatchOrderPage;
    }

    private String createOrder(String token, double distanceKm) {
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, distanceKm);

        return given()
                .baseUri(apiBaseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract()
                .path("orderId");
    }

    private void assertDispatchedOrder(String token, String orderId) {
        given()
                .baseUri(apiBaseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .statusCode(200)
                .body("status", equalTo("DISPATCHED"))
                .body("deliveryman.id", equalTo(DELIVERYMAN_ID));
    }

    private String authenticateRegisteredUser() {
        String password = "senhaValida123";
        RegisterUserRequest user = new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                EntityBuilder.TEST_EMAIL_PREFIX + UUID.randomUUID() + "@" + faker.internet().domainName(),
                password
        );
        registerUser(user);

        return given()
                .baseUri(apiBaseUrl)
                .contentType(ContentType.JSON)
                .body(new AuthRequest(user.email(), password))
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    private void registerUser(RegisterUserRequest user) {
        given()
                .baseUri(apiBaseUrl)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(201);
    }

    private Distance uniqueDistance() {
        int whole = ThreadLocalRandom.current().nextInt(60, 140);
        int tenth = ThreadLocalRandom.current().nextInt(1, 10);
        double value = whole + tenth / 10.0;
        String label = String.format(Locale.US, "%d.%d km", whole, tenth);

        return new Distance(value, label);
    }

    private boolean hasHorizontalOverflow() {
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.scrollWidth;");
        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.clientWidth;");

        return scrollWidth > clientWidth;
    }

    private record Distance(double value, String label) {
    }
}
