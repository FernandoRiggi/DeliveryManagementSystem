package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreateOrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Criar Pedido']");
    private final By customerIdInput = By.name("customerId");
    private final By pickupStreetInput = By.name("pickupStreet");
    private final By pickupNumberInput = By.name("pickupNumber");
    private final By pickupNeighborhoodInput = By.name("pickupNeighborhood");
    private final By pickupCityInput = By.name("pickupCity");
    private final By pickupStateInput = By.name("pickupState");
    private final By pickupCountryInput = By.name("pickupCountry");
    private final By pickupCepInput = By.name("pickupCep");
    private final By deliveryStreetInput = By.name("deliveryStreet");
    private final By deliveryNumberInput = By.name("deliveryNumber");
    private final By deliveryNeighborhoodInput = By.name("deliveryNeighborhood");
    private final By deliveryCityInput = By.name("deliveryCity");
    private final By deliveryStateInput = By.name("deliveryState");
    private final By deliveryCountryInput = By.name("deliveryCountry");
    private final By deliveryCepInput = By.name("deliveryCep");
    private final By distanceKmInput = By.name("distanceKm");
    private final By submitButton = By.xpath("//button[normalize-space()='Criar pedido']");
    private final By cancelLink = By.linkText("Cancelar");
    private final By backLink = By.xpath("//a[contains(@class, 'btn') and contains(normalize-space(), 'Voltar')]");
    private final By errorAlert = By.cssSelector(".alert-danger");
    private final By successBox = By.cssSelector(".created-order-box");
    private final By createdOrderId = By.cssSelector(".created-order-value code");
    private final By createAnotherOrderButton = By.xpath("//button[normalize-space()='Criar outro pedido']");

    public CreateOrderPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/orders/new");
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

    public WebElement pickupStreetInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pickupStreetInput));
    }

    public WebElement pickupNumberInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pickupNumberInput));
    }

    public WebElement deliveryStreetInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(deliveryStreetInput));
    }

    public WebElement distanceKmInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(distanceKmInput));
    }

    public WebElement submitButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement cancelLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(cancelLink));
    }

    public WebElement backLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public WebElement createAnotherOrderButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(createAnotherOrderButton));
    }

    public void submit() {
        submitButton().click();
    }

    public void fillValidOrder(String customerId, String pickupStreet, String deliveryStreet, String distanceKm) {
        fill(customerIdInput, customerId);
        fill(pickupStreetInput, pickupStreet);
        fill(pickupNumberInput, "100");
        fill(pickupNeighborhoodInput, "Centro");
        fill(pickupCityInput, "Sao Carlos");
        fill(pickupStateInput, "SP");
        fill(pickupCountryInput, "Brasil");
        fill(pickupCepInput, "13500000");
        fill(deliveryStreetInput, deliveryStreet);
        fill(deliveryNumberInput, "200");
        fill(deliveryNeighborhoodInput, "Vila Nova");
        fill(deliveryCityInput, "Sao Paulo");
        fill(deliveryStateInput, "SP");
        fill(deliveryCountryInput, "Brasil");
        fill(deliveryCepInput, "01001000");
        fill(distanceKmInput, distanceKm);
    }

    public void clearDistance() {
        distanceKmInput().clear();
    }

    public String waitForErrorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    public String waitForSuccessText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successBox)).getText();
    }

    public String createdOrderIdText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(createdOrderId)).getText();
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isCustomerIdValid() {
        return isValid(customerIdInput());
    }

    public boolean isDistanceValid() {
        return isValid(distanceKmInput());
    }

    public String customerIdValidationMessage() {
        return customerIdInput().getDomProperty("validationMessage");
    }

    public String distanceValidationMessage() {
        return distanceKmInput().getDomProperty("validationMessage");
    }

    private void fill(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
