package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
class LoginUiTest extends BaseUiTest {

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
}
