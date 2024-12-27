package service;

import dto.ReservationDTO;
import dto.ReservationProductDTO;
import entity.Product;
import entity.Reservation;
import entity.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import repository.ProductRepository;
import repository.ReservationRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ProductRepository productRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              ProductService productService
            , UserService userService, ProductRepository productRepository) {
        this.reservationRepository = reservationRepository;
        this.productService = productService;
        this.userService = userService;
        this.productRepository = productRepository;
    }

    public Reservation createReservation(ReservationDTO reservationDTO) {
        validateDates(reservationDTO.getStartDate(), reservationDTO.getEndDate());

        // Validate stock availability
        for (ReservationProductDTO productDTO : reservationDTO.getProducts()) {
            Product product = productService.getProduct(productDTO.getProductId());
            if (!isProductAvailable(product, productDTO.getQuantity(),
                    reservationDTO.getStartDate(),
                    reservationDTO.getEndDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for product: " + product.getName());
            }
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUser(userService.getUser(reservationDTO.getUserId()));
        reservation.setStartDate(reservationDTO.getStartDate());
        reservation.setEndDate(reservationDTO.getEndDate());
        reservation.setStatus(ReservationStatus.PENDING);

        List<Product> reservedProducts = new ArrayList<>();
        double totalPrice = 0;

        for (ReservationProductDTO productDTO : reservationDTO.getProducts()) {
            Product product = productRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found: " + productDTO.getProductId()));
            for (int i = 0; i < productDTO.getQuantity(); i++) {
                reservedProducts.add(product);
                totalPrice += calculateProductPrice(product,
                        reservationDTO.getStartDate(),
                        reservationDTO.getEndDate());
            }
        }

        reservation.setProducts(reservedProducts);
        reservation.setTotalPrice(totalPrice);

        return reservationRepository.save(reservation);
    }//TODO

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Start date cannot be in the past");
        }
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date must be after start date");
        }
    }

    /**
     * Gets total quantity of a product reserved for a date range
     * Counts products in all non-cancelled reservations that overlap with the range
     */
    private int getReservedQuantity(Long productId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> overlappingReservations = reservationRepository.findByDateRange(startDate, endDate);
        return (int) overlappingReservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .flatMap(r -> r.getProducts().stream())// trans stream of reservations into product stream
                .filter(p -> p.getId().equals(productId))// only include products matching id
                .count(); // count the number of products
    }

    // Get a reservation by ID
    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reservation not found with id: " + id));
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }//TODO


    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }//todo

    // Calculate the total price for a product based on the number of days
    private double calculateProductPrice(Product product, LocalDate startDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return product.getDailyRentalPrice() * days;
    }

    public void cancelReservation(Long id) {
        Reservation reservation = getReservation(id);
        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel a reservation that has already started");
        }

        // Count products to return correct quantities to stock
        Map<Long, Long> productCounts = reservation.getProducts().stream()
                .collect(Collectors.groupingBy(Product::getId, Collectors.counting()));

        // Return stock for each product
        productCounts.forEach((productId, count) -> {
            Product product = productService.getProduct(productId);
            product.setStock(product.getStock() + count.intValue());
            productService.updateProduct(productId, product);
        });

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // Check if a product is available for reservation
    public boolean isProductAvailable(Product product, int requestedQuantity,
                                      LocalDate startDate, LocalDate endDate) {
        int reservedQuantity = getReservedQuantity(product.getId(), startDate, endDate);
        return (product.getStock() - reservedQuantity) >= requestedQuantity;
    }
}
