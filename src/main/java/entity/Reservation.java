package entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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

    @ManyToMany(fetch = FetchType.EAGER) // load aLL products when loading a reservation
    @JoinTable(
            name = "reservation_products",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Schema(description = "Products included in the reservation")
    private List<Product> products;


    @NotNull
    @Schema(description = "Start date of the reservation", example = "2024-12-14")
    private LocalDate startDate;

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

    public Reservation() {
    }

    public Long getId() {
        return id;
    }

    public @NotNull(message = "User is required") User getUser() {
        return user;
    }

    public void setUser(@NotNull(message = "User is required") User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public @NotNull LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}