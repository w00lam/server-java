package kr.hhplus.be.server.intergration;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentResult;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenCommand;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenResult;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenUseCase;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import kr.hhplus.be.server.domain.payment.model.PaymentMethod;
import kr.hhplus.be.server.domain.user.model.User;

import kr.hhplus.be.server.infrastructure.persistence.concert.adapter.ConcertDateRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.concert.adapter.ConcertRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.concert.adapter.SeatRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.payment.adapter.PaymentRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.point.adapter.PointRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.queue.token.adapter.TokenRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.reservation.adapter.ReservationRepositoryImpl;
import kr.hhplus.be.server.infrastructure.persistence.user.adapter.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest
public abstract class ReservationIntegrationTestBase {
    /*
     * ================
     *   USE CASES
     * ================
     */
    @Autowired
    protected IssueTokenUseCase issueTokenUseCase;

    @Autowired
    protected MakeReservationUseCase makeReservationUseCase;

    @Autowired
    protected MakePaymentUseCase makePaymentUseCase;

    /*
     * =========================
     *     INFRA REPOSITORIES
     * =========================
     */
    @Autowired
    protected UserRepositoryImpl userRepository;

    @Autowired
    protected TokenRepositoryImpl tokenRepository;

    @Autowired
    protected ReservationRepositoryImpl reservationRepository;

    @Autowired
    protected PointRepositoryImpl pointRepository;

    @Autowired
    protected PaymentRepositoryImpl paymentRepository;

    @Autowired
    protected ConcertRepositoryImpl concertRepository;

    @Autowired
    protected ConcertDateRepositoryImpl concertDateRepository;

    @Autowired
    protected SeatRepositoryImpl seatRepository;

    @PersistenceContext
    private EntityManager em;

    /*
     * =========================
     *    HELPER: CREATE USER
     * =========================
     */
    protected User createUser() {
        User user = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@example.com")
                .name("nickname")
                .points(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();


        User saved = userRepository.save(user);

        return saved;
    }

    protected User createUserWithPoints(int points) {
        User user = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@example.com")
                .name("tester")
                .points(points)
                .deleted(false)
                .build();

        User saved = userRepository.save(user);

        return saved;
    }

    /*
     * =========================
     *    HELPER: CREATE Seat
     * =========================
     */
    protected Seat createSeat() {
        Concert concert = concertRepository.save(
                Concert.builder()
                        .title("테스트 콘서트")
                        .build()
        );

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );

        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .section("A")
                .row("1")
                .number("1")
                .grade("VIP")
                .deleted(false)
                .build();

        Seat saved = seatRepository.save(seat);

        return saved;
    }

    protected Seat createSeatWithConcert(ConcertDate concertDate, String section, String row, String number, String grade) {
        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .section(section)
                .row(row)
                .number(number)
                .grade(grade)
                .build();
        Seat saved = seatRepository.save(seat);

        return saved;
    }


    /*
     * =============================================
     *     HELPER: ISSUE TOKEN → RETURN RESULT
     * =============================================
     */
    protected IssueTokenResult issueToken(User user) {
        IssueTokenCommand cmd = new IssueTokenCommand(user.getId());
        return issueTokenUseCase.execute(cmd);
    }


    /*
     * =============================================
     *     HELPER: MAKE RESERVATION → RETURN RESULT
     * =============================================
     */
    protected MakeReservationResult reserveSeat(UUID userId, UUID concertId,UUID seatId) {
        MakeReservationCommand cmd = new MakeReservationCommand(userId, concertId, seatId);
        return makeReservationUseCase.execute(cmd);
    }


    /*
     * =============================================
     *     HELPER: MAKE PAYMENT → RETURN RESULT
     * =============================================
     */
    protected MakePaymentResult payReservation(UUID reservationId, int amount, PaymentMethod method) {
        MakePaymentCommand cmd = new MakePaymentCommand(reservationId, amount, method);
        return makePaymentUseCase.execute(cmd);
    }
}
