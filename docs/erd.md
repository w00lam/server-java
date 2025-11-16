# 콘서트 예약 서비스 - DB & ERD

## 체크리스트

- [ ]  엔터티 나열
- [ ]  각 엔터티 주요 필드와 제약(필수/유니크/길이)
- [ ]  관계 정의(1:N, N:M)와 삭제 정책(소프트/하드)
- [ ]  조회 성능용 인덱스 후보 지정

---

## 1. 테이블 설명과 역할

| 테이블명 | 역할 |
|----------|------|
| `CONCERTS` | 공연 정보 (이름, 공연장, 기간) |
| `SCHEDULES` | 공연 날짜별 스케줄 |
| `SEATS` | 좌석, 상태 관리 (AVAILABLE/TEMPHOLD/RESERVED) |
| `SEAT_ZONES` | 좌석 등급(zone)과 가격 |
| `USERS` | 유저 정보 |
| `QUEUE_TOKENS` | 로그인 후 발급되는 임시 예약용 토큰 |
| `WALLETS` | 유저 포인트 정보 |
| `WALLET_TRANSACTIONS` | 충전/사용 기록 |
| `RESERVATIONS` | 임시 예약/확정 예약 기록, TTL 관리 |
| `PAYMENTS` | 결제 트랜잭션 기록 |

---

## 2. 컬럼 설명

- **토큰 관련**
  - `QUEUE_TOKENS.token` ↔ `RESERVATIONS.queueToken`: 동일 값 참조
  - 상태 필드 의미
    - `QUEUE_TOKENS.status`: `WAITING` / `ACTIVE` / `EXPIRED`
    - `RESERVATIONS.status`: `TEMPORARY/HOLD` / `CONFIRMED` / `EXPIRED`

- **좌석 상태**
  - `SEATS.status`: `AVAILABLE` → `TEMPHOLD` → `RESERVED` → TTL 만료 시 `AVAILABLE`로 복귀

- **TTL**
  - 임시 예약 TTL: 5분
  - `RESERVATIONS.expiresAt`과 `QUEUE_TOKENS.expires_at` 컬럼으로 관리

---

## 3. ERD
```mermaid
erDiagram
    CONCERTS {
        string concertId PK "공연 ID"
        string name "공연 이름"
        string venue "공연장"
        date startDate "공연 시작일"
        date endDate "공연 종료일"
        datetime created_at "등록일"
    }

    USERS {
        long id PK
        string login_id
        string password
        datetime created_at
    }

    QUEUE_TOKENS {
        long id PK
        long user_id FK
        string token "로그인 시 발급, 임시 예약용"
        string status "WAITING/ACTIVE/EXPIRED"
        datetime expires_at "TTL 만료 시간"
        int position
        datetime created_at
    }

    WALLETS {
        long id PK
        long user_id FK
        int balance
        datetime updated_at
    }

    WALLET_TRANSACTIONS {
        long id PK
        long wallet_id FK
        int amount
        string type "CHARGE/USE"
        datetime created_at
    }

    SCHEDULES {
        string scheduleId PK
        string concertId FK
        date date
        int availableSeats
    }

    SEAT_ZONES {
        int zoneId PK
        string zoneName "R/S/VIP"
        int price "좌석 등급별 가격"
    }

    SEATS {
        int seatId PK
        string scheduleId FK
        int zoneId FK
        string status "AVAILABLE: 예약 가능, TEMPHOLD: 임시 배정, RESERVED: 결제 확정"
    }

    RESERVATIONS {
        string reservationId PK
        long userId FK
        string scheduleId FK
        int seatId FK
        string queueToken "QUEUE_TOKENS.token 참조, 임시 예약 시 기록"
        string status "TEMPORARY/HOLD: 임시 배정, CONFIRMED: 결제 완료, EXPIRED: TTL 만료"
        datetime expiresAt "임시 예약 만료 시간"
        datetime created_at
    }

    PAYMENTS {
        long paymentId PK
        string reservationId FK
        long userId FK
        int amount
        string status "SUCCESS/FAILED"
        datetime created_at
    }

    %% Relationships
    CONCERTS ||--o{ SCHEDULES : "has many"
    SCHEDULES ||--o{ SEATS : "has many"
    SEATS ||--|| SEAT_ZONES : "zone info"
    USERS ||--o{ QUEUE_TOKENS : "has many"
    USERS ||--|| WALLETS : "has one"
    WALLETS ||--o{ WALLET_TRANSACTIONS : "records"
    USERS ||--o{ RESERVATIONS : "makes"
    RESERVATIONS }o--|| SEATS : "assigned to"
    RESERVATIONS }o--|| SCHEDULES : "for schedule"
    RESERVATIONS }o--|| USERS : "by user"
    PAYMENTS }o--|| RESERVATIONS : "from reservation"
    USERS ||--o{ PAYMENTS : "makes"
    QUEUE_TOKENS }o--|| USERS : "belongs to"
```

---

## 4. 동시성 / 제약 사항

- 임시 예약 중 동일 좌석에 대한 충돌 처리 `409 CONFLICT`
  
- TTL 만료 시 상태 자동 갱신
  
- 결제 성공 시
    - `RESERVATIONS.status` → `CONFIRMED`
    - `SEATS.status` → `RESERVED`
      
- 결제 실패 또는 TTL 만료 시 상태 롤백
  
---

## 5. 추가 가능 사항

- 좌석/스케줄별 **총 좌석 수, 남은 좌석 수** 계산 방법
  
- 예약 내역/결제 내역 조회 시 `queueToken` 참조
  
- DB 인덱스 제안
    - `USERS.login_id`
    - `QUEUE_TOKENS.token`
    - `SEATS.scheduleId + seatId`
    - `RESERVATIONS.queueToken`, `RESERVATIONS.seatId`

- 추후 확장
    - 콘서트별 티켓 할인 정책
    - 프로모션 적용
    - 좌석 구간별 가변 가격
 
---
