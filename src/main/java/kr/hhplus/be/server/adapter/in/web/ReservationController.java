package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {
    private final ReservationService service;

    @PostMapping
    public ResponseEntity<ReservationResponse> makeReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = service.reserveSeat(request.getUserId(), request.getSeatId());
        ReservationResponse response = new ReservationResponse(
                reservation.getId(),
                request.getUserId(),
                reservation.getSeatId(),
                reservation.getStatus().name(),
                reservation.getTempHoldExpiresAt());
        return ResponseEntity.ok(response);
    }
}
