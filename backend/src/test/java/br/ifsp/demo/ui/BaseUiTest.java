package br.ifsp.demo.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

abstract class BaseUiTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = System.getProperty("ui.baseUrl", "http://localhost:5173");

        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("ui.headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,768");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
