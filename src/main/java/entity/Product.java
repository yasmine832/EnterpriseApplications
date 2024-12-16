package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(nullable = false)
    @NotBlank(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Positive(message = "Daily rental price must be positive")
    @Schema(description = "Price per day.", example = "50.0")
    @Column(nullable = false)
    private double dailyRentalPrice;

    @Schema(description = "Stock quantity available.", example = "10")
    @Column(nullable = false)
    @Min(0)// can't be negative
    private int stock;

    //@Column(name = "is_available")
    //    private boolean isAvailable; => make emthod in service layer? TODO

    @Schema(description = "Description of the product.", example = "A bright LED light for all your lighting needs.")
    @Size(max = 255)
    private String description;
}
