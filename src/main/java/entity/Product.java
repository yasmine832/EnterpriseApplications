package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;

@Entity
@Table(name = "products")
@Schema(description = "Represents a product available for rental")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the product.", example = "LED Light")
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Schema(description = "Category of the product.", example = "Lighting")
    @NotNull(message = "Category is required")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Schema(description = "Cost per day.", example = "50.0")
    @NotNull(message = "Price is required")
    @PositiveOrZero
    private double dailyRentalPrice;

    @Schema(description = "Stock quantity available.", example = "10")
    @NotNull(message = "Quantity is required")
    @Min(0)// can't be negative
    private int stock;

    @Schema(description = "Description of the product.", example = "A bright LED light for all your lighting needs.")
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;

    @Schema(description = "Reservations made for this product.")
    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private List<Reservation> reservations;


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
    @PositiveOrZero
    public double getDailyRentalPrice() {
        return dailyRentalPrice;
    }

    public void setDailyRentalPrice(@NotNull(message = "Price is required") @PositiveOrZero double dailyRentalPrice) {
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

    public List<Reservation> getReservations() {
        return reservations;
    }

}
