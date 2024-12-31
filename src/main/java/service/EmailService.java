package service;

import entity.Product;
import entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

// code adapted from https://mailtrap.io/blog/spring-send-email/

@Service
@Schema(description = "Service for sending email notifications")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendReservationConfirmation(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(reservation.getUser().getEmail());
        message.setSubject("Reservation Confirmation #" + reservation.getId());
        message.setText(buildReservationEmail(reservation));

        mailSender.send(message);
    }

    private String buildReservationEmail(Reservation reservation) {
        StringBuilder products = new StringBuilder();
        Map<Product, Long> productCounts = reservation.getProducts().stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        productCounts.forEach((product, count) ->
                products.append(String.format("- %s (x%d) at €%.2f per day%n",
                        product.getName(), count, product.getDailyRentalPrice())));

        return String.format("""
                        Dear %s,
                        
                        Your reservation has been confirmed for %s to %s.
                        
                        Reserved Items:
                        %s
                        
                        Total Price: €%.2f
                        
                        Reservation Status: %s
                        
                        Thank you for using our service!
                        """,
                reservation.getUser().getUsername(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                products.toString(),
                reservation.getTotalPrice(),
                reservation.getStatus() //TODO
        );
    }
}