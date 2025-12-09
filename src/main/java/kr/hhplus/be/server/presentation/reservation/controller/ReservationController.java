package kr.hhplus.be.server.presentation.reservation.controller;

import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.presentation.reservation.dto.ConfirmReservationResponse;
import kr.hhplus.be.server.presentation.reservation.dto.MakeReservationRequest;
import kr.hhplus.be.server.presentation.reservation.dto.MakeReservationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {
    private final MakeReservationUseCase makeReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;

    @PostMapping
    public ResponseEntity<MakeReservationResponse> makeReservation(@RequestBody MakeReservationRequest request) {
        var result = makeReservationUseCase.execute(new MakeReservationCommand(request.userId(), request.seatId()));
        var response = MakeReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ConfirmReservationResponse> confirmReservation(@PathVariable UUID reservationId) {
        var result = confirmReservationUseCase.execute(new ConfirmReservationCommand(reservationId));
        var response = ConfirmReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
