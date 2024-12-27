package service;

import entity.Product;
import entity.ProductCategory;
import entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import repository.ProductRepository;
import repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Schema(description = "Service for managing products.")
public class ProductService {

    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    // Create a new product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Get a product by ID
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found with id: " + id));
    }

    //Get a product by name
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Retrieves all products, optionally filtering by availability or category.
     *
     * @param startDate the start date to check availability (nullable)
     * @param endDate   the end date to check availability (nullable)
     * @param category  the category to filter by (nullable)
     * @return a list of products matching the selected criteria
     */
    public List<Product> getAllProducts(LocalDate startDate, LocalDate endDate, ProductCategory category) {
        List<Product> products = category != null ?
                productRepository.findByCategory(category) :
                productRepository.findAll(); //fallback if no ²category is provided

        // If no date range is provided
        if (startDate == null || endDate == null) {
            return products; //products list already been populated from db above
        }

        return products.stream()
                .filter(product -> {
                    // total quantity of product during the specified date range
                    int reservedQuantity = getReservedQuantity(product.getId(), startDate, endDate);

                    // remaining available stock
                    int availableQuantity = product.getStock() - reservedQuantity;

                    if (availableQuantity > 0) {
                        product.setStock(availableQuantity);
                        return true; // Include in filtered list
                    }

                    // exclude product if no stock
                    return false;
                })
                .collect(Collectors.toList());
    }//TODO


    // Update a product
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProduct(id);
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setDailyRentalPrice(productDetails.getDailyRentalPrice());
        product.setStock(productDetails.getStock());
        product.setDescription(productDetails.getDescription());
        return productRepository.save(product);
    }

    // Delete a product
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Calculates the total quantity reserved for a product within a specific date range.
     *
     * @param productId the ID of the product
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return the reserved quantity
     */
    private int getReservedQuantity(Long productId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> overlappingReservations = reservationRepository.findByDateRange(startDate, endDate);

        return (int) overlappingReservations.stream()
                .flatMap(r -> r.getProducts().stream()) // tranforms stream of reservations into stream of products (across all reservations)
                .filter(p -> p.getId().equals(productId)) // Filter to include only products matching id
                .count();
    }
}



