package br.ifsp.demo.ui;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.ManageOrderPage;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Tag("UiTest")
class ManageOrderUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();
    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String DELIVERYMAN_ID = "cccccccc-cccc-cccc-cccc-cccccccccccc";

    @Test
    @DisplayName("Should render manage order page with its main elements")
    void shouldRenderManageOrderPageWithItsMainElements() {
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        assertThat(manageOrderPage.title().getText()).isEqualTo("Alterar Status do Pedido");
        assertThat(manageOrderPage.subtitle().isDisplayed()).isTrue();
        assertThat(manageOrderPage.orderIdInput().isDisplayed()).isTrue();
        assertThat(manageOrderPage.orderIdInput().getDomAttribute("required")).isNotNull();
        assertThat(manageOrderPage.searchButton().isEnabled()).isTrue();
        assertThat(manageOrderPage.backLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block search when order id is empty")
    void shouldBlockSearchWhenOrderIdIsEmpty() {
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        manageOrderPage.submitSearch();

        assertThat(driver.getCurrentUrl()).contains("/orders/manage");
        assertThat(manageOrderPage.isOrderIdValid()).isFalse();
        assertThat(manageOrderPage.orderIdValidationMessage()).isNotBlank();
        assertThat(manageOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show validation error when order id format is invalid")
    void shouldShowValidationErrorWhenOrderIdFormatIsInvalid() {
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        manageOrderPage.search("pedido-invalido");

        String errorMessage = manageOrderPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/manage");
        assertThat(errorMessage).containsIgnoringCase("ID");
        assertThat(errorMessage).containsIgnoringCase("UUID");
        assertThat(manageOrderPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show error when order does not exist")
    void shouldShowErrorWhenOrderDoesNotExist() {
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        manageOrderPage.search(UUID.randomUUID().toString());

        String errorMessage = manageOrderPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/manage");
        assertThat(errorMessage).containsIgnoringCase("encontrado");
        assertThat(manageOrderPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show cancel action for created order")
    void shouldShowCancelActionForCreatedOrder() {
        String token = authenticateRegisteredUser();
        String orderId = createOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.actionsSection();

        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Criado");
        assertThat(manageOrderPage.isActionButtonVisible("Cancelar pedido")).isTrue();
        assertThat(manageOrderPage.isActionButtonVisible("Iniciar rota")).isFalse();
        assertThat(manageOrderPage.isActionButtonVisible("Concluir pedido")).isFalse();
    }

    @Test
    @DisplayName("Should dismiss action when user cancels browser confirmation")
    void shouldDismissActionWhenUserCancelsBrowserConfirmation() {
        String token = authenticateRegisteredUser();
        String orderId = createOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.dismissAction("Cancelar pedido");

        assertThat(manageOrderPage.isSuccessAlertVisible()).isFalse();
        assertOrderStatus(token, orderId, "CREATED");
    }

    @Test
    @DisplayName("Should cancel created order and show final status message")
    void shouldCancelCreatedOrderAndShowFinalStatusMessage() {
        String token = authenticateRegisteredUser();
        String orderId = createOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.confirmAction("Cancelar pedido");

        assertThat(manageOrderPage.waitForSuccessAlertText()).containsIgnoringCase("cancelado");
        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Cancelado");
        assertThat(manageOrderPage.infoAlertText()).containsIgnoringCase("finalizado");
        assertOrderStatus(token, orderId, "CANCELED");
    }

    @Test
    @DisplayName("Should show dispatched order actions")
    void shouldShowDispatchedOrderActions() {
        String token = authenticateRegisteredUser();
        String orderId = createDispatchedOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();

        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Despachado");
        assertThat(manageOrderPage.isActionButtonVisible("Iniciar rota")).isTrue();
        assertThat(manageOrderPage.isActionButtonVisible("Cancelar rota")).isTrue();
        assertThat(manageOrderPage.isActionButtonVisible("Cancelar pedido")).isTrue();
        cancelRoute(token, orderId);
    }

    @Test
    @DisplayName("Should start route for dispatched order")
    void shouldStartRouteForDispatchedOrder() {
        String token = authenticateRegisteredUser();
        String orderId = createDispatchedOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.confirmAction("Iniciar rota");

        assertThat(manageOrderPage.waitForSuccessAlertText()).containsIgnoringCase("Rota iniciada");
        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Em rota");
        assertThat(manageOrderPage.isActionButtonVisible("Concluir pedido")).isTrue();
        assertOrderStatus(token, orderId, "EN_ROUTE");
        cancelRoute(token, orderId);
    }

    @Test
    @DisplayName("Should cancel route and return order to created status")
    void shouldCancelRouteAndReturnOrderToCreatedStatus() {
        String token = authenticateRegisteredUser();
        String orderId = createDispatchedOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.confirmAction("Cancelar rota");

        assertThat(manageOrderPage.waitForSuccessAlertText()).containsIgnoringCase("Rota cancelada");
        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Criado");
        assertThat(manageOrderPage.isActionButtonVisible("Cancelar pedido")).isTrue();
        assertOrderStatus(token, orderId, "CREATED");
    }

    @Test
    @DisplayName("Should conclude order that is en route")
    void shouldConcludeOrderThatIsEnRoute() {
        String token = authenticateRegisteredUser();
        String orderId = createEnRouteOrder(token);
        ManageOrderPage manageOrderPage = openManageOrderPage(token);

        manageOrderPage.search(orderId);
        manageOrderPage.orderCard();
        manageOrderPage.confirmAction("Concluir pedido");

        assertThat(manageOrderPage.waitForSuccessAlertText()).containsIgnoringCase("conclu");
        assertThat(manageOrderPage.statusBadge().getText()).isEqualTo("Concluído");
        assertThat(manageOrderPage.infoAlertText()).containsIgnoringCase("finalizado");
        assertOrderStatus(token, orderId, "CONCLUDED");
    }

    @Test
    @DisplayName("Should navigate back to dashboard from back link")
    void shouldNavigateBackToDashboardFromBackLink() {
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        manageOrderPage.backLink().click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("Should keep manage order page usable on mobile viewport")
    void shouldKeepManageOrderPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        ManageOrderPage manageOrderPage = openAuthenticatedManageOrderPage();

        assertThat(manageOrderPage.title().isDisplayed()).isTrue();
        assertThat(manageOrderPage.orderIdInput().isDisplayed()).isTrue();
        assertThat(manageOrderPage.searchButton().isDisplayed()).isTrue();
        assertThat(manageOrderPage.backLink().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private ManageOrderPage openAuthenticatedManageOrderPage() {
        return openManageOrderPage(authenticateRegisteredUser());
    }

    private ManageOrderPage openManageOrderPage(String token) {
        ManageOrderPage manageOrderPage = new ManageOrderPage(driver, wait, baseUrl);
        manageOrderPage.openAuthenticated(token);
        manageOrderPage.waitUntilLoaded();
        return manageOrderPage;
    }

    private String createOrder(String token) {
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 7.3);

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

    private String createDispatchedOrder(String token) {
        String orderId = createOrder(token);

        given()
                .baseUri(apiBaseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/dispatch/{deliverymanId}", orderId, DELIVERYMAN_ID)
                .then()
                .statusCode(204);

        return orderId;
    }

    private String createEnRouteOrder(String token) {
        String orderId = createDispatchedOrder(token);

        given()
                .baseUri(apiBaseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/start-route", orderId)
                .then()
                .statusCode(204);

        return orderId;
    }

    private void cancelRoute(String token, String orderId) {
        given()
                .baseUri(apiBaseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .patch("/api/v1/orders/{orderId}/cancel-route", orderId)
                .then()
                .statusCode(204);
    }

    private void assertOrderStatus(String token, String orderId, String status) {
        given()
                .baseUri(apiBaseUrl)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders/{orderId}", orderId)
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
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

    private boolean hasHorizontalOverflow() {
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.scrollWidth;");
        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.clientWidth;");

        return scrollWidth > clientWidth;
    }
}
