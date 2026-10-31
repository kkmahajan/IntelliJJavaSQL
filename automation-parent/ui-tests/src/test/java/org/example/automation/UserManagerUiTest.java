package org.example.automation;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class UserManagerUiTest {
    @Test
    void shouldSearchForSampleUser() {
        assumeTrue(Boolean.getBoolean("ui.tests"), "Run with -Dui.tests=true after starting the sample app");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(System.getProperty("app.baseUrl", "http://localhost:8080"));
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.findElement(By.id("users")).getText().contains("John Watson"));
            driver.findElement(By.id("search")).sendKeys("John");
            assertTrue(driver.findElement(By.id("users")).getText().contains("John Watson"));
            driver.findElement(By.id("search")).clear();
            driver.findElement(By.id("search")).sendKeys("nonexistent");
            assertTrue(driver.findElement(By.id("users")).getText().isBlank());
        } finally {
            driver.quit();
        }
    }
}
