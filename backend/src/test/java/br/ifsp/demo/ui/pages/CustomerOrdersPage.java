package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CustomerOrdersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Pedidos por Cliente']");
    private final By customerIdInput = By.cssSelector("input.form-control");
    private final By submitButton = By.xpath("//button[normalize-space()='Buscar']");
    private final By backLink = By.xpath("//a[contains(@class, 'btn') and contains(normalize-space(), 'Voltar')]");
    private final By errorAlert = By.cssSelector(".alert-danger");
    private final By infoAlert = By.cssSelector(".alert-info");
    private final By resultCount = By.cssSelector(".section-label");
    private final By orderCard = By.cssSelector(".order-card");

    public CustomerOrdersPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/orders/customer");
    }

    public void openAuthenticated(String token) {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', arguments[0]);", token);
        open();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(customerIdInput));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement customerIdInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(customerIdInput));
    }

    public WebElement submitButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement backLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public WebElement resultCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(resultCount));
    }

    public void submit() {
        submitButton().click();
    }

    public void fillCustomerId(String customerId) {
        customerIdInput().clear();
        customerIdInput().sendKeys(customerId);
    }

    public void search(String customerId) {
        fillCustomerId(customerId);
        submit();
    }

    public void waitForAtLeastOneOrderCard() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(orderCard, 0));
    }

    public List<WebElement> orderCards() {
        return driver.findElements(orderCard);
    }

    public String allOrderCardsText() {
        return orderCards().stream()
                .map(WebElement::getText)
                .reduce("", (left, right) -> left + "\n" + right);
    }

    public String waitForErrorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    public String waitForInfoAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(infoAlert)).getText();
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isInfoAlertVisible() {
        return driver.findElements(infoAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isOrderCardVisible() {
        return driver.findElements(orderCard).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isCustomerIdValid() {
        return isValid(customerIdInput());
    }

    public String customerIdValidationMessage() {
        return customerIdInput().getDomProperty("validationMessage");
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
