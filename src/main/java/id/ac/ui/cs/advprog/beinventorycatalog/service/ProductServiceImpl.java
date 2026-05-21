package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Waduh, barang dengan ID ini nggak ketemu cuy!"));
    }

    @Override
    @Transactional
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
    public boolean reserveStock(UUID id, int quantity) {
        if (quantity <= 0) return false;
        int updatedRows = productRepository.decrementStock(id, quantity);
        return updatedRows > 0;
    }

    @Override
    @Transactional
    public boolean releaseStock(UUID id, int quantity) {
        if (quantity <= 0) return false;
        int updatedRows = productRepository.incrementStock(id, quantity);
        return updatedRows > 0;
    }
}