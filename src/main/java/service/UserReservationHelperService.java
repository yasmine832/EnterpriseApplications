package service;

import entity.Reservation;
import entity.ReservationStatus;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import repository.ReservationRepository;
import repository.UserRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserReservationHelperService {
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ProductService productService;

    @Autowired
    public UserReservationHelperService(UserRepository userRepository, ReservationRepository reservationRepository, ProductService productService) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.productService = productService;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    //cancel reservation
    public void cancelAllUserReservations(Long userId) {
        reservationRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)// get all reservations that are not cancelled and cancel them
                .forEach(r -> cancelReservation(r.getId()));
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (reservation.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel a reservation that has already started");
        }

        // Return stock for each product
        Map<Long, Long> productCounts = reservation.getProducts().stream()
                .collect(Collectors.groupingBy(product -> product.getId(), Collectors.counting()));

        productCounts.forEach((productId, count) -> {
            var product = productService.getProduct(productId);
            product.setStock(product.getStock() + count.intValue());
            productService.updateProduct(productId, product);
        });

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}