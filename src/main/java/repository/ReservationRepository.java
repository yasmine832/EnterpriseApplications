package repository;

import entity.Reservation;
import entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservationRepository  extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    // find reservations overlapping with date range
    @Query("SELECT r FROM Reservation r WHERE " +
            "((r.startDate <= :endDate) AND (r.endDate >= :startDate))")
    List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    List<Reservation> findByStatusIn(Collection<ReservationStatus> statuses);

}
