package entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Schema(description = "Represents a user of the platform.")
@NoArgsConstructor
@AllArgsConstructor
@Builder // creates builder pattern for class
@Table(name = "users")
public class User {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user.", example = "john_doe")
    @Column(nullable = false, unique = true)
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid format")
    @Column(unique = true, nullable = false)
    private String email;

    @Schema(description = "Password for the account. Stored as a hash.", example = "$2a$10$...")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    //TODO paswword confirmation

    @Schema(description = "Roles assigned to the user.", example = "'ROLE_USER'")
    //@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) //@CollectionTable creates a separate table for roles
    @ElementCollection(fetch = FetchType.EAGER) //  allows storing a collection of basic types, FetchType.EAGER loads roles immediately with user
    private Set<String> roles;
    //default

    @Schema(description = "Date and time the user was created.")
    private LocalDateTime createdAt; //weglaten?

    @PrePersist // to automatically set the createdAt date when a new user is created
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.roles = Set.of("ROLE_STUDENT"); // Default role
    }


    //registration
    //TODO

}
