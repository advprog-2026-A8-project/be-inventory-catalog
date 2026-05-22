package id.ac.ui.cs.advprog.beinventorycatalog.factory;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ProductFactoryTest {
    @Test
    void testCreateProduct() {
        ProductFactory factory = new ProductFactory();
        Product product = factory.createProduct("Name", "Desc", 100.0, 10, "jastiper1", "Indonesia", LocalDate.now());
        assertEquals("Name", product.getName());
        assertEquals(10, product.getStock());
    }
}
