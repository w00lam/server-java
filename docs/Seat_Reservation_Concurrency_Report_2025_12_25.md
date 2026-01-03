# 좌석 예약 동시성 처리 보고서

## 1. 좌석 임시 배정 시 락 제어

### 배경

동일 공연 좌석을 여러 사용자가 동시에 선택할 수 있기 때문에, 중복 예약을 방지하기 위해 락 제어가 필요함.

### 구현 방식

* **Pessimistic Locking (for update)** 사용

  * `SeatRepository`에서 특정 좌석을 조회할 때 `for update` 적용.
  * 트랜잭션 내에서 조회 후 좌석 상태 변경.

### 테스트

* 다중 스레드 환경에서 동일 좌석에 접근 시 한 스레드만 예약 성공.
* 다른 스레드는 트랜잭션 완료 후에 접근 가능.
* 테스트 코드: `SeatRepositoryIT`에서 `findByIdForUpdate` 검증.

## 2. 잔액 차감 동시성 제어

### 배경

사용자가 동시에 결제 시 잔액 차감 로직이 꼬일 수 있음.

### 구현 방식

* 트랜잭션 범위에서 잔액 조회 → 검증 → 차감
* Spring `@Transactional` 적용
* DB에서 `Pessimistic Lock` 또는 `Optimistic Lock` 사용 가능

  * 현재는 Pessimistic Locking 적용: `select for update`.

### 테스트

* 두 스레드가 동시에 결제 시도
* 한 스레드만 정상 완료, 다른 스레드는 예외 발생 후 롤백
* 테스트 코드: `ReservationServiceConcurrencyTest`

## 3. 배정 타임아웃 해제 스케줄러

### 배경

사용자가 좌석을 선택 후 결제를 하지 않으면 일정 시간 후 좌석을 해제해야 함.

### 구현 방식

* Spring `@Scheduled(fixedRate = 60000)`로 1분마다 실행
* 만료된 좌석 조회: `holdUntil < now`
* 트랜잭션 적용: `@Transactional`
* 좌석 상태 변경:

  * `held = false`
  * `holdUntil = null`

### 테스트

* 좌석 생성 후 holdUntil을 과거로 설정
* 스케줄러 실행 후 상태 검증

  * `held == false`
  * `holdUntil == null`
* 테스트 코드: `SeatHoldReleaseSchedulerIT`

## 4. 결론

* Pessimistic Locking으로 좌석 중복 배정 방지
* 트랜잭션 범위 내 잔액 차감으로 결제 동시성 문제 해결
* 스케줄러로 만료 좌석 자동 해제, 테스트로 검증 완료

## 5. 참고

* Spring Data JPA
* Hibernate Pessimistic Locking
* JUnit 5 Integration Test
