package entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Represents a Hello message.")
@Entity
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
