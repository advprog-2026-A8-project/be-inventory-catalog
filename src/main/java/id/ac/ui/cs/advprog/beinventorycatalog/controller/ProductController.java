package id.ac.ui.cs.advprog.beinventorycatalog.controller;

import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.factory.ProductFactory;
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductFactory productFactory;

    @PostMapping("/create")
    @PreAuthorize("hasRole('JASTIPER')")
    public ResponseEntity<Product> createProduct(
            Authentication authentication,
            @Valid @RequestBody ProductDTO productDTO) {

        String userId = resolvePreferredUserId(authentication);

        Product product = productFactory.createProduct(
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getPrice(),
                productDTO.getStock(),
                userId,
                productDTO.getOriginCountry(),
                productDTO.getPurchaseDate());

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

        Set<String> identityCandidates = extractIdentityCandidates(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Product existing = productService.getProductById(id);

        if (!isAdmin && !identityCandidates.contains(existing.getJastiperId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(
            Authentication authentication,
            @PathVariable UUID id) {

        Set<String> identityCandidates = extractIdentityCandidates(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Product existing = productService.getProductById(id);

        if (!isAdmin && !identityCandidates.contains(existing.getJastiperId())) {
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
        Set<String> identityCandidates = extractIdentityCandidates(authentication);
        List<Product> products = identityCandidates.stream()
                .flatMap(identity -> productService.getProductsByJastiper(identity).stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Product::getId, product -> product, (left, right) -> left, java.util.LinkedHashMap::new),
                        map -> List.copyOf(map.values())
                ));
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

    private String resolvePreferredUserId(Authentication authentication) {
        if (authentication == null) {
            return "";
        }
        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> detailMap) {
            Object userId = detailMap.get("userId");
            if (userId instanceof String userIdString && !userIdString.isBlank()) {
                return userIdString;
            }
        }
        return authentication.getName();
    }

    private Set<String> extractIdentityCandidates(Authentication authentication) {
        Set<String> identities = new LinkedHashSet<>();
        if (authentication == null) {
            return identities;
        }
        addIfPresent(identities, authentication.getName());
        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> detailMap) {
            addIfPresent(identities, detailMap.get("userId"));
            addIfPresent(identities, detailMap.get("subject"));
        }
        return identities;
    }

    private void addIfPresent(Set<String> identities, Object candidate) {
        if (!(candidate instanceof String value)) {
            return;
        }
        String normalized = value.trim();
        if (!normalized.isEmpty()) {
            identities.add(normalized);
        }
    }
}
