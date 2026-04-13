package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO; // <--- Deklarasiin ini biar nggak "Cannot resolve symbol"
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        // Setup Product Model
        product = Product.builder()
                .id(testId)
                .name("Kipas Angin")
                .description("Kipas angin dinding")
                .price(150000.0)
                .stock(10)
                .jastiperId("jastiper-123")
                .build();

        // Setup Product DTO buat dipake di testUpdate
        productDTO = new ProductDTO();
        productDTO.setName("Kipas Angin Baru");
        productDTO.setDescription("Deskripsi baru");
        productDTO.setPrice(160000.0);
        productDTO.setStock(5);
        productDTO.setJastiperId("jastiper-123");
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.createProduct(product);

        assertNotNull(savedProduct);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testFindAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByIdSuccess() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(productRepository, times(1)).findById(testId);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(testId);
        });
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(testId, productDTO);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(testId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.findById(testId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(testId);

        verify(productRepository, times(1)).findById(testId);
        verify(productRepository, times(1)).delete(product);
    }
}