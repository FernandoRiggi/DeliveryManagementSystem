package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchOrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Buscar Pedido']");
    private final By orderIdInput = By.cssSelector("input.form-control");
    private final By submitButton = By.xpath("//button[normalize-space()='Buscar']");
    private final By backLink = By.xpath("//a[contains(@class, 'btn') and contains(normalize-space(), 'Voltar')]");
    private final By errorAlert = By.cssSelector(".alert-danger");
    private final By resultSection = By.xpath("//*[normalize-space()='Resultado']");
    private final By orderCard = By.cssSelector(".order-card");
    private final By orderIdBadge = By.cssSelector(".order-id");
    private final By statusBadge = By.cssSelector(".order-card .badge");

    public SearchOrderPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/orders/search");
    }

    public void openAuthenticated(String token) {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', arguments[0]);", token);
        open();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderIdInput));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement orderIdInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderIdInput));
    }

    public WebElement submitButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement backLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public WebElement resultSection() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(resultSection));
    }

    public WebElement orderCard() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderCard));
    }

    public WebElement orderIdBadge() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderIdBadge));
    }

    public WebElement statusBadge() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(statusBadge));
    }

    public void submit() {
        submitButton().click();
    }

    public void fillOrderId(String orderId) {
        orderIdInput().clear();
        orderIdInput().sendKeys(orderId);
    }

    public void search(String orderId) {
        fillOrderId(orderId);
        submit();
    }

    public String waitForErrorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isOrderCardVisible() {
        return driver.findElements(orderCard).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isOrderIdValid() {
        return isValid(orderIdInput());
    }

    public String orderIdValidationMessage() {
        return orderIdInput().getDomProperty("validationMessage");
    }

    public String orderCardText() {
        return orderCard().getText();
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
