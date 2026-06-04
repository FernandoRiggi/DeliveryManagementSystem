package br.ifsp.demo.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {

    private final WebDriverWait wait;

    private final By title = By.xpath("//h1[normalize-space()='Dashboard']");
    private final By logoutButton = By.xpath("//button[normalize-space()='Sair']");

    public DashboardPage(WebDriverWait wait) {
        this.wait = wait;
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    }

    public WebElement title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }
}
