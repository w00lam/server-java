package kr.hhplus.be.server.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    private UUID reservationId;
    private UUID userId;
    private UUID seatId;
    private String status;
    private LocalDateTime tempHoldExpiresAt;
}
