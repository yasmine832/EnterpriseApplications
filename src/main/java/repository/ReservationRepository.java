package repository;

import entity.Reservation;
import entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    //List<Reservation> findByStartDate(LocalDate startDate); TODO
}

