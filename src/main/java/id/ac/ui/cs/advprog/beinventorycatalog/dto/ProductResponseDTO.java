package id.ac.ui.cs.advprog.beinventorycatalog.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class ProductResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String jastiperId;
    private String originCountry;
    private String purchaseDate;
}