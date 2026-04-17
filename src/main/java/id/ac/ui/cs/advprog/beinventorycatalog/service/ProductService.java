package id.ac.ui.cs.advprog.beinventorycatalog.service;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import id.ac.ui.cs.advprog.beinventorycatalog.dto.ProductDTO;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(UUID id);
    Product updateProduct(UUID id, ProductDTO productDTO);
    void deleteProduct(UUID id);
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByJastiper(String jastiperId);
}