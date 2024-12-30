package entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the status of a reservation.")
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    ACTIVE,  // During rental period
    //RETURNED, //Items returned successfully
    //OVERDUE, // Items not returned on time //todo
}