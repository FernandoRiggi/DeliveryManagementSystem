package br.ifsp.demo.ui.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ManageOrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Alterar Status do Pedido']");
    private final By subtitle = By.xpath("//*[contains(normalize-space(), 'Busque um pedido pelo ID')]");
    private final By orderIdInput = By.cssSelector("input.form-control");
    private final By searchButton = By.xpath("//button[normalize-space()='Buscar']");
    private final By backLink = By.xpath("//a[contains(@class, 'btn') and contains(normalize-space(), 'Voltar')]");
    private final By errorAlert = By.cssSelector(".alert-danger");
    private final By successAlert = By.cssSelector(".alert-success");
    private final By infoAlert = By.cssSelector(".alert-info");
    private final By orderCard = By.cssSelector(".order-card");
    private final By statusBadge = By.cssSelector(".order-card .badge");
    private final By actionsSection = By.xpath("//*[normalize-space()='Ações disponíveis' or normalize-space()='AÃ§Ãµes disponÃ­veis']");

    public ManageOrderPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/orders/manage");
    }

    public void openAuthenticated(String token) {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', arguments[0]);", token);
        open();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderIdInput));
        wait.until(ExpectedConditions.elementToBeClickable(searchButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement subtitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtitle));
    }

    public WebElement orderIdInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderIdInput));
    }

    public WebElement searchButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(searchButton));
    }

    public WebElement backLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public WebElement orderCard() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderCard));
    }

    public WebElement statusBadge() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(statusBadge));
    }

    public WebElement actionsSection() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(actionsSection));
    }

    public WebElement actionButton(String label) {
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + label + "']")
        ));
    }

    public void submitSearch() {
        searchButton().click();
    }

    public void search(String orderId) {
        orderIdInput().clear();
        orderIdInput().sendKeys(orderId);
        submitSearch();
    }

    public void confirmAction(String label) {
        actionButton(label).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    public void dismissAction(String label) {
        actionButton(label).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
    }

    public String waitForErrorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    public String waitForSuccessAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successAlert)).getText();
    }

    public String infoAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(infoAlert)).getText();
    }

    public String orderCardText() {
        return orderCard().getText();
    }

    public boolean isOrderCardVisible() {
        return driver.findElements(orderCard).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isSuccessAlertVisible() {
        return driver.findElements(successAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isActionButtonVisible(String label) {
        return driver.findElements(By.xpath("//button[normalize-space()='" + label + "']"))
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public boolean isOrderIdValid() {
        return isValid(orderIdInput());
    }

    public String orderIdValidationMessage() {
        return orderIdInput().getDomProperty("validationMessage");
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
