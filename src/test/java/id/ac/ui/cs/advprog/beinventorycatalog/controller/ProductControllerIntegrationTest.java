package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product savedProduct;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        Product product = Product.builder()
                .name("Kipas Angin")
                .description("Kipas angin dinding")
                .price(150000.0)
                .stock(10)
                .originCountry("Indonesia")
                .purchaseDate(LocalDate.now())
                .jastiperId("jastiper-123")
                .build();
        savedProduct = productRepository.save(product);

        productDTO = new ProductDTO();
        productDTO.setName("Kipas Angin Baru");
        productDTO.setDescription("Deskripsi baru");
        productDTO.setPrice(160000.0);
        productDTO.setStock(5);
        productDTO.setOriginCountry("Indonesia");
        productDTO.setPurchaseDate(LocalDate.now());
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateProductSuccess() throws Exception {
        mockMvc.perform(post("/api/products/create")
                .header("X-User-Role", "JASTIPER")
                .header("X-User-Id", "jastiper-new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kipas Angin Baru"))
                .andExpect(jsonPath("$.jastiperId").value("jastiper-new"));
    }

    @Test
    void testCreateProductForbidden() throws Exception {
        mockMvc.perform(post("/api/products/create")
                .header("X-User-Role", "TITIPER")
                .header("X-User-Id", "titiper-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/api/products/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kipas Angin"));
    }

    @Test
    void testUpdateProductByOwner() throws Exception {
        mockMvc.perform(put("/api/products/update/" + savedProduct.getId())
                .header("X-User-Role", "JASTIPER")
                .header("X-User-Id", "jastiper-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kipas Angin Baru"));
    }

    @Test
    void testUpdateProductForbidden() throws Exception {
        mockMvc.perform(put("/api/products/update/" + savedProduct.getId())
                .header("X-User-Role", "JASTIPER")
                .header("X-User-Id", "other-jastiper")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testReserveStockSuccess() throws Exception {
        mockMvc.perform(post("/api/products/" + savedProduct.getId() + "/reserve")
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock reserved successfully"));

        Product updated = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(8, updated.getStock());
    }

    @Test
    void testReserveStockOversell() throws Exception {
        mockMvc.perform(post("/api/products/" + savedProduct.getId() + "/reserve")
                .param("quantity", "20"))
                .andExpect(status().isBadRequest());

        Product unchanged = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertEquals(10, unchanged.getStock());
    }

    @Test
    void testSearchByJastiperPublic() throws Exception {
        mockMvc.perform(get("/api/products/jastiper/jastiper-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jastiperId").value("jastiper-123"));
    }
}
