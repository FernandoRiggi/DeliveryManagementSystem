package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private final By title = By.xpath("//h1[normalize-space()='Criar conta']");
    private final By subtitle = By.xpath("//*[contains(normalize-space(), 'Preencha os dados abaixo')]");
    private final By nameInput = By.xpath("//label[normalize-space()='Nome']/following-sibling::input");
    private final By lastnameInput = By.xpath("//label[normalize-space()='Sobrenome']/following-sibling::input");
    private final By emailInput = By.cssSelector("input[type='email']");
    private final By passwordInput = By.cssSelector("input[type='password']");
    private final By submitButton = By.xpath("//button[normalize-space()='Criar conta']");
    private final By loginLink = By.linkText("Entrar");

    public RegisterPage(WebDriver driver, WebDriverWait wait, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = baseUrl;
    }

    public void open() {
        driver.get(baseUrl + "/register");
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        wait.until(ExpectedConditions.visibilityOfElementLocated(lastnameInput));
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

    public WebElement nameInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
    }

    public WebElement lastnameInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(lastnameInput));
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

    public WebElement loginLink() {
        return wait.until(ExpectedConditions.elementToBeClickable(loginLink));
    }

    public void submit() {
        submitButton().click();
    }

    public boolean isNameValid() {
        return isValid(nameInput());
    }

    public boolean isLastnameValid() {
        return isValid(lastnameInput());
    }

    public boolean isEmailValid() {
        return isValid(emailInput());
    }

    public boolean isPasswordValid() {
        return isValid(passwordInput());
    }

    public String nameValidationMessage() {
        return nameInput().getDomProperty("validationMessage");
    }

    private boolean isValid(WebElement element) {
        return (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", element);
    }
}
