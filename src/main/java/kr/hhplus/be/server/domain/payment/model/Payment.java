package kr.hhplus.be.server.domain.payment.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PAYMENTS",
        indexes = {
                @Index(name = "idx_reservation_id", columnList = "reservationId"),
                @Index(name = "idx_status", columnList = "status")
        })
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted;

    public static Payment createPending(Reservation reservation, int amount) {
        return Payment.builder()
                .id(UUID.randomUUID())
                .reservation(reservation)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .paidAt(null)
                .deleted(false)
                .build();
    }
}