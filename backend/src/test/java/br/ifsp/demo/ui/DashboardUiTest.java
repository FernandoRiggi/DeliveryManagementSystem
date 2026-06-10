package br.ifsp.demo.ui;

import br.ifsp.demo.ui.pages.DashboardPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
class DashboardUiTest extends BaseUiTest {

    @Test
    @DisplayName("Should redirect unauthenticated user to login page")
    void shouldRedirectUnauthenticatedUserToLoginPage() {
        driver.get(baseUrl + "/dashboard");

        wait.until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Should render dashboard with its main actions")
    void shouldRenderDashboardWithItsMainActions() {
        DashboardPage dashboardPage = openAuthenticatedDashboard();

        assertThat(dashboardPage.title().getText()).isEqualTo("Dashboard");
        assertThat(dashboardPage.subtitle().isDisplayed()).isTrue();
        assertThat(dashboardPage.logoutButton().isDisplayed()).isTrue();
        assertThat(dashboardPage.createOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.searchOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.customerOrdersCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.dispatchOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.manageOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.referenceToggle().isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("Should navigate to order pages from dashboard cards")
    void shouldNavigateToOrderPagesFromDashboardCards() {
        assertCardNavigatesTo(DashboardPage::createOrderCard, "/orders/new");
        assertCardNavigatesTo(DashboardPage::searchOrderCard, "/orders/search");
        assertCardNavigatesTo(DashboardPage::customerOrdersCard, "/orders/customer");
        assertCardNavigatesTo(DashboardPage::dispatchOrderCard, "/orders/dispatch");
        assertCardNavigatesTo(DashboardPage::manageOrderCard, "/orders/manage");
    }

    @Test
    @DisplayName("Should show seeded customer and deliveryman references")
    void shouldShowSeededCustomerAndDeliverymanReferences() {
        DashboardPage dashboardPage = openAuthenticatedDashboard();

        dashboardPage.openReferences();

        assertThat(dashboardPage.acmeCorpReference().isDisplayed()).isTrue();
        assertThat(dashboardPage.carlosMendesReference().isDisplayed()).isTrue();
        assertThat(dashboardPage.referenceBody().getText()).contains("11111111");
        assertThat(dashboardPage.referenceBody().getText()).contains("aaaaaaaa");
    }

    @Test
    @DisplayName("Should logout and clear stored token")
    void shouldLogoutAndClearStoredToken() {
        DashboardPage dashboardPage = openAuthenticatedDashboard();

        dashboardPage.logout();

        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
        assertThat(storedToken()).isNull();
    }

    @Test
    @DisplayName("Should keep dashboard usable on mobile viewport")
    void shouldKeepDashboardUsableOnMobileViewport() {
        driver.manage().window().setSize(new Dimension(390, 844));
        DashboardPage dashboardPage = openAuthenticatedDashboard();

        assertThat(dashboardPage.title().isDisplayed()).isTrue();
        assertThat(dashboardPage.logoutButton().isDisplayed()).isTrue();
        assertThat(dashboardPage.createOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.searchOrderCard().isDisplayed()).isTrue();
        assertThat(dashboardPage.referenceToggle().isDisplayed()).isTrue();
        assertThat(hasHorizontalOverflow()).isFalse();
    }

    private DashboardPage openAuthenticatedDashboard() {
        DashboardPage dashboardPage = new DashboardPage(driver, wait, baseUrl);
        dashboardPage.openAuthenticated();
        dashboardPage.waitUntilLoaded();
        return dashboardPage;
    }

    private void assertCardNavigatesTo(Function<DashboardPage, WebElement> card, String path) {
        DashboardPage dashboardPage = openAuthenticatedDashboard();

        card.apply(dashboardPage).click();

        wait.until(ExpectedConditions.urlContains(path));
        assertThat(driver.getCurrentUrl()).contains(path);
    }

    private boolean hasHorizontalOverflow() {
        Long scrollWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.scrollWidth;");
        Long clientWidth = (Long) ((JavascriptExecutor) driver)
                .executeScript("return document.documentElement.clientWidth;");

        return scrollWidth > clientWidth;
    }

    private String storedToken() {
        return (String) ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token');");
    }
}
