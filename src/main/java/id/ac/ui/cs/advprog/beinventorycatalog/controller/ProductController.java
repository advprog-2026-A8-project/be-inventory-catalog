package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('JASTIPER')")
    public ResponseEntity<Product> createProduct(
            Authentication authentication,
            @Valid @RequestBody ProductDTO productDTO) {

        String userId = authentication.getName();

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
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(
            Authentication authentication,
            @PathVariable UUID id, 
            @Valid @RequestBody ProductDTO productDTO) {

        String userId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Product existing = productService.getProductById(id);

        if (!isAdmin && !existing.getJastiperId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(
            Authentication authentication,
            @PathVariable UUID id) {

        String userId = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Product existing = productService.getProductById(id);

        if (!isAdmin && !existing.getJastiperId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        productService.deleteProduct(id);
        return ResponseEntity.ok("Barang berhasil dihapus dari katalog!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/my-catalog")
    @PreAuthorize("hasRole('JASTIPER')")
    public ResponseEntity<List<Product>> getMyCatalog(
            Authentication authentication) {
        
        String userId = authentication.getName();

        List<Product> products = productService.getProductsByJastiper(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/jastiper/{jastiperId}")
    public ResponseEntity<List<Product>> getProductsByJastiper(@PathVariable String jastiperId) {
        List<Product> products = productService.getProductsByJastiper(jastiperId);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<String> reserveStock(@PathVariable UUID id, @RequestParam int quantity) {
        boolean success = productService.reserveStock(id, quantity);
        if (success) {
            return ResponseEntity.ok("Stock reserved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reserve stock. Insufficient stock or invalid quantity.");
        }
    }

    @PostMapping("/{id}/release")
    public ResponseEntity<String> releaseStock(@PathVariable UUID id, @RequestParam int quantity) {
        boolean success = productService.releaseStock(id, quantity);
        if (success) {
            return ResponseEntity.ok("Stock released successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to release stock. Invalid quantity.");
        }
    }
}