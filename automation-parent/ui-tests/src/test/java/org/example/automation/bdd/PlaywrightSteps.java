package org.example.automation.bdd;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.*;
import io.cucumber.java.en.*;
import io.qameta.allure.Allure;
import java.util.UUID;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PlaywrightSteps {
    private Playwright playwright;
    private Browser browser;
    private Page page;
    private String name, updated, suffix, email;
    private boolean created;

    /**
     * Runs the action INSIDE an Allure step so the step's status reflects what
     * happened. A bare Allure.step("text") is recorded as passed immediately,
     * before the action runs, so a failing action would still show a green step.
     */
    private void step(String text, Runnable action) {
        Allure.step(text, () -> { action.run(); });
    }

    /** Narration only, for facts that are already known to have happened. */
    private void note(String text) { Allure.step(text); }

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
        step("Navigate browser to " + url, () -> page.navigate(url));
        step("Verify seeded user John Watson is displayed inside #users",
                () -> page.locator("#users").getByText("John Watson").waitFor());
    }

    @When("I create a unique user")
    public void createUser() {
        step("Fill #name with " + name, () -> page.locator("#name").fill(name));
        step("Fill #email with " + email, () -> page.locator("#email").fill(email));
        step("Click #save to create user", () -> page.locator("#save").click());
        // The request was sent, so the user may exist even if the confirmation
        // below never appears. Mark it for cleanup before verifying.
        created = true;
        step("Verify UI confirmation: User created", () -> page.getByText("User created").waitFor());
    }

    @When("I search for the created user")
    public void search() {
        step("Fill #search with unique suffix " + suffix, () -> page.locator("#search").fill(suffix));
        // assertThat(...) retries until the condition holds or times out;
        // innerText() would be a one-time snapshot and could be flaky.
        step("Verify #users contains " + name, () -> assertThat(page.locator("#users")).containsText(name));
    }

    @When("I edit the user's name")
    public void edit() {
        step("Click Edit " + name, () -> page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Edit " + name)).click());
        step("Fill #name with " + updated, () -> page.locator("#name").fill(updated));
        step("Click #save to update user", () -> page.locator("#save").click());
        step("Verify UI confirmation: User updated", () -> page.getByText("User updated").waitFor());
    }

    @Then("the updated user is displayed")
    public void updatedDisplayed() {
        step("Verify #users contains updated name " + updated,
                () -> assertThat(page.locator("#users")).containsText(updated));
    }

    @When("I delete the user")
    public void delete() {
        step("Fill #search with unique suffix " + suffix, () -> page.locator("#search").fill(suffix));
        step("Click Delete " + updated + " and accept confirmation", () -> {
            page.onceDialog(Dialog::accept);
            page.locator("button[aria-label='Delete " + updated + "']").click();
        });
        step("Verify UI confirmation: User deleted", () -> page.getByText("User deleted").waitFor());
        created = false;
    }

    @Then("the deleted user is absent")
    public void absent() {
        // .not() retries until the text is gone, so this cannot pass before the
        // list has re-rendered.
        step("Verify deleted user is absent from #users",
                () -> assertThat(page.locator("#users")).not().containsText(updated));
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
                        note("Cleanup: deleted test user " + candidate);
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
