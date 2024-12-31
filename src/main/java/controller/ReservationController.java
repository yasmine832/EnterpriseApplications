package controller;

import dto.ReservationDTO;
import entity.Reservation;
import entity.ReservationStatus;
import entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.ReservationRepository;
import service.ReservationService;
import service.UserReservationHelperService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Schema(description = "Controller for managing reservations")
@Tag(name = "Reservation Management", description = "APIs for managing reservations")
@RequestMapping("api/reservations")
@PreAuthorize("isAuthenticated()")
public class ReservationController {

    private final ReservationService reservationService;
    private final repository.ReservationRepository reservationRepository;
    private final UserReservationHelperService userReservationHelperService;

    @Autowired
    public ReservationController(ReservationService reservationService, ReservationRepository reservationRepository, UserReservationHelperService userReservationHelperService) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
        this.userReservationHelperService = userReservationHelperService;
    }

    @Transactional
    @PostMapping
    @Operation(summary = "Create a new reservation with product quantities")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        return new ResponseEntity<>(reservationService.createReservation(reservationDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a reservation by ID")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Get all reservations for the current user (optionally filtered by status)")
    public ResponseEntity<List<Reservation>> getMyReservations(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) ReservationStatus status) {
        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        if (status != null) {
            reservations = reservations.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all reservations (optionally filtered by status)")
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(required = false) ReservationStatus status) {
        List<Reservation> reservations = reservationService.getAllReservations();
        if (status != null) {
            reservations = reservations.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reservation (user's own or any for admin)")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestParam boolean confirm) {
        if (!confirm) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancellation requires confirmation");
        }

        Reservation reservation = reservationService.getReservation(id);
        boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");

        if (!isAdmin && !reservation.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can only cancel own reservations");
        }

        userReservationHelperService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reservations for a specific user")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getUserReservations(userId));
    }

    @PutMapping("/{id}/confirm-payment")
    @Operation(summary = "Confirm payment for reservation")
    public ResponseEntity<Reservation> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmPayment(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/return")
    @Operation(summary = "Process return of rented items")
    public ResponseEntity<Reservation> processReturn(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.processReturn(id));
    } //TODO

}