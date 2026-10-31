package org.example.automation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

abstract class UiTestSupport {
    protected WebDriver driver;
    protected UserManagerPage page;

    @BeforeEach void openBrowser() {
        assumeTrue(Boolean.getBoolean("ui.tests"), "Run with -Dui.tests=true and the sample app running");
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("ui.headless", "true"))) options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        page = new UserManagerPage(driver, Duration.ofSeconds(10));
        page.open(System.getProperty("app.baseUrl", "http://localhost:8080"));
    }

    @AfterEach void closeBrowser() {
        if (driver != null) driver.quit();
    }
}
