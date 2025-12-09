package kr.hhplus.be.server.domain.reservation.model;

import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.common.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest extends BaseUnitTest {
    @Test
    @DisplayName("Reservation.create() returns a Reservation with fixed UUID and fixed expiration")
    void testCreateReservation() {
        // given
        User user = new User();
        Seat seat = new Seat();

        // Clock 고정
        Instant fixedInstant = fixedNow().atZone(ZoneId.of("UTC")).toInstant();
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        // 간단한 고정 만료 정책
        ReservationExpirationPolicy expirationPolicy = now -> fixedNow().plusMinutes(5);

        // when
        Reservation reservation = Reservation.create(user, seat, fixedClock, expirationPolicy);

        // then
        assertNotNull(reservation.getId());
        assertEquals(user, reservation.getUser());
        assertEquals(seat, reservation.getSeat());
        assertEquals(ReservationStatus.TEMP_HOLD, reservation.getStatus());
        assertEquals(fixedNow().plusMinutes(5), reservation.getTempHoldExpiresAt());
        assertNull(reservation.getConfirmedAt());
        assertFalse(reservation.isDeleted());
    }
}
