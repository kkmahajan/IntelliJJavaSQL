package org.example.automation.bdd;

import com.microsoft.playwright.*;
import io.cucumber.java.*;
import io.cucumber.java.en.*;
import io.qameta.allure.Allure;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PlaywrightSteps {
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private String name, updated, suffix, email;
    private boolean created;

    private void step(String text) { Allure.step(text); }

    @Before("@playwright")
    public void open() {
        assumeTrue(Boolean.getBoolean("playwright.tests"), "Playwright BDD runs in the dedicated CI stage");
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(Boolean.parseBoolean(System.getProperty("ui.headless", "true"))));
        page = browser.newPage();
        page.onRequest(request -> {
            if (request.url().contains("/api/users")) {
                Allure.step("Browser request: " + request.method() + " " + request.url());
            }
        });
        page.onResponse(response -> {
            if (response.url().contains("/api/users")) {
                Allure.step("Browser response: HTTP " + response.status() + " " + response.url());
            }
        });
        suffix = UUID.randomUUID().toString().substring(0, 8);
        name = "PW Test " + suffix;
        updated = "PW Updated " + suffix;
        email = "pw-" + suffix + "@example.com";
    }

    @Given("the Service Desk user management page is open")
    public void applicationOpen() {
        String url = System.getProperty("app.baseUrl", "http://localhost:8080");
        step("Navigate browser to " + url);
        page.navigate(url);
        page.locator("#users").getByText("John Watson").waitFor();
        step("Verified seeded user John Watson is displayed inside #users");
    }

    @When("I create a unique user")
    public void createUser() {
        step("Fill #name with " + name);
        page.locator("#name").fill(name);
        step("Fill #email with " + email);
        page.locator("#email").fill(email);
        step("Click #save to create user");
        page.locator("#save").click();
        page.getByText("User created").waitFor();
        created = true;
        step("Verified UI confirmation: User created");
    }

    @When("I search for the created user")
    public void search() {
        step("Fill #search with unique suffix " + suffix);
        page.locator("#search").fill(suffix);
        assertTrue(page.locator("#users").innerText().contains(name));
        step("Asserted #users contains " + name);
    }

    @When("I edit the user's name")
    public void edit() {
        step("Click Edit " + name);
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Edit " + name)).click();
        step("Fill #name with " + updated);
        page.locator("#name").fill(updated);
        step("Click #save to update user");
        page.locator("#save").click();
        page.getByText("User updated").waitFor();
        step("Verified UI confirmation: User updated");
    }

    @Then("the updated user is displayed")
    public void updatedDisplayed() {
        assertTrue(page.locator("#users").innerText().contains(updated));
        step("Asserted #users contains updated name " + updated);
    }

    @When("I delete the user")
    public void delete() {
        page.locator("#search").fill(suffix);
        step("Click Delete " + updated + " and accept confirmation");
        page.onceDialog(Dialog::accept);
        page.locator("button[aria-label='Delete " + updated + "']").click();
        page.getByText("User deleted").waitFor();
        created = false;
        step("Verified UI confirmation: User deleted");
    }

    @Then("the deleted user is absent")
    public void absent() {
        assertFalse(page.locator("#users").innerText().contains(updated));
        step("Asserted deleted user is absent from #users");
        attachScreenshot("Final user list");
    }

    private void attachScreenshot(String label) {
        if (page != null) {
            try {
                Allure.addAttachment(label, "image/png",
                        new java.io.ByteArrayInputStream(page.screenshot()), ".png");
            } catch (RuntimeException ignored) { /* Preserve original test failure. */ }
        }
    }

    @After("@playwright")
    public void close(Scenario scenario) {
        if (page != null) {
            if (scenario.isFailed()) attachScreenshot("Failure screenshot");
            if (created) {
                try {
                    page.locator("#search").fill(suffix);
                    String candidate = page.locator("button[aria-label='Delete " + updated + "']").count() > 0 ? updated : name;
                    if (page.locator("button[aria-label='Delete " + candidate + "']").count() > 0) {
                        page.onceDialog(Dialog::accept);
                        page.locator("button[aria-label='Delete " + candidate + "']").click();
                        step("Cleanup: deleted test user " + candidate);
                    }
                } catch (RuntimeException cleanupError) {
                    scenario.log("Cleanup failed: " + cleanupError.getMessage());
                }
            }
        }
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
