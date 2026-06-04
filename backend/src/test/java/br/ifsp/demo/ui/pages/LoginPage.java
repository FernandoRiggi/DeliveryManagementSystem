package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Entrar']");
    private final By subtitle = By.xpath("//*[contains(normalize-space(), 'gerenciamento de entregas')]");
    private final By emailInput = By.cssSelector("input[type='email']");
    private final By passwordInput = By.cssSelector("input[type='password']");
    private final By submitButton = By.xpath("//button[normalize-space()='Entrar']");
    private final By registerLink = By.linkText("Cadastrar");
    private final By errorAlert = By.cssSelector(".alert-danger");

    public LoginPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/login");
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public WebElement subtitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(subtitle));
    }

    public WebElement emailInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    public WebElement passwordInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
    }

    public WebElement submitButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    public WebElement registerLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(registerLink));
    }

    public void submit() {
        submitButton().click();
    }

    public void fillEmail(String email) {
        emailInput().clear();
        emailInput().sendKeys(email);
    }

    public void fillPassword(String password) {
        passwordInput().clear();
        passwordInput().sendKeys(password);
    }

    public void login(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        submit();
    }

    public boolean isEmailValid() {
        return isValid(emailInput());
    }

    public boolean isPasswordValid() {
        return isValid(passwordInput());
    }

    public String emailValidationMessage() {
        return emailInput().getDomProperty("validationMessage");
    }

    public String passwordValidationMessage() {
        return passwordInput().getDomProperty("validationMessage");
    }

    public boolean isErrorAlertVisible() {
        return driver.findElements(errorAlert).stream().anyMatch(WebElement::isDisplayed);
    }

    public String waitForErrorAlertText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).getText();
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
