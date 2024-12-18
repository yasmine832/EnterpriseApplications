package config;

import entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import repository.ProductRepository;
import repository.ReservationRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Schema(description = "Loads dummy data into the database.")
@Configuration
public class DummyDataLoader {

    @Bean
    public CommandLineRunner loadDummyData(ProductRepository productRepository, UserRepository userRepository, ReservationRepository reservationRepository) {
        return args -> {

            // Load Users
            User user1 = new User();
            user1.setUsername("john_doe");
            user1.setEmail("radio@mail.com");
            user1.setPassword("password"); // TODO: Encrypt passwords for user1 and user2
            user1.setRoles(new HashSet<>(Set.of("ROLE_USER")));
            user1.setCreatedAt(LocalDateTime.now());
            userRepository.save(user1);

            User user2 = new User();
            user2.setUsername("jane_doe");
            user2.setEmail("rupi@mail.com");
            user2.setPassword("password");
            user2.setRoles(new HashSet<>(Set.of("ROLE_USER")));
            user2.setCreatedAt(LocalDateTime.now());
            userRepository.save(user2);

            // Load Products
            Product product1 = new Product();
            product1.setName("Laptop");
            product1.setDescription("A laptop for all your needs");
            product1.setDailyRentalPrice(1000.00);
            product1.setStock(10);
            product1.setCategory(ProductCategory.LIGHTING);
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Tablet");
            product2.setDescription("A tablet for all your needs");
            product2.setDailyRentalPrice(500.00);
            product2.setStock(23);
            product2.setCategory(ProductCategory.CABLES);
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Smartphone");
            product3.setDescription("A smartphone for all your needs");
            product3.setDailyRentalPrice(700.00);
            product3.setStock(15);
            product3.setCategory(ProductCategory.CONTROL_PANELS);
            productRepository.save(product3);

            // Load Reservations
            Reservation reservation1 = new Reservation();
            reservation1.setUser(user1);
            reservation1.setProducts(Arrays.asList(product1, product2));
            reservation1.setStartDate(LocalDate.now());
            reservation1.setEndDate(LocalDate.now().plusDays(2));
            reservation1.setTotalPrice(1500.00);
            reservation1.setStatus(ReservationStatus.CONFIRMED);
            reservation1.setCreatedAt(LocalDateTime.now());
            reservationRepository.save(reservation1);

            Reservation reservation2 = new Reservation();
            reservation2.setUser(user2);
            reservation2.setProducts(Arrays.asList(product2));
            reservation2.setStartDate(LocalDate.now());
            reservation2.setEndDate(LocalDate.now().plusDays(1));
            reservation2.setTotalPrice(500.00);
            reservation2.setStatus(ReservationStatus.CONFIRMED);
            reservation2.setCreatedAt(LocalDateTime.now());
            reservationRepository.save(reservation2);
        };
    }
}
