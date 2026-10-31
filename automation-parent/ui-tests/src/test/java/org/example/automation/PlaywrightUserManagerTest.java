package org.example.automation;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class PlaywrightUserManagerTest {
    @Test void createSearchEditDelete() {
        assumeTrue(Boolean.getBoolean("playwright.tests"), "Run with -Dplaywright.tests=true after installing Playwright Chromium");
        String baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080");
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String name = "PW Test " + suffix;
        String updated = "PW Updated " + suffix;
        String email = "pw-" + suffix + "@example.com";
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(Boolean.parseBoolean(System.getProperty("ui.headless", "true"))));
            try {
                Page page = browser.newPage();
                page.navigate(baseUrl);
                page.locator("#users").getByText("John Watson").waitFor();
                page.locator("#name").fill(name);
                page.locator("#email").fill(email);
                page.locator("#save").click();
                page.getByText("User created").waitFor();
                try {
                    page.locator("#search").fill(suffix);
                    assertTrue(page.locator("#users").innerText().contains(name));
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit " + name)).click();
                    page.locator("#name").fill(updated);
                    page.locator("#save").click();
                    page.getByText("User updated").waitFor();
                    assertTrue(page.locator("#users").innerText().contains(updated));
                } finally {
                    page.locator("#search").fill(suffix);
                    if (page.locator("button[aria-label='Delete " + updated + "']").count() > 0
                            || page.locator("button[aria-label='Delete " + name + "']").count() > 0) {
                        String deleteName = page.locator("button[aria-label='Delete " + updated + "']").count() > 0 ? updated : name;
                        page.onceDialog(Dialog::accept);
                        page.locator("button[aria-label='Delete " + deleteName + "']").click();
                        page.getByText("User deleted").waitFor();
                        assertFalse(page.locator("#users").innerText().contains(deleteName));
                    }
                }
            } catch (RuntimeException | AssertionError failure) {
                try {
                    Path folder = Path.of("target", "failure-artifacts");
                    Files.createDirectories(folder);
                    for (Page failedPage : browser.contexts().stream().flatMap(c -> c.pages().stream()).toList()) {
                        failedPage.screenshot(new Page.ScreenshotOptions().setPath(folder.resolve("playwright-createSearchEditDelete.png")));
                        break;
                    }
                } catch (Exception screenshotError) {
                    failure.addSuppressed(screenshotError);
                }
                throw failure;
            } finally {
                browser.close();
            }
        }
    }
}
