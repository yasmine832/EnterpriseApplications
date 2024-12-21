package service;

import entity.Product;
import entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import repository.ProductRepository;

import java.util.List;

@Service
@Schema(description = "Service for managing products.")
public class ProductService {

    //todo error cosntraints
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get a product by name
    public Product getProductByName(String name) {
        return productRepository.findByName(name);
    }

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

    // Filter products by category
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    // Filter products by stock quantity
    public List<Product> getProductsInStock() {
        return productRepository.findByStockGreaterThan(0);
    }
}