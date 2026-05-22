package id.ac.ui.cs.advprog.beinventorycatalog.factory;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class ProductFactory {
    
    public Product createProduct(String name, String description, Double price, Integer stock, String jastiperId, String originCountry, LocalDate purchaseDate) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .jastiperId(jastiperId)
                .originCountry(originCountry)
                .purchaseDate(purchaseDate)
                .build();
    }
}
