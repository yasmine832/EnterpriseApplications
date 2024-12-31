package controller;

import entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import service.UserReservationHelperService;
import service.UserService;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user managment")
public class UserController {
    private final UserService userService;
    private final UserReservationHelperService userReservationHelperService;

    @Autowired
    public UserController(UserService userService, UserReservationHelperService userReservationHelperService) {
        this.userService = userService;
        this.userReservationHelperService = userReservationHelperService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<User> getProfileDetails(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userReservationHelperService.getUser(user.getId()));
    }//todo

    //@GetMapping("/profile")
    //   public ResponseEntity<RegisterDTO> getProfile(@AuthenticationPrincipal User user) {
    //       RegisterDTO profile = new RegisterDTO();
    //       profile.setUsername(user.getUsername());
    //       profile.setEmail(user.getEmail());
    //       return ResponseEntity.ok(profile);
    //   }

    @PutMapping("/profile")
    @Operation(summary = "Update current user's profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal User user, @Valid @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), updatedUser));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Delete own account")
    public ResponseEntity<Void> deleteOwnAccount(
            @AuthenticationPrincipal User user,
            @RequestParam boolean confirm) {
        if (!confirm) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please confirm account deletion");
        }
        userService.deleteUser(user.getId(), user.getId(), confirm);//TODO
        return ResponseEntity.noContent().build();
    }//TODO


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user (Admin only)")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @RequestParam boolean confirm,
            @AuthenticationPrincipal User admin) {
        userService.deleteUser(id, admin.getId(), confirm);
        return ResponseEntity.noContent().build();
    }


}




