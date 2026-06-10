package br.ifsp.demo.ui;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.SearchOrderPage;
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

@Tag("UiTest")
class SearchOrderUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();
    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("Should render search order page with its main elements")
    void shouldRenderSearchOrderPageWithItsMainElements() {
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        assertThat(searchOrderPage.title().getText()).isEqualTo("Buscar Pedido");
        assertThat(searchOrderPage.orderIdInput().isDisplayed()).isTrue();
        assertThat(searchOrderPage.orderIdInput().getDomAttribute("required")).isNotNull();
        assertThat(searchOrderPage.submitButton().isEnabled()).isTrue();
        assertThat(searchOrderPage.backLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block search submission when order id is empty")
    void shouldBlockSearchSubmissionWhenOrderIdIsEmpty() {
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        searchOrderPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/orders/search");
        assertThat(searchOrderPage.isOrderIdValid()).isFalse();
        assertThat(searchOrderPage.orderIdValidationMessage()).isNotBlank();
        assertThat(searchOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should block search submission when order id format is invalid")
    void shouldBlockSearchSubmissionWhenOrderIdFormatIsInvalid() {
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        searchOrderPage.fillOrderId("pedido-invalido");

        assertThat(driver.getCurrentUrl()).contains("/orders/search");
        assertThat(searchOrderPage.isOrderIdValid()).isFalse();
        assertThat(searchOrderPage.orderIdValidationMessage()).isNotBlank();
        assertThat(searchOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show error when searched order does not exist")
    void shouldShowErrorWhenSearchedOrderDoesNotExist() {
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        searchOrderPage.search(UUID.randomUUID().toString());

        String errorMessage = searchOrderPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/search");
        assertThat(errorMessage).isNotBlank();
        assertThat(errorMessage).containsIgnoringCase("encontrado");
        assertThat(searchOrderPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should search existing order and show order payload")
    void shouldSearchExistingOrderAndShowOrderPayload() {
        String token = authenticateRegisteredUser();
        String orderId = createOrder(token);
        SearchOrderPage searchOrderPage = openSearchOrderPage(token);

        searchOrderPage.search(orderId);

        searchOrderPage.resultSection();
        assertThat(searchOrderPage.orderIdBadge().getDomAttribute("title")).isEqualTo(orderId);
        assertThat(searchOrderPage.statusBadge().getText()).isEqualTo("Criado");
        assertThat(searchOrderPage.orderCardText()).contains("Acme Corp");
        assertThat(searchOrderPage.orderCardText()).contains("12.5 km");
    }

    @Test
    @DisplayName("Should clear previous result when search returns error")
    void shouldClearPreviousResultWhenSearchReturnsError() {
        String token = authenticateRegisteredUser();
        String orderId = createOrder(token);
        SearchOrderPage searchOrderPage = openSearchOrderPage(token);

        searchOrderPage.search(orderId);
        searchOrderPage.orderCard();
        searchOrderPage.search(UUID.randomUUID().toString());

        String errorMessage = searchOrderPage.waitForErrorAlertText();

        assertThat(errorMessage).isNotBlank();
        assertThat(searchOrderPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should navigate back to dashboard from back link")
    void shouldNavigateBackToDashboardFromBackLink() {
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        searchOrderPage.backLink().click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("Should keep search order page usable on mobile viewport")
    void shouldKeepSearchOrderPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        SearchOrderPage searchOrderPage = openAuthenticatedSearchOrderPage();

        assertThat(searchOrderPage.title().isDisplayed()).isTrue();
        assertThat(searchOrderPage.orderIdInput().isDisplayed()).isTrue();
        assertThat(searchOrderPage.submitButton().isDisplayed()).isTrue();
        assertThat(searchOrderPage.backLink().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private SearchOrderPage openAuthenticatedSearchOrderPage() {
        return openSearchOrderPage(authenticateRegisteredUser());
    }

    private SearchOrderPage openSearchOrderPage(String token) {
        SearchOrderPage searchOrderPage = new SearchOrderPage(driver, wait, baseUrl);
        searchOrderPage.openAuthenticated(token);
        searchOrderPage.waitUntilLoaded();
        return searchOrderPage;
    }

    private String createOrder(String token) {
        CreateOrderHttpRequest request = EntityBuilder.createRandomCreateOrderRequest(SEEDED_CUSTOMER_ID, 12.5);

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
