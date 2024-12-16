package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Schema(description = "Represents a product available for rental")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the product.", example = "LED Light")
    @NotBlank
    @Size(max = 100)
    private String name;

    @Schema(description = "Category of the product.", example = "Lighting")
    @NotNull(message = "Category is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Schema(description = "Cost per day.", example = "50.0")
    @NotNull(message = "Price is required")
    private double dailyRentalPrice;

    @Schema(description = "Stock quantity available.", example = "10")
    @NotNull(message = "Quantity is required")
    @Min(0)// can't be negative
    private int stock;

    //@Column(name = "is_available")
    //    private boolean isAvailable; => make emthod in service layer? TODO

    @Schema(description = "Description of the product.", example = "A bright LED light for all your lighting needs.")
    @Size(max = 255)
    private String description;
}
