package id.ac.ui.cs.advprog.beinventorycatalog.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DefaultStockValidationStrategyTest {
    @Test
    void testIsValid() {
        DefaultStockValidationStrategy strategy = new DefaultStockValidationStrategy();
        assertTrue(strategy.isValid(5));
        assertFalse(strategy.isValid(0));
        assertFalse(strategy.isValid(-5));
    }
}
