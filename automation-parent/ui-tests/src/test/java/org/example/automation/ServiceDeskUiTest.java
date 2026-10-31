package org.example.automation;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ServiceDeskUiTest extends UiTestSupport {
    @Test void createsAssignsStartsAndResolvesTicket() {
        ServiceDeskTicketPage tickets = new ServiceDeskTicketPage(driver);
        String title = "Checkout error " + UUID.randomUUID();
        boolean created = false;
        try {
            tickets.create(title, "HIGH", "1");
            created = true;
            assertTrue(tickets.ticket(title).getText().contains("HIGH"));
            assertTrue(tickets.ticket(title).getText().contains("John Watson"));
            tickets.startWork(title);
            tickets.resolve(title);
            assertTrue(tickets.ticket(title).getText().contains("RESOLVED"));
        } finally {
            if (created) tickets.delete(title);
        }
    }

    @Test void filtersTicketsByPriority() {
        ServiceDeskTicketPage tickets = new ServiceDeskTicketPage(driver);
        String title = "Priority test " + UUID.randomUUID();
        boolean created = false;
        try {
            tickets.create(title, "HIGH", "1");
            created = true;
            tickets.filterPriority("LOW");
            assertFalse(tickets.visible(title));
            tickets.filterPriority("HIGH");
            assertTrue(tickets.ticket(title).isDisplayed());
        } finally {
            tickets.filterPriority("");
            if (created) tickets.delete(title);
        }
    }
}
