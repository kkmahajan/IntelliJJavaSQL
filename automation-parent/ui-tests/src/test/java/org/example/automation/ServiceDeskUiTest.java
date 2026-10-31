package org.example.automation;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ServiceDeskUiTest extends UiTestSupport {
    private WebDriverWait waitFor() { return new WebDriverWait(driver, Duration.ofSeconds(10)); }
    private void type(String id, String value) {
        WebElement element = driver.findElement(By.id(id));
        element.clear();
        element.sendKeys(value);
    }
    private WebElement ticket(String title) {
        return waitFor().until(d -> d.findElements(By.cssSelector(".ticket-card")).stream()
                .filter(e -> e.getText().contains(title)).findFirst().orElse(null));
    }
    private void create(String title, String priority) {
        type("ticket-title", title);
        type("ticket-description", "Customer reports a checkout error");
        new Select(driver.findElement(By.id("ticket-priority"))).selectByValue(priority);
        new Select(driver.findElement(By.id("ticket-assignee"))).selectByValue("1");
        driver.findElement(By.id("ticket-save")).click();
        ticket(title);
    }
    private void remove(String title) {
        WebElement card = ticket(title);
        card.findElement(By.xpath(".//button[starts-with(@aria-label,'Delete ticket ')]")).click();
        waitFor().until(ExpectedConditions.alertIsPresent()).accept();
        waitFor().until(d -> d.findElements(By.cssSelector(".ticket-card")).stream()
                .noneMatch(e -> e.getText().contains(title)));
    }

    @Test void createsAssignsStartsAndResolvesTicket() {
        String title = "Checkout error " + UUID.randomUUID();
        boolean created = false;
        try {
            create(title, "HIGH");
            created = true;
            WebElement card = ticket(title);
            assertTrue(card.getText().contains("HIGH"));
            assertTrue(card.getText().contains("John Watson"));
            card.findElement(By.xpath(".//button[starts-with(@aria-label,'Start work ticket ')]")).click();
            waitFor().until(d -> ticket(title).getText().contains("IN PROGRESS"));
            ticket(title).findElement(By.xpath(".//button[starts-with(@aria-label,'Resolve ticket ')]")).click();
            waitFor().until(d -> ticket(title).getText().contains("RESOLVED"));
        } finally {
            if (created) remove(title);
        }
    }

    @Test void filtersTicketsByPriority() {
        String title = "Priority test " + UUID.randomUUID();
        boolean created = false;
        try {
            create(title, "HIGH");
            created = true;
            new Select(driver.findElement(By.id("ticket-priority-filter"))).selectByValue("LOW");
            assertTrue(driver.findElements(By.cssSelector(".ticket-card")).stream()
                    .noneMatch(e -> e.getText().contains(title)));
            new Select(driver.findElement(By.id("ticket-priority-filter"))).selectByValue("HIGH");
            assertTrue(ticket(title).isDisplayed());
        } finally {
            if (created) {
                new Select(driver.findElement(By.id("ticket-priority-filter"))).selectByValue("");
                remove(title);
            }
        }
    }
}
