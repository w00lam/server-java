package kr.hhplus.be.server.reservation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.adapter.out.persistence.jpa.JpaReservationRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Reservation 통합 테스트")
public class ReservationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaReservationRepository jpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID fixedUserId;
    private UUID fixedSeatId;

    @BeforeEach
    void setUp() {
        fixedUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        fixedSeatId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        jpaRepository.deleteAll(); // 테스트 반복 가능하도록 초기화
    }

    @Test
    @DisplayName("좌석 예약 요청 시 정상 동작")
    void reserveSeat_integration_success() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setUserId(fixedUserId);
        request.setSeatId(fixedSeatId);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.userId").value(fixedUserId.toString()))
                .andExpect(jsonPath("$.seatId").value(fixedSeatId.toString()))
                .andExpect(jsonPath("$.status").value(ReservationStatus.TEMP_HOLD.name()));

        // DB 검증
        Optional<Reservation> list = jpaRepository.findBySeatIdAndActive(fixedSeatId, List.of(ReservationStatus.TEMP_HOLD, ReservationStatus.CONFIRMED));
        assertTrue(list.isPresent(), "예약이 존재해야 합니다");

        Reservation reservation = list.get();
        assertEquals(fixedSeatId, reservation.getSeatId());
    }
}
