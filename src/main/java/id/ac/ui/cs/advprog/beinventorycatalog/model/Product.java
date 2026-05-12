package id.ac.ui.cs.advprog.beinventorycatalog.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_jastiper_id", columnList = "jastiper_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "jastiper_id", nullable = false)
    private String jastiperId;

    @Column(name = "origin_country", nullable = false)
    private String originCountry;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;
}