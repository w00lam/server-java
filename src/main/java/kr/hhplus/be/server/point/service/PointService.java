package kr.hhplus.be.server.point.service;

import kr.hhplus.be.server.point.domain.PointTransaction;
import kr.hhplus.be.server.point.domain.PointType;
import kr.hhplus.be.server.point.repository.PointRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class PointService {
    private final PointRepository repository;

    public void chargePoint(UUID userId, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        repository.save(new PointTransaction(userId, amount, PointType.CHARGE));
    }

    public int getPointBalance(UUID userId) {
        List<PointTransaction> transactions = repository.findByUserId(userId);
        return transactions.stream().filter(tx-> !tx.isDeleted()).mapToInt(PointTransaction::getAmount).sum();
    }
}
