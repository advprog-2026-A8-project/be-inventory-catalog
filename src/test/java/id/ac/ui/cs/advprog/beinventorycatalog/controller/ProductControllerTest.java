package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductResponseDTO;
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
                .purchaseDate("2026-05-05")
                .jastiperId("jastiper-123")
                .build();

        productDTO = new ProductDTO();
        productDTO.setName("Kipas Angin");
        productDTO.setDescription("Kipas angin dinding");
        productDTO.setPrice(150000.0);
        productDTO.setStock(10);
        productDTO.setOriginCountry("Indonesia");
        productDTO.setPurchaseDate("2026-05-05");
    }

    @Test
    void testCreateProduct() {
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        ResponseEntity<?> response = productController.createProduct("jastiper-123", "JASTIPER", productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        ProductResponseDTO body = (ProductResponseDTO) response.getBody();
        assertNotNull(body);
        assertEquals(product.getId(), body.getId());
        assertEquals(product.getName(), body.getName());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testGetAllProducts() {
        when(productService.getAllProducts()).thenReturn(List.of(product));
        ResponseEntity<List<ProductResponseDTO>> response = productController.getAllProducts();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() {
        when(productService.getProductById(product.getId())).thenReturn(product);
        ResponseEntity<ProductResponseDTO> response = productController.getProductById(product.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).getProductById(product.getId());
    }

    @Test
    void testUpdateProduct() {
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);

        ResponseEntity<?> response = productController.updateProduct(product.getId(), "jastiper-123", "JASTIPER", productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        ProductResponseDTO body = (ProductResponseDTO) response.getBody();
        assertNotNull(body);
        assertEquals(product.getId(), body.getId());
        verify(productService, times(1)).updateProduct(eq(product.getId()), any(ProductDTO.class));
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productService).deleteProduct(product.getId());
        ResponseEntity<?> response = productController.deleteProduct(product.getId(), "ADMIN");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).deleteProduct(product.getId());
    }

    @Test
    void testSearchProductsByName() {
        when(productService.searchProductsByName("Kipas")).thenReturn(List.of(product));
        ResponseEntity<List<ProductResponseDTO>> response = productController.searchProductsByName("Kipas");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).searchProductsByName("Kipas");
    }

    @Test
    void testGetMyCatalog() {
        when(productService.getProductsByJastiper("jastiper-123")).thenReturn(List.of(product));
        ResponseEntity<?> response = productController.getMyCatalog("jastiper-123", "JASTIPER");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).getProductsByJastiper("jastiper-123");
    }

    @Test
    void testMonitorProductsAdmin() {
        when(productService.getAllProducts()).thenReturn(List.of(product));
        ResponseEntity<?> response = productController.monitorProductsAdmin("ADMIN");
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).getAllProducts();
    }
}