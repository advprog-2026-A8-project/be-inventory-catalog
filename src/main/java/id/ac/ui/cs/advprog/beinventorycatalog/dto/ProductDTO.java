package id.ac.ui.cs.advprog.beinventorycatalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {

    @NotBlank(message = "Nama produk tidak boleh kosong")
    private String name;

    @NotBlank(message = "Deskripsi tidak boleh kosong")
    private String description;

    @NotNull(message = "Harga tidak boleh kosong")
    @Min(value = 0, message = "Harga tidak boleh negatif")
    private Double price;

    @NotNull(message = "Stok tidak boleh kosong")
    @Min(value = 0, message = "Stok tidak boleh negatif")
    private Integer stock;

    @NotBlank(message = "Jastiper ID tidak boleh kosong")
    private String jastiperId;
}