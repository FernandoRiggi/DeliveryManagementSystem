package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class DispatchOrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Despachar Pedido']");
    private final By subtitle = By.xpath("//*[contains(normalize-space(), 'Pedidos aguardando despacho')]");
    private final By backLink = By.xpath("//a[contains(@class, 'btn') and contains(normalize-space(), 'Voltar')]");
    private final By loadingQueueText = By.xpath("//*[contains(normalize-space(), 'Carregando fila')]");
    private final By queueItem = By.cssSelector(".queue-item");
    private final By selectedQueueItem = By.cssSelector(".queue-item--selected");
    private final By infoAlert = By.cssSelector(".alert-info");
    private final By errorAlert = By.cssSelector(".alert-danger");
    private final By orderCard = By.cssSelector(".order-card");
    private final By deliverymanCard = By.cssSelector(".deliveryman-card");
    private final By confirmDispatchButton = By.xpath("//button[normalize-space()='Confirmar despacho']");
    private final By cancelSelectionButton = By.xpath("//button[contains(normalize-space(), 'Cancelar')]");
    private final By successBox = By.cssSelector(".created-order-box");
    private final By dispatchAnotherButton = By.xpath("//button[normalize-space()='Despachar outro pedido']");

    public DispatchOrderPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/orders/dispatch");
    }

    public void openAuthenticated(String token) {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.setItem('token', arguments[0]);", token);
        open();
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(subtitle));
        wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public void waitForQueueLoaded() {
        wait.until(driver -> driver.findElements(loadingQueueText).stream().noneMatch(WebElement::isDisplayed));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement subtitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtitle));
    }

    public WebElement backLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(backLink));
    }

    public List<WebElement> queueItems() {
        return driver.findElements(queueItem);
    }

    public WebElement selectedQueueItem() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(selectedQueueItem));
    }

    public WebElement orderCard() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(orderCard));
    }

    public WebElement confirmDispatchButton() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmDispatchButton));
    }

    public WebElement cancelSelectionButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(cancelSelectionButton));
    }

    public WebElement dispatchAnotherButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(dispatchAnotherButton));
    }

    public String successText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(successBox)).getText();
    }

    public String queueText() {
        waitForQueueLoaded();
        return queueItems().stream()
                .map(WebElement::getText)
                .reduce("", (left, right) -> left + "\n" + right);
    }

    public String orderCardText() {
        return orderCard().getText();
    }

    public String errorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    public boolean isInfoAlertVisible() {
        return driver.findElements(infoAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isOrderCardVisible() {
        return driver.findElements(orderCard).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isConfirmDispatchVisible() {
        return driver.findElements(confirmDispatchButton).stream().anyMatch(WebElement::isDisplayed);
    }

    public void selectQueueItemContaining(String text) {
        waitForQueueLoaded();
        WebElement item = wait.until(driver -> queueItems().stream()
                .filter(element -> element.getText().contains(text))
                .findFirst()
                .orElse(null));
        item.click();
    }

    public void waitForQueueItemContaining(String text) {
        waitForQueueLoaded();
        wait.until(driver -> queueItems().stream().anyMatch(element -> element.getText().contains(text)));
    }

    public void selectDeliverymanContaining(String text) {
        WebElement card = wait.until(driver -> driver.findElements(deliverymanCard).stream()
                .filter(element -> element.getText().contains(text))
                .findFirst()
                .orElse(null));
        card.click();
    }

    public void confirmDispatch() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmDispatchButton)).click();
    }
}
