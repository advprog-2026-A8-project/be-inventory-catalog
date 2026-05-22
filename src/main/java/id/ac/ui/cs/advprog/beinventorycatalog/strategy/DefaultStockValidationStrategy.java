package id.ac.ui.cs.advprog.beinventorycatalog.strategy;

import org.springframework.stereotype.Component;

@Component
public class DefaultStockValidationStrategy implements StockValidationStrategy {
    @Override
    public boolean isValid(int quantity) {
        return quantity > 0;
    }
}
