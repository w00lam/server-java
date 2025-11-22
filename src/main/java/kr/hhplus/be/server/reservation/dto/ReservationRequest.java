package kr.hhplus.be.server.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReservationRequest {
    private UUID userId;
    private UUID seatId;
}
