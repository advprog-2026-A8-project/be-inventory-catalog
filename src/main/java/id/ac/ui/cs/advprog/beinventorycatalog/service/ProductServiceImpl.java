package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Import baru buat ngunci transaksi

import java.util.List;
import java.util.UUID;

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
    public Product updateProduct(UUID id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());

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
    public Product reduceProductStock(UUID id, int quantity) {
        Product product = productRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalArgumentException("Waduh, barang dengan ID ini nggak ketemu cuy!"));

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Waduh, stok nggak cukup! Sisa stok: " + product.getStock());
        }

        product.setStock(product.getStock() - quantity);
        return productRepository.save(product);
    }
}