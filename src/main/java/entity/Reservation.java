package entity;

import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservations")
@Schema(description = "Represents a student's equipment reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @Schema(description = "The user who made the reservation")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "reservation_products",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Schema(description = "Products included in the reservation")
    private List<Product> products;

    @NotNull
    @Schema(description = "Start date of the reservation", example = "2024-12-14")
    private LocalDate startDate; //met isavailable bool method (hotels)

    @NotNull
    @Schema(description = "End date of the reservation", example = "2024-12-16")
    private LocalDate endDate;

    @Schema(description = "The total cost of the reservation", example = "45.0")
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of the reservation", example = "CONFIRMED")
    private ReservationStatus status;

    @Schema(description = "Date and time when the reservation was made", example = "2024-12-14T14:30:00")
    private LocalDateTime createdAt;

    @PrePersist // to automatically set the createdAt date when a new reservation is created, hidden from the API and set state to PENDING
    @Schema(hidden = true)
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = ReservationStatus.PENDING;
    }

}