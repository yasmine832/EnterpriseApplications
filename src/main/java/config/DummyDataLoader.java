package config;

import entity.Product;
import entity.ProductCategory;
import entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.ProductRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "Loads dummy data into the database.")
@Configuration
public class DummyDataLoader {

    @Bean
    public CommandLineRunner loadDummyData(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Load Users
            User user1 = new User();
            user1.setUsername("john_doe");
            user1.setEmail("radio@mail.com");
            user1.setPassword(passwordEncoder.encode("password1"));  // Encrypt the password
            user1.setRoles(new HashSet<>(Set.of("ROLE_ADMIN", "ROLE_STUDENT")));
            user1.setCreatedAt(LocalDateTime.now());
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("jane_doe");
            user2.setEmail("rupi@mail.com");
            user2.setPassword(passwordEncoder.encode("password1"));  // Encrypt the password
            user2.setRoles(new HashSet<>(Set.of("ROLE_STUDENT")));
            user2.setCreatedAt(LocalDateTime.now());
            userRepository.save(user2);


            // Load Products
            Product product1 = new Product();
            product1.setName("Laptop");
            product1.setDescription("A laptop for all your needs");
            product1.setDailyRentalPrice((50.00));
            product1.setStock(3);
            product1.setCategory(ProductCategory.LIGHTING);
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Tablet");
            product2.setDescription("A tablet for all your needs");
            product2.setDailyRentalPrice(30);
            product2.setStock(5);
            product2.setCategory(ProductCategory.CABLES);
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Smartphone");
            product3.setDescription("A smartphone for all your needs");
            product3.setDailyRentalPrice(25);
            product3.setStock(4);
            product3.setCategory(ProductCategory.CONTROL_PANELS);
            productRepository.save(product3);


//            // Load Reservations
//            Reservation reservation1 = new Reservation();
//            reservation1.setUser(user1);
//            List<Product> products1 = new ArrayList<>();
//            products1.add(product1);
//            products1.add(product1);
//            products1.add(product2);
//            reservation1.setProducts(products1);
//            reservation1.setStartDate(LocalDate.of(2024, 12, 24));
//            reservation1.setEndDate(LocalDate.of(2024, 12, 26));
//            reservation1.setTotalPrice((2 * 50.0 + 30.0) * 3);
//            reservation1.setStatus(ReservationStatus.CONFIRMED);
//            reservation1.setCreatedAt(LocalDateTime.now());
//            reservationRepository.save(reservation1);
//
////            Reservation reservation2 = new Reservation();
////            reservation2.setUser(user2);
////            reservation2.setProducts(Arrays.asList(product3, product3));
////            reservation2.setStartDate(LocalDate.of(2024, 12, 31));
////            reservation2.setEndDate(LocalDate.of(2025, 1, 2));
////            reservation2.setTotalPrice(2 * 25.0 * 3);
////            reservation2.setStatus(ReservationStatus.CONFIRMED);
////            reservation2.setCreatedAt(LocalDateTime.now());
////            reservationRepository.save(reservation2);
////
////            Reservation reservation3 = new Reservation();
////            reservation3.setUser(user1);
////            reservation3.setProducts(Arrays.asList(product1, product2, product2));
////            reservation3.setStartDate(LocalDate.of(2024, 12, 15));
////            reservation3.setEndDate(LocalDate.of(2024, 12, 17));
////            reservation3.setTotalPrice((50.0 + 2 * 30.0) * 3);
////            reservation3.setStatus(ReservationStatus.CONFIRMED);
////            reservation3.setCreatedAt(LocalDateTime.now());
////            reservationRepository.save(reservation3);
        };
    }
}
