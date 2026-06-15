package br.ifsp.demo.ui;

import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.support.EntityBuilder;
import br.ifsp.demo.ui.pages.DashboardPage;
import br.ifsp.demo.ui.pages.LoginPage;
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
class LoginUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();

    @Test
    @DisplayName("Should render login page with its main elements")
    void shouldRenderLoginPageWithItsMainElements() {
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);

        loginPage.open();
        loginPage.waitUntilLoaded();

        assertThat(loginPage.title().getText()).isEqualTo("Entrar");
        assertThat(loginPage.subtitle().isDisplayed()).isTrue();
        assertThat(loginPage.emailInput().isDisplayed()).isTrue();
        assertThat(loginPage.emailInput().getDomAttribute("required")).isNotNull();
        assertThat(loginPage.passwordInput().isDisplayed()).isTrue();
        assertThat(loginPage.passwordInput().getDomAttribute("type")).isEqualTo("password");
        assertThat(loginPage.passwordInput().getDomAttribute("required")).isNotNull();
        assertThat(loginPage.submitButton().isEnabled()).isTrue();
        assertThat(loginPage.registerLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block login submission when required fields are empty")
    void shouldBlockLoginSubmissionWhenRequiredFieldsAreEmpty() {
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);

        loginPage.open();
        loginPage.waitUntilLoaded();
        loginPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/login");
        assertThat(loginPage.isEmailValid()).isFalse();
        assertThat(loginPage.isPasswordValid()).isFalse();
        assertThat(loginPage.emailValidationMessage()).isNotBlank();
        assertThat(loginPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should block login submission when email format is invalid")
    void shouldBlockLoginSubmissionWhenEmailFormatIsInvalid() {
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);

        loginPage.open();
        loginPage.waitUntilLoaded();
        loginPage.fillEmail("email-invalido");
        loginPage.fillPassword("senhaValida123");
        loginPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/login");
        assertThat(loginPage.isEmailValid()).isFalse();
        assertThat(loginPage.isPasswordValid()).isTrue();
        assertThat(loginPage.emailValidationMessage()).isNotBlank();
        assertThat(loginPage.isErrorAlertVisible()).isFalse();
    }

    @Test
    @DisplayName("Should navigate to register page when clicking register link")
    void shouldNavigateToRegisterPageWhenClickingRegisterLink() {
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);

        loginPage.open();
        loginPage.waitUntilLoaded();
        loginPage.registerLink().click();

        wait.until(ExpectedConditions.urlContains("/register"));

        assertThat(driver.getCurrentUrl()).contains("/register");
    }

    @Test
    @DisplayName("Should show error when login credentials do not exist")
    void shouldShowErrorWhenLoginCredentialsDoNotExist() {
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);
        String nonexistentEmail = nonexistentEmail();

        loginPage.open();
        loginPage.waitUntilLoaded();
        loginPage.login(nonexistentEmail, "senhaValida123");

        String errorMessage = loginPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/login");
        assertThat(errorMessage).isNotBlank();
        assertThat(errorMessage).containsIgnoringCase("senha");
    }

    @Test
    @DisplayName("Should keep login page usable on mobile viewport")
    void shouldKeepLoginPageUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);

        loginPage.open();
        loginPage.waitUntilLoaded();

        assertThat(loginPage.title().isDisplayed()).isTrue();
        assertThat(loginPage.emailInput().isDisplayed()).isTrue();
        assertThat(loginPage.passwordInput().isDisplayed()).isTrue();
        assertThat(loginPage.submitButton().isDisplayed()).isTrue();
        assertThat(loginPage.registerLink().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    @Test
    @DisplayName("Should login with valid credentials and navigate to dashboard")
    void shouldLoginWithValidCredentialsAndNavigateToDashboard() {
        String password = "senhaValida123";
        RegisterUserRequest user = randomRegisterUser(password);
        registerUser(user);
        LoginPage loginPage = new LoginPage(driver, wait, baseUrl);
        DashboardPage dashboardPage = new DashboardPage(wait);

        loginPage.open();
        loginPage.waitUntilLoaded();
        loginPage.login(user.email(), password);
        dashboardPage.waitUntilLoaded();

        assertThat(driver.getCurrentUrl()).contains("/dashboard");
        assertThat(dashboardPage.title().getText()).isEqualTo("Dashboard");
        assertThat(storedToken()).isNotBlank();
    }

    private String nonexistentEmail() {
        String[] emailParts = faker.internet().emailAddress().split("@", 2);

        return emailParts[0] + "-" + UUID.randomUUID() + "@" + emailParts[1];
    }

    private boolean hasHorizontalOverflow() {
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.scrollWidth;");
        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.clientWidth;");

        return scrollWidth > clientWidth;
    }

    private RegisterUserRequest randomRegisterUser(String password) {
        return new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                EntityBuilder.TEST_EMAIL_PREFIX + UUID.randomUUID() + "@" + faker.internet().domainName(),
                password
        );
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

    private String storedToken() {
        return (String) ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token');");
    }
}
