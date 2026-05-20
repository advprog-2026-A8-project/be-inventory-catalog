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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private Authentication authentication;

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
        when(authentication.getName()).thenReturn("jastiper-123");
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(authentication, productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProductUsesUserIdFromAuthenticationDetails() {
        when(authentication.getDetails()).thenReturn(Map.of(
                "userId", "11111111-1111-1111-1111-111111111111",
                "subject", "jastiper@example.com"
        ));
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.createProduct(authentication, productDTO);

        assertEquals(200, response.getStatusCode().value());
        ArgumentCaptor<Product> captor = forClass(Product.class);
        verify(productService).createProduct(captor.capture());
        assertEquals("11111111-1111-1111-1111-111111111111", captor.getValue().getJastiperId());
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
        when(authentication.getName()).thenReturn("jastiper-123");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);

        ResponseEntity<Product> response = productController.updateProduct(authentication, product.getId(), productDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).updateProduct(eq(product.getId()), any(ProductDTO.class));
    }

    @Test
    void testDeleteProduct() {
        when(authentication.getName()).thenReturn("jastiper-123");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        doNothing().when(productService).deleteProduct(product.getId());

        ResponseEntity<String> response = productController.deleteProduct(authentication, product.getId());

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
        when(authentication.getName()).thenReturn(jastiperId);
        when(productService.getProductsByJastiper(jastiperId)).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.getMyCatalog(authentication);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(product, response.getBody().getFirst());
        verify(productService, times(1)).getProductsByJastiper(jastiperId);
    }

    @Test
    void testGetMyCatalogAggregatesIdentityCandidatesAndDeduplicates() {
        Product secondProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Payung")
                .description("Payung lipat")
                .price(50000.0)
                .stock(4)
                .originCountry("Indonesia")
                .purchaseDate(LocalDate.now())
                .jastiperId("jastiper@example.com")
                .build();

        when(authentication.getName()).thenReturn("jastiper@example.com");
        when(authentication.getDetails()).thenReturn(Map.of(
                "userId", "11111111-1111-1111-1111-111111111111",
                "subject", "jastiper@example.com"
        ));
        when(productService.getProductsByJastiper("jastiper@example.com")).thenReturn(List.of(product, secondProduct));
        when(productService.getProductsByJastiper("11111111-1111-1111-1111-111111111111")).thenReturn(List.of(product));

        ResponseEntity<List<Product>> response = productController.getMyCatalog(authentication);

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(productService).getProductsByJastiper("jastiper@example.com");
        verify(productService).getProductsByJastiper("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void testUpdateProductForbidden() {
        when(authentication.getName()).thenReturn("other-user");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        
        ResponseEntity<Product> response = productController.updateProduct(authentication, product.getId(), productDTO);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testUpdateProductAllowedWhenOwnerMatchesSubjectCandidate() {
        Product emailOwnedProduct = Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .originCountry(product.getOriginCountry())
                .purchaseDate(product.getPurchaseDate())
                .jastiperId("jastiper@example.com")
                .build();

        when(authentication.getName()).thenReturn("11111111-1111-1111-1111-111111111111");
        when(authentication.getDetails()).thenReturn(Map.of(
                "userId", "11111111-1111-1111-1111-111111111111",
                "subject", "jastiper@example.com"
        ));
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(emailOwnedProduct);
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(emailOwnedProduct);

        ResponseEntity<Product> response = productController.updateProduct(authentication, product.getId(), productDTO);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateProductAdminAllowed() {
        when(authentication.getName()).thenReturn("admin-123");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        when(productService.updateProduct(eq(product.getId()), any(ProductDTO.class))).thenReturn(product);
        
        ResponseEntity<Product> response = productController.updateProduct(authentication, product.getId(), productDTO);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductForbidden() {
        when(authentication.getName()).thenReturn("other-user");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        
        ResponseEntity<String> response = productController.deleteProduct(authentication, product.getId());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductAdminAllowed() {
        when(authentication.getName()).thenReturn("admin-123");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(product);
        doNothing().when(productService).deleteProduct(product.getId());
        
        ResponseEntity<String> response = productController.deleteProduct(authentication, product.getId());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteProductAllowedWhenOwnerMatchesUserIdCandidate() {
        Product uuidOwnedProduct = Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .originCountry(product.getOriginCountry())
                .purchaseDate(product.getPurchaseDate())
                .jastiperId("11111111-1111-1111-1111-111111111111")
                .build();

        when(authentication.getName()).thenReturn("jastiper@example.com");
        when(authentication.getDetails()).thenReturn(Map.of(
                "userId", "11111111-1111-1111-1111-111111111111",
                "subject", "jastiper@example.com"
        ));
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_JASTIPER"))).when(authentication).getAuthorities();
        when(productService.getProductById(product.getId())).thenReturn(uuidOwnedProduct);
        doNothing().when(productService).deleteProduct(product.getId());

        ResponseEntity<String> response = productController.deleteProduct(authentication, product.getId());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testGetProductsByJastiperPublic() {
        String jastiperId = "jastiper-123";
        when(productService.getProductsByJastiper(jastiperId)).thenReturn(List.of(product));
        ResponseEntity<List<Product>> response = productController.getProductsByJastiper(jastiperId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productService, times(1)).getProductsByJastiper(jastiperId);
    }

    @Test
    void testReserveStockSuccess() {
        when(productService.reserveStock(product.getId(), 2)).thenReturn(true);
        ResponseEntity<String> response = productController.reserveStock(product.getId(), 2);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Stock reserved successfully", response.getBody());
    }

    @Test
    void testReserveStockFailure() {
        when(productService.reserveStock(product.getId(), 20)).thenReturn(false);
        ResponseEntity<String> response = productController.reserveStock(product.getId(), 20);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testReleaseStockSuccess() {
        when(productService.releaseStock(product.getId(), 2)).thenReturn(true);
        ResponseEntity<String> response = productController.releaseStock(product.getId(), 2);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Stock released successfully", response.getBody());
    }

    @Test
    void testReleaseStockFailure() {
        when(productService.releaseStock(product.getId(), 20)).thenReturn(false);
        ResponseEntity<String> response = productController.releaseStock(product.getId(), 20);
        assertEquals(400, response.getStatusCode().value());
    }
}
