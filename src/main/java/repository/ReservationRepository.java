package repository;

import entity.Reservation;
import entity.ReservationStatus;
import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByCreatedAt(LocalDateTime createdAt);
    List<Reservation> findByUserAndCreatedAt(User user, LocalDateTime createdAt);
    List<Reservation> findByUserAndStartDate( User user, LocalDate startDate);
    List<Reservation> findByUserAndEndDate( User user, LocalDate endDate);
    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);


}

