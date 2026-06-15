package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Dashboard']");
    private final By subtitle = By.xpath("//*[contains(normalize-space(), 'Sistema de gerenciamento de entregas')]");
    private final By logoutButton = By.xpath("//button[normalize-space()='Sair']");
    private final By createOrderCard = By.xpath("//a[contains(@class, 'nav-card') and .//h5[normalize-space()='Criar Pedido']]");
    private final By searchOrderCard = By.xpath("//a[contains(@class, 'nav-card') and .//h5[normalize-space()='Buscar Pedido']]");
    private final By customerOrdersCard = By.xpath("//a[contains(@class, 'nav-card') and .//h5[normalize-space()='Pedidos por Cliente']]");
    private final By dispatchOrderCard = By.xpath("//a[contains(@class, 'nav-card') and .//h5[normalize-space()='Despachar Pedido']]");
    private final By manageOrderCard = By.xpath("//a[contains(@class, 'nav-card') and .//h5[normalize-space()='Alterar Status']]");
    private final By referenceToggle = By.cssSelector(".ref-toggle");
    private final By referenceBody = By.cssSelector(".ref-body");
    private final By acmeCorpReference = By.xpath("//*[normalize-space()='Acme Corp']");
    private final By carlosMendesReference = By.xpath("//*[normalize-space()='Carlos Mendes']");

    public DashboardPage(WebDriverWait wait) {
        this.driver = null;
        this.wait = wait;
        this.baseUrl = null;
    }

    public DashboardPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/dashboard");
    }

    public void openAuthenticated() {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', 'ui-test-token');");
        open();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement subtitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtitle));
    }

    public WebElement logoutButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    }

    public WebElement createOrderCard() {
        return wait.until(ExpectedConditions.elementToBeClickable(createOrderCard));
    }

    public WebElement searchOrderCard() {
        return wait.until(ExpectedConditions.elementToBeClickable(searchOrderCard));
    }

    public WebElement customerOrdersCard() {
        return wait.until(ExpectedConditions.elementToBeClickable(customerOrdersCard));
    }

    public WebElement dispatchOrderCard() {
        return wait.until(ExpectedConditions.elementToBeClickable(dispatchOrderCard));
    }

    public WebElement manageOrderCard() {
        return wait.until(ExpectedConditions.elementToBeClickable(manageOrderCard));
    }

    public WebElement referenceToggle() {
        return wait.until(ExpectedConditions.elementToBeClickable(referenceToggle));
    }

    public WebElement referenceBody() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(referenceBody));
    }

    public WebElement acmeCorpReference() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(acmeCorpReference));
    }

    public WebElement carlosMendesReference() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(carlosMendesReference));
    }

    public void logout() {
        logoutButton().click();
    }

    public void openReferences() {
        referenceToggle().click();
        referenceBody();
    }
}
