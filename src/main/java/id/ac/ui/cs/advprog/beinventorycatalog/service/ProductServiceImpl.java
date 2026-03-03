package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Penting! Biar Spring Boot tau ini adalah Manajer-nya
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository; // Manggil si kuli database

    @Override
    public Product createProduct(Product product) {
        // nyimpen data ke database
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        // ngambil semua data dari database
        return productRepository.findAll();
    }
}