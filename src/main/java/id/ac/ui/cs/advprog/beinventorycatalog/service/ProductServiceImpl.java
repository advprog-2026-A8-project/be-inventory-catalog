package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.beinventorycatalog.event.ProductCreatedEvent;
import id.ac.ui.cs.advprog.beinventorycatalog.strategy.StockValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StockValidationStrategy stockValidationStrategy;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        Product saved = productRepository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(saved));
        return saved;
    }

    @Override
    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Waduh, barang dengan ID ini nggak ketemu cuy!"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(UUID id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setOriginCountry(productDTO.getOriginCountry());
        existingProduct.setPurchaseDate(productDTO.getPurchaseDate());

        return productRepository.save(existingProduct);
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID id) {
        Product existingProduct = getProductById(id);
        productRepository.delete(existingProduct);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByJastiper(String jastiperId) {
        return productRepository.findByJastiperId(jastiperId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public boolean reserveStock(UUID id, int quantity) {
        if (!stockValidationStrategy.isValid(quantity)) return false;
        int updatedRows = productRepository.decrementStock(id, quantity);
        return updatedRows > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public boolean releaseStock(UUID id, int quantity) {
        if (!stockValidationStrategy.isValid(quantity)) return false;
        int updatedRows = productRepository.incrementStock(id, quantity);
        return updatedRows > 0;
    }
}
