package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductResponseDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private ProductResponseDTO convertToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .jastiperId(product.getJastiperId())
                .originCountry(product.getOriginCountry())
                .purchaseDate(product.getPurchaseDate())
                .build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody ProductDTO productDTO) {

        if (!"JASTIPER".equalsIgnoreCase(role) || userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: Hanya Jastiper yang dapat menambah produk.");
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .originCountry(productDTO.getOriginCountry())
                .purchaseDate(productDTO.getPurchaseDate())
                .jastiperId(userId)
                .build();

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(convertToResponseDTO(savedProduct));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products.stream().map(this::convertToResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(convertToResponseDTO(productService.getProductById(id)));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody ProductDTO productDTO) {

        if (!"JASTIPER".equalsIgnoreCase(role) || userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }

        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(convertToResponseDTO(updatedProduct));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        // FIX MEDIUM 2: Admin punya kuasa buat hapus barang fraud
        if (!"JASTIPER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }

        productService.deleteProduct(id);
        return ResponseEntity.ok("Barang berhasil dihapus!");
    }

    @GetMapping("/my-catalog")
    public ResponseEntity<?> getMyCatalog(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"JASTIPER".equalsIgnoreCase(role) || userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak.");
        }

        List<Product> products = productService.getProductsByJastiper(userId);
        return ResponseEntity.ok(products.stream().map(this::convertToResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products.stream().map(this::convertToResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/admin/monitor")
    public ResponseEntity<?> monitorProductsAdmin(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Akses ditolak: Hanya Admin.");
        }

        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products.stream().map(this::convertToResponseDTO).collect(Collectors.toList()));
    }
}