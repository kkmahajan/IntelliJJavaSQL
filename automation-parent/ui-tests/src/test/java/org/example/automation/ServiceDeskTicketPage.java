package org.example.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/** Reusable ticket-board Page Object; selectors stay outside test assertions. */
final class ServiceDeskTicketPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    ServiceDeskTicketPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.wait.ignoring(StaleElementReferenceException.class).ignoring(NoSuchElementException.class);
    }
    private void type(String id, String value) {
        WebElement input = driver.findElement(By.id(id));
        input.clear();
        input.sendKeys(value);
    }
    WebElement ticket(String title) {
        return wait.until(d -> d.findElements(By.cssSelector(".ticket-card")).stream()
                .filter(e -> e.getText().contains(title)).findFirst().orElse(null));
    }
    void create(String title, String priority, String assigneeId) {
        type("ticket-title", title);
        type("ticket-description", "Customer reports a checkout error");
        new Select(driver.findElement(By.id("ticket-priority"))).selectByValue(priority);
        wait.until(d -> new Select(d.findElement(By.id("ticket-assignee"))).getOptions().size() > 1);
        new Select(driver.findElement(By.id("ticket-assignee"))).selectByValue(assigneeId);
        driver.findElement(By.id("ticket-save")).click();
        ticket(title);
    }
    void startWork(String title) {
        ticket(title).findElement(By.xpath(".//button[starts-with(@aria-label,'Start work ticket ')]")).click();
        wait.until(d -> cardTextContains(title, "IN PROGRESS"));
    }
    void resolve(String title) {
        ticket(title).findElement(By.xpath(".//button[starts-with(@aria-label,'Resolve ticket ')]")).click();
        wait.until(d -> cardTextContains(title, "RESOLVED"));
    }
    void filterPriority(String priority) {
        new Select(driver.findElement(By.id("ticket-priority-filter"))).selectByValue(priority);
    }
    private boolean cardTextContains(String title, String expected) {
        return driver.findElements(By.cssSelector(".ticket-card")).stream()
                .anyMatch(card -> { String text = card.getText(); return text.contains(title) && text.contains(expected); });
    }
    boolean visible(String title) {
        return driver.findElements(By.cssSelector(".ticket-card")).stream().anyMatch(e -> e.getText().contains(title));
    }
    void delete(String title) {
        ticket(title).findElement(By.xpath(".//button[starts-with(@aria-label,'Delete ticket ')]")).click();
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        wait.until(d -> !visible(title));
    }
}
