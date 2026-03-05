package id.ac.ui.cs.advprog.beinventorycatalog.model;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductTest {

    @Test
    void testProductBuilder() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder()
                .id(id)
                .name("Kemeja Flanel")
                .description("Ukuran L")
                .price(200000.0)
                .stock(5)
                .jastiperId("jastiper-456")
                .build();

        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals("Kemeja Flanel", product.getName());
        assertEquals("Ukuran L", product.getDescription());
        assertEquals(200000.0, product.getPrice());
        assertEquals(5, product.getStock());
        assertEquals("jastiper-456", product.getJastiperId());
    }

    @Test
    void testProductSetters() {
        Product product = new Product();
        UUID id = UUID.randomUUID();

        product.setId(id);
        product.setName("Celana Jeans");
        product.setDescription("Warna Biru");
        product.setPrice(300000.0);
        product.setStock(15);
        product.setJastiperId("jastiper-789");

        assertEquals(id, product.getId());
        assertEquals("Celana Jeans", product.getName());
        assertEquals("Warna Biru", product.getDescription());
        assertEquals(300000.0, product.getPrice());
        assertEquals(15, product.getStock());
        assertEquals("jastiper-789", product.getJastiperId());
    }
}