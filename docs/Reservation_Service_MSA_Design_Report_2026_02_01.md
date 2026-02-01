# Concert Reservation Platform – MSA 설계 및 트랜잭션 전략

## 1. 도메인별 배포 단위 제안

| 도메인 | 책임 | 배포 단위 |
|--------|------|------------|
| User | 유저 관리, 포인트 조회/충전 | `user-service` |
| Concert | 콘서트 정보, 날짜, 좌석 | `concert-service` |
| Reservation | 좌석 예약, 상태 전환, 중복 방지 | `reservation-service` |
| Payment | 결제 처리, 결제 상태 | `payment-service` |
| Notification / Data Platform | 외부 시스템 알림, 데이터 전송 | `notification-service` |

- 서비스 간 연계는 이벤트 기반 통신(Kafka 등)으로 처리
- 각 도메인은 독립 배포 가능하며, 확장성 확보

---

## 2. 트랜잭션 처리의 한계

- 기존 모놀리식에서는 **결제 + 예약 확정**을 DB 트랜잭션으로 원자적 처리 가능
- MSA 구조에서는 **분산 트랜잭션**이 필요
    - XA 트랜잭션: 구현 복잡, 성능 저하
    - 단순 2PC/3PC: 이벤트 기반 처리에서 어려움
- 문제점
    1. 결제 성공 후 예약 확정 실패 시 데이터 불일치
    2. 좌석 락/중복 예약 방지와 결제 상태 동기화 어려움
    3. 외부 시스템 데이터 전송 타이밍 관리 필요

---

## 3. 해결 방안

### 3.1 Saga 패턴
- 서비스 간 상태 전이를 이벤트 기반으로 처리
- 예: 결제 완료 이벤트 → 예약 확정 서비스 → 데이터 플랫폼 전송
- 실패 시 보상 처리 가능

### 3.2 Event-Driven Architecture
- 각 서비스는 **자체 트랜잭션 내에서 상태 변경 후 이벤트 발행**
- 이벤트 수신 서비스는 독립 트랜잭션으로 처리
- 이벤트 순서, 재시도, 중복 처리 전략 필요

### 3.3 분산 락 / Seat Lock 관리
- Redis 기반 임시 좌석 락 사용
- TTL 기반 자동 해제
- Reservation 서비스 단독 관리로 트랜잭션 독립성 보장

---

## 4. 서비스 개선 포인트

1. Reservation → Data Platform 연계
    - `ReservationConfirmedEvent` 발행
    - Notification/Data Platform 서비스가 이벤트 수신 후 Mock API 호출
2. 테스트 작성
    - Integration Test로 이벤트 발행 및 외부 호출 검증
3. 트랜잭션 경계 분리
    - Payment / Reservation / Notification 각각 독립 트랜잭션
    - 상태 변경 이벤트로 서비스 간 연계

---

## 5. 결론

- MSA 구조로 전환 시, **트랜잭션을 각 서비스 내부로 한정**하고  
  서비스 간 상태 연계는 이벤트 기반으로 처리
- 분산 트랜잭션 대신 **Saga + Event-Driven** 조합으로 확장성과 안정성 확보
- 과제 구현에서는 **예약 확정 이벤트 + Mock API 테스트**로 서비스 관심사 분리와 트랜잭션 독립성을 검증
