package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO for creating a reservation.")
public class ReservationDTO {
    @Schema(description = "ID of the user making the reservation", example = "1")
    @NotNull(message = "User ID is required")
    private Long userId;

    @Schema(description = "List of products and their quantities to reserve")
    @NotEmpty(message = "At least one product is required")
    private List<ReservationProductDTO> products;

    @Schema(description = "Start date of the reservation", example = "2024-12-25")
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;

    @Schema(description = "End date of the reservation", example = "2024-12-28")
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    public @NotNull(message = "User ID is required") Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull(message = "User ID is required") Long userId) {
        this.userId = userId;
    }

    public @NotEmpty(message = "At least one product is required") List<ReservationProductDTO> getProducts() {
        return products;
    }

    public void setProducts(@NotEmpty(message = "At least one product is required") List<ReservationProductDTO> products) {
        this.products = products;
    }

    public @NotNull(message = "Start date is required") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "End date is required") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "End date is required") LocalDate endDate) {
        this.endDate = endDate;
    }
}