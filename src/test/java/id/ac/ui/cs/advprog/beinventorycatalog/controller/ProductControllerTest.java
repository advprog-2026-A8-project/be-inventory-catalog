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
                .jastiperId("jastiper-123")
                .build();

        productDTO = new ProductDTO();
        productDTO.setName("Kipas Angin");
        productDTO.setDescription("Kipas angin dinding");
        productDTO.setPrice(150000.0);
        productDTO.setStock(10);
        productDTO.setJastiperId("jastiper-123");
    }

    @Test
    void testCreateProduct() {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<ProductResponseDTO> response = productController.createProduct(productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(product.getPrice(), response.getBody().getPrice());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(product);
        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<ProductResponseDTO>> response = productController.getAllProducts();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getName(), response.getBody().get(0).getName());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() {
        when(productService.getProductById(product.getId())).thenReturn(product);

        ResponseEntity<ProductResponseDTO> response = productController.getProductById(product.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
        verify(productService, times(1)).getProductById(product.getId());
    }

    @Test
    void testUpdateProduct() {
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);

        ResponseEntity<ProductResponseDTO> response = productController.updateProduct(product.getId(), productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(product.getPrice(), response.getBody().getPrice());
        verify(productService, times(1)).updateProduct(eq(product.getId()), any(ProductDTO.class));
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productService).deleteProduct(product.getId());

        ResponseEntity<String> response = productController.deleteProduct(product.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Barang berhasil dihapus dari katalog!", response.getBody());
        verify(productService, times(1)).deleteProduct(product.getId());
    }

    @Test
    void testSearchProductsByName() {
        String searchKeyword = "Kipas";
        when(productService.searchProductsByName(searchKeyword)).thenReturn(List.of(product));

        ResponseEntity<List<ProductResponseDTO>> response = productController.searchProductsByName(searchKeyword);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getName(), response.getBody().get(0).getName());
        verify(productService, times(1)).searchProductsByName(searchKeyword);
    }

    @Test
    void testGetProductsByJastiper() {
        String jastiperId = "jastiper-123";
        when(productService.getProductsByJastiper(jastiperId)).thenReturn(List.of(product));

        ResponseEntity<List<ProductResponseDTO>> response = productController.getProductsByJastiper(jastiperId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getJastiperId(), response.getBody().get(0).getJastiperId());
        verify(productService, times(1)).getProductsByJastiper(jastiperId);
    }

    @Test
    void testMonitorProductsAdmin() {
        // Setup data bohongan
        List<Product> products = List.of(product);
        when(productService.getAllProducts()).thenReturn(products);

        // Panggil endpoint adminnya
        ResponseEntity<List<ProductResponseDTO>> response = productController.monitorProductsAdmin();

        // Validasi
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product.getId(), response.getBody().get(0).getId());
        assertEquals(product.getName(), response.getBody().get(0).getName());
        verify(productService, times(1)).getAllProducts();
    }
}