package org.example.automation;

import io.qameta.allure.Allure;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

final class UserManagerPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    UserManagerPage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    /**
     * Runs the action INSIDE an Allure step so the step's status reflects what
     * happened. A bare Allure.step("text") is recorded as passed immediately,
     * before the action runs, so a failing action would still show a green step.
     */
    private static void step(String text, Runnable action) {
        Allure.step(text, () -> { action.run(); });
    }

    void open(String baseUrl) {
        step("Navigate to " + baseUrl, () -> driver.get(baseUrl));
        step("Wait for seeded user to be listed",
                () -> wait.until(d -> d.findElement(By.id("users")).getText().contains("John Watson")));
    }

    void search(String query) {
        WebElement field = driver.findElement(By.id("search"));
        field.clear();
        step("Enter search query: " + query, () -> field.sendKeys(query));
    }

    boolean containsUser(String name) {
        return driver.findElement(By.id("users")).getText().contains(name);
    }

    void waitForUser(String name) {
        wait.until(d -> containsUser(name));
    }

    void waitForUserAbsent(String name) {
        wait.until(d -> !containsUser(name));
    }

    void enterUser(String name, String email) {
        WebElement nameField = driver.findElement(By.id("name"));
        WebElement emailField = driver.findElement(By.id("email"));
        nameField.clear();
        step("Fill #name: " + name, () -> nameField.sendKeys(name));
        emailField.clear();
        step("Fill #email: " + email, () -> emailField.sendKeys(email));
    }

    void save() {
        step("Click Save button (#save)", () -> driver.findElement(By.id("save")).click());
    }

    void create(String name, String email) {
        enterUser(name, email);
        save();
        waitForMessage("User created");
        waitForUser(name);
    }

    void edit(String existingName, String newName, String email) {
        driver.findElement(By.cssSelector("button[aria-label='Edit " + existingName + "']")).click();
        wait.until(d -> d.findElement(By.id("save")).getText().equals("Save changes"));
        enterUser(newName, email);
        save();
        waitForMessage("User updated");
        waitForUser(newName);
    }

    void delete(String name) {
        driver.findElement(By.cssSelector("button[aria-label='Delete " + name + "']")).click();
        Alert confirmation = wait.until(ExpectedConditions.alertIsPresent());
        step("Accept delete confirmation", confirmation::accept);
        waitForMessage("User deleted");
        waitForUserAbsent(name);
    }

    void waitForMessage(String message) {
        step("Verify UI message: " + message,
                () -> wait.until(d -> d.findElement(By.id("message")).getText().contains(message)));
    }

    void waitForAnyMessage() {
        wait.until(d -> !d.findElement(By.id("message")).getText().isBlank());
    }

    String message() {
        return driver.findElement(By.id("message")).getText();
    }
}
