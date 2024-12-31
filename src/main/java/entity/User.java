package entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

//https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/index.html)
@Entity
@Schema(description = "Represents a user of the platform.")
@Table(name = "users")
public class User implements UserDetails { //interface required by spring security

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
        // this.roles = Set.of("ROLE_CLIENT"); // Default role
    }

    public User() {
    }

    public Long getId() {
        return id;
    }
    public @NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username is required") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username) {
        this.username = username;
    }

    public @NotBlank(message = "Email is required") @Email(message = "Email should be valid format") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid format") String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }//since roles are stored as strings, we need to convert them to SimpleGrantedAuthority objects for spring security system

    public @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}
