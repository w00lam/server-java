package kr.hhplus.be.server.infrastructure.persistence.jpa;

import kr.hhplus.be.server.domain.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;


public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {
    @Modifying
    @Query("UPDATE Reservation r SET r.tempHoldExpiresAt = :expiresAt WHERE r.id = :reservationId")
    void updateExpiration(@Param("reservationId") UUID reservationId,
                         @Param("expiresAt") LocalDateTime expiresAt);
}
