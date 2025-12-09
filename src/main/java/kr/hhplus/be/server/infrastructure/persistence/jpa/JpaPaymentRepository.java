package kr.hhplus.be.server.infrastructure.persistence.jpa;

import kr.hhplus.be.server.domain.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
}
