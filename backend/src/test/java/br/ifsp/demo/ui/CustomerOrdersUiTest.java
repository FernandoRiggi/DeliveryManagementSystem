package br.ifsp.demo.ui;

import br.ifsp.demo.domain.dto.CreateOrderHttpRequest;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.CustomerOrdersPage;
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
class CustomerOrdersUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();
    private static final UUID SEEDED_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("Should render customer orders page with its main elements")
    void shouldRenderCustomerOrdersPageWithItsMainElements() {
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        assertThat(customerOrdersPage.title().getText()).isEqualTo("Pedidos por Cliente");
        assertThat(customerOrdersPage.customerIdInput().isDisplayed()).isTrue();
        assertThat(customerOrdersPage.customerIdInput().getDomAttribute("required")).isNotNull();
        assertThat(customerOrdersPage.submitButton().isEnabled()).isTrue();
        assertThat(customerOrdersPage.backLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block customer order search when customer id is empty")
    void shouldBlockCustomerOrderSearchWhenCustomerIdIsEmpty() {
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        customerOrdersPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/orders/customer");
        assertThat(customerOrdersPage.isCustomerIdValid()).isFalse();
        assertThat(customerOrdersPage.customerIdValidationMessage()).isNotBlank();
        assertThat(customerOrdersPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show validation error when customer id format is invalid")
    void shouldShowValidationErrorWhenCustomerIdFormatIsInvalid() {
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        customerOrdersPage.search("cliente-invalido");

        String errorMessage = customerOrdersPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/customer");
        assertThat(errorMessage).containsIgnoringCase("ID");
        assertThat(errorMessage).containsIgnoringCase("UUID");
        assertThat(errorMessage).contains("550e8400-e29b-41d4-a716-446655440000");
        assertThat(customerOrdersPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show error when customer does not exist")
    void shouldShowErrorWhenCustomerDoesNotExist() {
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        customerOrdersPage.search(UUID.randomUUID().toString());

        String errorMessage = customerOrdersPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/customer");
        assertThat(errorMessage).isNotBlank();
        assertThat(errorMessage).containsIgnoringCase("cliente");
        assertThat(customerOrdersPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should list orders for existing customer")
    void shouldListOrdersForExistingCustomer() {
        String token = authenticateRegisteredUser();
        createOrder(token, 41.7);
        createOrder(token, 42.8);
        CustomerOrdersPage customerOrdersPage = openCustomerOrdersPage(token);

        customerOrdersPage.search(SEEDED_CUSTOMER_ID.toString());
        customerOrdersPage.waitForAtLeastOneOrderCard();

        assertThat(customerOrdersPage.resultCount().getText()).containsIgnoringCase("pedido");
        assertThat(customerOrdersPage.allOrderCardsText()).contains("Acme Corp");
        assertThat(customerOrdersPage.allOrderCardsText()).contains("41.7 km");
        assertThat(customerOrdersPage.allOrderCardsText()).contains("42.8 km");
    }

    @Test
    @DisplayName("Should clear previous list when customer search returns error")
    void shouldClearPreviousListWhenCustomerSearchReturnsError() {
        String token = authenticateRegisteredUser();
        createOrder(token, 18.4);
        CustomerOrdersPage customerOrdersPage = openCustomerOrdersPage(token);

        customerOrdersPage.search(SEEDED_CUSTOMER_ID.toString());
        customerOrdersPage.waitForAtLeastOneOrderCard();
        customerOrdersPage.search(UUID.randomUUID().toString());

        String errorMessage = customerOrdersPage.waitForErrorAlertText();

        assertThat(errorMessage).isNotBlank();
        assertThat(customerOrdersPage.isOrderCardVisible()).isFalse();
    }

    @Test
    @DisplayName("Should navigate back to dashboard from back link")
    void shouldNavigateBackToDashboardFromBackLink() {
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        customerOrdersPage.backLink().click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("Should keep customer orders page usable on mobile viewport")
    void shouldKeepCustomerOrdersPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        CustomerOrdersPage customerOrdersPage = openAuthenticatedCustomerOrdersPage();

        assertThat(customerOrdersPage.title().isDisplayed()).isTrue();
        assertThat(customerOrdersPage.customerIdInput().isDisplayed()).isTrue();
        assertThat(customerOrdersPage.submitButton().isDisplayed()).isTrue();
        assertThat(customerOrdersPage.backLink().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private CustomerOrdersPage openAuthenticatedCustomerOrdersPage() {
        return openCustomerOrdersPage(authenticateRegisteredUser());
    }

    private CustomerOrdersPage openCustomerOrdersPage(String token) {
        CustomerOrdersPage customerOrdersPage = new CustomerOrdersPage(driver, wait, baseUrl);
        customerOrdersPage.openAuthenticated(token);
        customerOrdersPage.waitUntilLoaded();
        return customerOrdersPage;
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
