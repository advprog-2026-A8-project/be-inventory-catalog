package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import java.time.LocalDate;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .name("Kipas Angin")
                .description("Kipas angin dinding")
                .price(150000.0)
                .stock(10)
                .originCountry("Indonesia")
                .purchaseDate(LocalDate.now())
                .jastiperId("jastiper-123")
                .build();

        productDTO = new ProductDTO();
        productDTO.setName("Kipas Angin");
        productDTO.setDescription("Kipas angin dinding");
        productDTO.setPrice(150000.0);
        productDTO.setStock(10);
        productDTO.setOriginCountry("Indonesia");
        productDTO.setPurchaseDate(LocalDate.now());
    }

    @Test
    void testCreateProduct() {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct("JASTIPER", "jastiper-123", productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).createProduct(any(Product.class));
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

    @Test
    void testGetProductById() {
        when(productService.getProductById(product.getId())).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductById(product.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).getProductById(product.getId());
    }

    @Test
    void testUpdateProduct() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct("JASTIPER", "jastiper-123", product.getId(), productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).updateProduct(eq(product.getId()), any(ProductDTO.class));
    }

    @Test
    void testDeleteProduct() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        doNothing().when(productService).deleteProduct(product.getId());

        ResponseEntity<String> response = productController.deleteProduct("JASTIPER", "jastiper-123", product.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Barang berhasil dihapus dari katalog!", response.getBody());
        verify(productService, times(1)).deleteProduct(product.getId());
    }

    @Test
    void testSearchProductsByName() {
        String searchKeyword = "Kipas";
        when(productService.searchProductsByName(searchKeyword)).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.searchProductsByName(searchKeyword);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product, response.getBody().getFirst());
        verify(productService, times(1)).searchProductsByName(searchKeyword);
    }

    @Test
    void testGetMyCatalog() {
        String jastiperId = "jastiper-123";
        when(productService.getProductsByJastiper(jastiperId)).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.getMyCatalog("JASTIPER", jastiperId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product, response.getBody().getFirst());
        verify(productService, times(1)).getProductsByJastiper(jastiperId);
    }

    @Test
    void testCreateProductForbidden() {
        ResponseEntity<Product> response = productController.createProduct("USER", "user-123", productDTO);
        assertEquals(403, response.getStatusCode().value());
        
        ResponseEntity<Product> response2 = productController.createProduct("JASTIPER", "", productDTO);
        assertEquals(403, response2.getStatusCode().value());
    }

    @Test
    void testUpdateProductUnauthorized() {
        ResponseEntity<Product> response = productController.updateProduct("JASTIPER", "", product.getId(), productDTO);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testUpdateProductForbidden() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        ResponseEntity<Product> response = productController.updateProduct("JASTIPER", "other-user", product.getId(), productDTO);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testUpdateProductAdminAllowed() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);
        ResponseEntity<Product> response = productController.updateProduct("ADMIN", "other-user", product.getId(), productDTO);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductUnauthorized() {
        ResponseEntity<String> response = productController.deleteProduct("JASTIPER", "", product.getId());
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductForbidden() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        ResponseEntity<String> response = productController.deleteProduct("JASTIPER", "other-user", product.getId());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductAdminAllowed() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        doNothing().when(productService).deleteProduct(product.getId());
        ResponseEntity<String> response = productController.deleteProduct("ADMIN", "other-user", product.getId());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetMyCatalogForbidden() {
        ResponseEntity<List<Product>> response = productController.getMyCatalog("USER", "user-123");
        assertEquals(403, response.getStatusCode().value());
        
        ResponseEntity<List<Product>> response2 = productController.getMyCatalog("JASTIPER", "");
        assertEquals(403, response2.getStatusCode().value());
    }
}