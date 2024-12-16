package repository;

import entity.Product;
import entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);
    List<Product> findByCategory(ProductCategory category);
    List<Product> findByStockGreaterThan(int stock);
    //available products for period? TODO
    //List<Product> findByIsAvailable(Boolean isAvailable);
    //List<Product> findAvailableProductsForPeriod(LocalDate startDate, LocalDate endDate);
    //
    //    // Find products not associated with any reservation overlapping a given period
    //    List<Product> findByReservations_StartDateGreaterThanEqualAndReservations_EndDateLessThanEqual(
    //            LocalDate endDate, LocalDate startDate);
    //

}
