package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.RegisterPage;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
class RegisterUiTest extends BaseUiTest {

    private static final Faker faker = Faker.instance();

    @Test
    @DisplayName("Should render register page with its main elements")
    void shouldRenderRegisterPageWithItsMainElements() {
        RegisterPage registerPage = new RegisterPage(driver, wait, baseUrl);

        registerPage.open();
        registerPage.waitUntilLoaded();

        assertThat(registerPage.title().getText()).isEqualTo("Criar conta");
        assertThat(registerPage.subtitle().isDisplayed()).isTrue();
        assertThat(registerPage.nameInput().isDisplayed()).isTrue();
        assertThat(registerPage.nameInput().getDomAttribute("required")).isNotNull();
        assertThat(registerPage.lastnameInput().isDisplayed()).isTrue();
        assertThat(registerPage.lastnameInput().getDomAttribute("required")).isNotNull();
        assertThat(registerPage.emailInput().isDisplayed()).isTrue();
        assertThat(registerPage.emailInput().getDomAttribute("required")).isNotNull();
        assertThat(registerPage.passwordInput().isDisplayed()).isTrue();
        assertThat(registerPage.passwordInput().getDomAttribute("type")).isEqualTo("password");
        assertThat(registerPage.passwordInput().getDomAttribute("required")).isNotNull();
        assertThat(registerPage.submitButton().isEnabled()).isTrue();
        assertThat(registerPage.loginLink().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should block register submission when required fields are empty")
    void shouldBlockRegisterSubmissionWhenRequiredFieldsAreEmpty() {
        RegisterPage registerPage = new RegisterPage(driver, wait, baseUrl);

        registerPage.open();
        registerPage.waitUntilLoaded();
        registerPage.submit();

        assertThat(driver.getCurrentUrl()).contains("/register");
        assertThat(registerPage.isNameValid()).isFalse();
        assertThat(registerPage.isLastnameValid()).isFalse();
        assertThat(registerPage.isEmailValid()).isFalse();
        assertThat(registerPage.isPasswordValid()).isFalse();
        assertThat(registerPage.nameValidationMessage()).isNotBlank();
    }

    @Test
    @DisplayName("Should show error when register data does not meet size rules")
    void shouldShowErrorWhenRegisterDataDoesNotMeetSizeRules() {
        RegisterPage registerPage = new RegisterPage(driver, wait, baseUrl);
        String email = "ui-invalid-" + UUID.randomUUID() + "@" + faker.internet().domainName();

        registerPage.open();
        registerPage.waitUntilLoaded();
        registerPage.register("1", "1", email, "1");

        String errorMessage = registerPage.waitForErrorAlertText();

        assertThat(driver.getCurrentUrl()).contains("/register");
        assertThat(errorMessage).isNotBlank();
    }

    @Test
    @DisplayName("Should block register submission when email format is invalid")
    void shouldBlockRegisterSubmissionWhenEmailFormatIsInvalid() {
        RegisterPage registerPage = new RegisterPage(driver, wait, baseUrl);

        registerPage.open();
        registerPage.waitUntilLoaded();
        registerPage.register("Joao", "Silva", "email-invalido", "senhaValida123");

        assertThat(driver.getCurrentUrl()).contains("/register");
        assertThat(registerPage.isNameValid()).isTrue();
        assertThat(registerPage.isLastnameValid()).isTrue();
        assertThat(registerPage.isEmailValid()).isFalse();
        assertThat(registerPage.isPasswordValid()).isTrue();
        assertThat(registerPage.emailValidationMessage()).isNotBlank();
    }
}
