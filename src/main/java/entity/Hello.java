package entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "Represents a Hello message.")
@Entity
@Data // Lombok annotation to create all the getters, setters, equals, hash, and toString methods (except fields with validation cosntraints
@NoArgsConstructor // Lombok annotation to create a no-args constructor
@AllArgsConstructor // Lombok annotation to create a constructor with all args
public class Hello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key generation
    private Long id;

    @NotBlank(message = "Message cannot be blank") // Validation annotation
    private String message;

    @Override
    public String toString() {
        return "Hello{" +
                "message='" + message + '\'' +
                '}';
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
