package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
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
}
