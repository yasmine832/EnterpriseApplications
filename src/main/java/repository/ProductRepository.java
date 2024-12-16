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
    //available products for period? TODO
}
