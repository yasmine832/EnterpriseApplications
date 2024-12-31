package service;

import dto.RegisterDTO;
import entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Schema(description = "Service for managing users")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserReservationHelperService UserReservationHelperService;
    private final UserReservationHelperService userReservationHelperService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserReservationHelperService UserReservationHelperService, UserReservationHelperService userReservationHelperService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.UserReservationHelperService = UserReservationHelperService;
        this.userReservationHelperService = userReservationHelperService;
    }

    //get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //find user by username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    //register new user
    public User registerUser(RegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRoles(Collections.singleton("ROLE_STUDENT"));

        return userRepository.save(user);
    }

    //update user
    public User updateUser(Long id, User updatedUser) {
        User user = UserReservationHelperService.getUser(id);
        // check unique
        if (!user.getUsername().equals(updatedUser.getUsername())
                && userRepository.existsByUsername(updatedUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (!user.getEmail().equals(updatedUser.getEmail())
                && userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) { //only update password if new one provided
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }//todo confirm password
        return userRepository.save(user);
    }

    //delete user and cancel all their reservations
    public void deleteUser(Long userId, Long requestingUserId, boolean confirmed) {
        if (!confirmed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deletion requires confirmation");
        }

        User requestingUser = UserReservationHelperService.getUser(requestingUserId);
        boolean isAdmin = requestingUser.getRoles().contains("ROLE_ADMIN");

        if (!isAdmin && !userId.equals(requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        UserReservationHelperService.cancelAllUserReservations(userId);
        userRepository.deleteById(userId);
    }

    //update user roles
    public User addAdminRole(Long userId, boolean isAdmin, Long adminId) {
        User admin = userReservationHelperService.getUser(adminId);
        if (!admin.getRoles().contains("ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can modify roles");
        }

        User user = userReservationHelperService.getUser(userId);
        Set<String> roles = new HashSet<>(Collections.singleton("ROLE_STUDENT"));
        if (isAdmin) {
            roles.add("ROLE_ADMIN");
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }



}