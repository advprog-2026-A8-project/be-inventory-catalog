package id.ac.ui.cs.advprog.beinventorycatalog.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String jastiperId;
}