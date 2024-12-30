package service;

import dto.RegisterDTO;
import entity.ReservationStatus;
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
import repository.ReservationRepository;
import repository.UserRepository;

import java.util.List;

@Service
@Schema(description = "Service for managing users")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ReservationService reservationService, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + id));
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

        return userRepository.save(user);
    }

    //update user
    public User updateUser(Long id, User updatedUser) {
        User user = getUser(id);
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
        if (updatedUser.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }//todo confirm password
        return userRepository.save(user);
    }

    //delete user
    public void deleteUser(Long userId, Long requestingUserId, boolean confirmed) {
        if (!confirmed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deletion requires confirmation");
        }

        User requestingUser = getUser(requestingUserId);
        boolean isAdmin = requestingUser.getRoles().contains("ROLE_ADMIN");

        if (!isAdmin && !userId.equals(requestingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        cancelAllUserReservations(userId);
        userRepository.deleteById(userId);
    }

    //cancel reservation
    private void cancelAllUserReservations(Long userId) {
        reservationRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)// get all reservations that are not cancelled and cancel them
                .forEach(r -> reservationService.cancelReservation(r.getId()));
    }

}