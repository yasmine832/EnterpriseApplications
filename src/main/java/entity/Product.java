package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

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

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public @NotBlank @Size(max = 100) String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(max = 100) String name) {
        this.name = name;
    }

    public @NotNull(message = "Category is required") ProductCategory getCategory() {
        return category;
    }

    public void setCategory(@NotNull(message = "Category is required") ProductCategory category) {
        this.category = category;
    }

    @NotNull(message = "Price is required")
    public double getDailyRentalPrice() {
        return dailyRentalPrice;
    }

    public void setDailyRentalPrice(@NotNull(message = "Price is required") double dailyRentalPrice) {
        this.dailyRentalPrice = dailyRentalPrice;
    }

    @NotNull(message = "Quantity is required")
    @Min(0)
    public int getStock() {
        return stock;
    }

    public void setStock(@NotNull(message = "Quantity is required") @Min(0) int stock) {
        this.stock = stock;
    }

    public @Size(max = 255) String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 255) String description) {
        this.description = description;
    }
}
