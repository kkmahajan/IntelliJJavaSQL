package org.example.automation;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserManagerCrudUiTest extends UiTestSupport {
    @Test void createSearchEditAndDelete() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String original = "UI Test " + suffix;
        String updated = "UI Updated " + suffix;
        String email = "ui-" + suffix + "@example.com";
        boolean created = false;
        try {
            page.create(original, email);
            created = true;
            page.search(suffix);
            assertTrue(page.containsUser(original));
            page.edit(original, updated, email);
            assertFalse(page.containsUser(original));
            assertTrue(page.containsUser(updated));
            page.delete(updated);
            created = false;
            assertFalse(page.containsUser(updated));
        } finally {
            if (created) {
                page.search(suffix);
                if (page.containsUser(updated)) page.delete(updated);
                else if (page.containsUser(original)) page.delete(original);
            }
        }
    }

    @Test void duplicateEmailDisplaysError() {
        page.enterUser("Duplicate Test", "JOHN@example.com");
        page.save();
        page.waitForMessage("Request failed");
        assertFalse(page.containsUser("Duplicate Test"));
    }
}
