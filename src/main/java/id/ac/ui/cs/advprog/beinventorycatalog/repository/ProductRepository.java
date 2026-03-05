package id.ac.ui.cs.advprog.beinventorycatalog.repository;

import id.ac.ui.cs.advprog.beinventorycatalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {}