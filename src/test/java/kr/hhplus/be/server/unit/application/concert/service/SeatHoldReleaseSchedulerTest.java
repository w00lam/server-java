package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.application.concert.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.application.concert.service.SeatHoldReleaseScheduler;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SeatHoldReleaseSchedulerTest {
    @Mock
    private SeatRepositoryPort seatRepository;

    @InjectMocks
    private SeatHoldReleaseScheduler scheduler;

    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seat1 = Seat.builder().deleted(true).build();
        seat2 = Seat.builder().deleted(true).build();
    }

    @Test
    void testReleaseExpiredHolds() {
        List<Seat> expiredSeats = List.of(seat1, seat2);
        when(seatRepository.findSeatsByConcertDateIdForHoldRelease(any(LocalDateTime.class)))
                .thenReturn(expiredSeats);

        scheduler.releaseExpiredHolds();

        // Repository save 호출 확인
        verify(seatRepository, times(1)).save(seat1);
        verify(seatRepository, times(1)).save(seat2);

        // deleted=false로 변경 확인
        assertFalse(seat1.isDeleted());
        assertFalse(seat2.isDeleted());
    }
}
