package br.ifsp.demo.ui;

import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.CreateOrderPage;
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
class CreateOrderUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();
    private static final String SEEDED_CUSTOMER_ID = "11111111-1111-1111-1111-111111111111";

    @Test
    @DisplayName("Should render create order page with its main form fields")
    void shouldRenderCreateOrderPageWithItsMainFormFields() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        assertThat(createOrderPage.title().getText()).isEqualTo("Criar Pedido");
        assertThat(createOrderPage.customerIdInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.customerIdInput().getDomAttribute("required")).isNotNull();
        assertThat(createOrderPage.pickupStreetInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.pickupNumberInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.deliveryStreetInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.distanceKmInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.distanceKmInput().getDomAttribute("type")).isEqualTo("number");
        assertThat(createOrderPage.distanceKmInput().getDomAttribute("min")).isEqualTo("0.1");
        assertThat(createOrderPage.submitButton().isEnabled()).isTrue();
        assertThat(createOrderPage.cancelLink().isDisplayed()).isTrue();
        assertThat(createOrderPage.backLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block order submission when required fields are empty")
    void shouldBlockOrderSubmissionWhenRequiredFieldsAreEmpty() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/orders/new");
        assertThat(createOrderPage.isCustomerIdValid()).isFalse();
        assertThat(createOrderPage.customerIdValidationMessage()).isNotBlank();
        assertThat(createOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should block order submission when distance is below minimum")
    void shouldBlockOrderSubmissionWhenDistanceIsBelowMinimum() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.fillValidOrder(SEEDED_CUSTOMER_ID, faker.address().streetName(), faker.address().streetName(), "0");
        createOrderPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/orders/new");
        assertThat(createOrderPage.isDistanceValid()).isFalse();
        assertThat(createOrderPage.distanceValidationMessage()).isNotBlank();
        assertThat(createOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should block order submission when customer id format is invalid")
    void shouldBlockOrderSubmissionWhenCustomerIdFormatIsInvalid() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.fillValidOrder("cliente-invalido", faker.address().streetName(), faker.address().streetName(), "8.5");

        assertThat(driver.getCurrentUrl()).contains("/orders/new");
        assertThat(createOrderPage.isCustomerIdValid()).isFalse();
        assertThat(createOrderPage.customerIdValidationMessage()).isNotBlank();
        assertThat(createOrderPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should show error when creating order for nonexistent customer")
    void shouldShowErrorWhenCreatingOrderForNonexistentCustomer() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.fillValidOrder(UUID.randomUUID().toString(), faker.address().streetName(), faker.address().streetName(), "8.5");
        createOrderPage.submit();

        String errorMessage = createOrderPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/orders/new");
        assertThat(errorMessage).isNotBlank();
        assertThat(errorMessage).containsIgnoringCase("cliente");
    }

    @Test
    @DisplayName("Should create order with valid data and show generated order id")
    void shouldCreateOrderWithValidDataAndShowGeneratedOrderId() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.fillValidOrder(SEEDED_CUSTOMER_ID, faker.address().streetName(), faker.address().streetName(), "12.5");
        createOrderPage.submit();

        String successText = createOrderPage.waitForSuccessText();

        assertThat(successText).containsIgnoringCase("Pedido criado");
        assertThat(createOrderPage.createdOrderIdText())
                .matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    @Test
    @DisplayName("Should reset form after creating an order")
    void shouldResetFormAfterCreatingAnOrder() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.fillValidOrder(SEEDED_CUSTOMER_ID, faker.address().streetName(), faker.address().streetName(), "5.5");
        createOrderPage.submit();
        createOrderPage.waitForSuccessText();
        createOrderPage.createAnotherOrderButton().click();

        assertThat(createOrderPage.customerIdInput().getDomProperty("value")).isBlank();
        assertThat(createOrderPage.distanceKmInput().getDomProperty("value")).isBlank();
    }

    @Test
    @DisplayName("Should navigate back to dashboard from cancel link")
    void shouldNavigateBackToDashboardFromCancelLink() {
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        createOrderPage.cancelLink().click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        assertThat(driver.getCurrentUrl()).contains("/dashboard");
    }

    @Test
    @DisplayName("Should keep create order page usable on mobile viewport")
    void shouldKeepCreateOrderPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        CreateOrderPage createOrderPage = openAuthenticatedCreateOrderPage();

        assertThat(createOrderPage.title().isDisplayed()).isTrue();
        assertThat(createOrderPage.customerIdInput().isDisplayed()).isTrue();
        assertThat(createOrderPage.submitButton().isDisplayed()).isTrue();
        assertThat(createOrderPage.cancelLink().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private CreateOrderPage openAuthenticatedCreateOrderPage() {
        String token = authenticateRegisteredUser();
        CreateOrderPage createOrderPage = new CreateOrderPage(driver, wait, baseUrl);
        createOrderPage.openAuthenticated(token);
        createOrderPage.waitUntilLoaded();
        return createOrderPage;
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
