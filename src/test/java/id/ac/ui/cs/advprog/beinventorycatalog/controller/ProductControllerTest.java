package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Kipas Angin")
                .description("Kipas angin dinding")
                .price(150000.0)
                .stock(10)
                .jastiperId("jastiper-123")
                .build();
    }

    @Test
    void testCreateProduct() {
        when(productService.createProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).createProduct(product);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(product);
        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product, response.getBody().getFirst());
        verify(productService, times(1)).getAllProducts();
    }
}