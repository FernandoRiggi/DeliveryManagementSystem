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
}
