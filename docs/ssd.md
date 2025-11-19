---

# 1. Overview

콘서트 예약 시스템의 핵심 비즈니스 플로우를 시퀀스 다이어그램으로 정리합니다.

---

# 2. 콘서트 조회 (콘서트 목록 → 날짜 → 좌석)

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant DB as PostgreSQL

    User ->> API: GET /concerts
    API ->> App: Forward request
    App ->> DB: SELECT * FROM concerts WHERE deleted_at IS NULL
    DB -->> App: Concert list
    App -->> API: 200 OK
    API -->> User: Concert list

    User ->> API: GET /concerts/{concertId}/dates
    API ->> App: Forward request
    App ->> DB: SELECT * FROM concert_dates WHERE concert_id = ? AND deleted_at IS NULL
    DB -->> App: Date list
    App -->> API: 200 OK
    API -->> User: Date list

    User ->> API: GET /concert-dates/{dateId}/seats
    API ->> App: Forward request
    App ->> DB: SELECT seats + seat_status (booked/pending)
    DB -->> App: Seat list
    App -->> API: 200 OK
    API -->> User: Seat list with availability
```

---

# 3. 좌석 예약 요청 (임시 배정: PENDING)

좌석 충돌 방지를 위해 **Redis 분산락 + Redis PENDING Hold + DB Unique(seatId) 체크**를 함께 사용합니다.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant Redis as Redis Cluster
    participant DB as PostgreSQL

    User ->> API: POST /reservations (seatId)
    API ->> App: Forward request

    Note over App,Redis: 1) Seat Lock 획득
    App ->> Redis: TRY_LOCK("seat:{seatId}") with TTL(3 sec)
    Redis -->> App: Lock OK

    Note over App,Redis: 2) 좌석 임시 배정(TTL 3분)
    App ->> Redis: SETEX seat:{seatId}:pending = userId, TTL=180s

    Note over App,DB: 3) DB 예약 레코드 생성 (status=PENDING)
    App ->> DB: INSERT INTO reservations(seat_id, user_id, status)
    DB -->> App: created

    App -->> API: 201 Created
    API -->> User: reservationId + expiresAt

    Note over Redis: TTL 만료 → pending 자동 해제
```

---

# 4. 결제 요청 → 승인 → 예약 확정

결제는 **외부 PG**와 연동되며,
결제 성공 후 **Kafka에 이벤트를 발행**하고
Consumer가 Reservation 상태를 **CONFIRMED**로 업데이트합니다.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant PG as Payment Gateway
    participant Kafka as Kafka Producer
    participant Consumer as Reservation Consumer
    participant DB as PostgreSQL
    participant Redis as Redis Cluster

    User ->> API: POST /payments
    API ->> App: Forward request

    App ->> PG: Create Payment Intent (amount & seat info)
    PG -->> App: Payment APPROVED

    Note over App: 결제 성공 처리

    App ->> Kafka: Publish "payment.succeeded" event
    Kafka -->> Consumer: Consume event

    Consumer ->> DB: UPDATE reservations SET status='CONFIRMED'
    DB -->> Consumer: OK

    Consumer ->> Redis: DEL seat:{seatId}:pending
    Redis -->> Consumer: OK

    App -->> API: 200 OK
    API -->> User: Payment Success + Seat Confirmed
```

---

# 5. 예약 실패 / 좌석 만료 흐름

임시 배정 후 **3분 내 결제 실패 / 만료** 시 자리 자동 해제.

```mermaid
sequenceDiagram
    participant Redis as Redis
    participant DB as PostgreSQL

    Note over Redis: TTL(3분) 만료

    Redis -->> Redis: Key seat:{seatId}:pending auto-delete

    Redis ->> DB: UPDATE reservations SET status='EXPIRED' (optional batch process)
    DB -->> Redis: OK
```

---

# 6. 포인트 충전 / 조회 API

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant DB as PostgreSQL
    participant Kafka as Kafka

    User ->> API: GET /points
    API ->> App: Forward request
    App ->> DB: SELECT points WHERE user_id=?
    DB -->> App: Result
    App -->> API: 200 OK
    API -->> User: point info

    User ->> API: POST /points/charge
    API ->> App: Forward request
    App ->> DB: UPDATE points SET balance = balance + ?
    DB -->> App: OK

    App ->> Kafka: Publish "points.charged"
    Kafka -->> App: OK

    App -->> API: 200 OK
    API -->> User: charged balance
```

---

# 7. 대기열 토큰 발급

대기열은 Redis Sorted Set 사용
(score = timestamp)
TTL 자동 만료 적용.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant Redis as Redis Cluster

    User ->> API: POST /queue/token
    API ->> App: Forward request

    App ->> Redis: ZADD queue:concert:{id} score=timestamp member=userId
    Redis -->> App: OK

    App ->> Redis: ZRANK queue userId
    Redis -->> App: position

    App -->> API: 201 Created (token, position)
    API -->> User: token + 대기열 순번
```

---

# 8. 대기열 진입 후 실제 예약 API 호출

대기열이 0에 가까워지면 티켓 구매 API 접근 허용.

```mermaid
sequenceDiagram
    participant User
    participant API
    participant App
    participant Redis

    User ->> API: GET /queue/status
    API ->> App: Forward request

    App ->> Redis: ZRANK queue userId
    Redis -->> App: position

    App -->> API: position
    API -->> User: Wait position

    Note over User: position <= N → 예약 API 접근 가능
```

---

# 9. 예약 전체 통합 플로우 (요약 종합 버전)

```mermaid
sequenceDiagram
    participant User
    participant API
    participant App
    participant Redis
    participant DB
    participant PG
    participant Kafka
    participant Consumer

    User ->> API: 조회
    API ->> App: seats/dates info
    App ->> DB: fetch
    App -->> User: seat list

    User ->> API: 예약 시도(seatId)
    API ->> App: Forward
    App ->> Redis: Lock + PENDING Hold (TTL)
    App ->> DB: Create reservation(PENDING)

    User ->> API: 결제
    App ->> PG: Process Payment
    PG -->> App: APPROVED
    App ->> Kafka: publish success

    Consumer ->> DB: reservation=CONFIRMED
    Consumer ->> Redis: delete PENDING
    DB -->> Consumer: OK

    App -->> User: 결제 + 예약 성공
```

---
