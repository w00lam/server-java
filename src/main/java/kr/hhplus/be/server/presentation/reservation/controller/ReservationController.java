package kr.hhplus.be.server.presentation.reservation.controller;

import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.presentation.reservation.dto.MakeReservationRequest;
import kr.hhplus.be.server.presentation.reservation.dto.MakeReservationResponse;
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
    private final MakeReservationUseCase makeReservationUseCase;

    @PostMapping
    public ResponseEntity<MakeReservationResponse> makeReservation(@RequestBody MakeReservationRequest request) {
        var result = makeReservationUseCase.execute(new MakeReservationCommand(request.userId(), request.seatId()));
        var response = MakeReservationResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
