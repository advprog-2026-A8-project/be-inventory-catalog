package id.ac.ui.cs.advprog.beinventorycatalog.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductCreatedEventTest {
    @Test
    void testEventCreation() {
        Object source = new Object();
        ProductCreatedEvent event = new ProductCreatedEvent(source);
        assertEquals(source, event.getSource());
    }

    @Test
    void testEventListener() {
        ProductEventListener listener = new ProductEventListener();
        ProductCreatedEvent event = new ProductCreatedEvent("Test");
        assertDoesNotThrow(() -> listener.handleProductCreatedEvent(event));
    }
}
