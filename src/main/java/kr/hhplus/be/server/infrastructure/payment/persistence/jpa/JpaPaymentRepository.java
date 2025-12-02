package kr.hhplus.be.server.infrastructure.payment.persistence.jpa;

import kr.hhplus.be.server.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
}
