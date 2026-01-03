# Performance Analysis

## 1. 목적

고빈도 조회 쿼리 및 주요 테이블 성능을 분석하고, 개발 진행에 따라 점진적으로 기록하기 위한 문서.

---

## 2. 분석 대상

- **테이블** : `seats`, `reservations`, `payments`, `concert_date` 등  
- **중점** : 조회 쿼리 성능, 인덱스 활용, row 수, 동시성 영향

---

## 3. 단계별 조회 성능 분석

### 1단계 : 핵심 조회

#### 1. 좌석 조회 (`seats`)

**쿼리 / 메서드**
- 도메인 메서드 예시
  ```java
  List<Seat> findSeatsByConcertDateId(UUID concertDateId);
  ```
**데이터 규모** : 콘서트당 50개

**인덱스** : `uk_concert_date_section_seat_row_number`, `idx_concert_date_section_seat_row`

**EXPLAIN** : Using index condition, ref, key_len=16

**결론** : 성능 문제 없음

**비고** : soft delete 미사용, 단일 콘서트 조회 기준 안전

---

#### 2. 활성 예약 조회 (`reservations`)

**쿼리 / 메서드**
- 조회 대상
  - 활성 예약 정의 : `status = 'CONFIRMED'`
- 도메인 메서드 예시
  - 유저 단위 활성 예약 조회
    ```java
    ReservationRepository.findByUserIdAndStatus(userId, CONFIRMED);
    ```
  - 콘서트 단위 전체 활성 예약 조회
    ```java
    ReservationRepository.findAllConfirmedByConcertDateId(concertDateId);
    ```
      - 구현 시 내부 조인 : `reservations r` ↔ `seats s`
- 주요 사용처
  - 마이페이지 : 사용자 예약 내역 조회
  - 좌석 UI : "이미 예약된 좌석 표시"
  - 통계 : 콘서트별 예약 현황 집계
  
**데이터 규모**
- reservations 테이블 전체 row
  - 실제 서비스 가정 : 수십만 ~ 수백만 row
  - 벤치마크 기준 샘플 : 500,000 row 이상 추천
- 조회 기준별 예측 row 수
  - 유저 단위 조회
    - 1명당 예약 내역 : 수십 ~ 수백 row
    - (한 유저가 여러 공연을 예매했다고 가정)
  - 콘서트 단위 전체 활성 예약 조회
    - 콘서트당 좌석 : 50개
    - 활성 예약률 60~80% 가정
    - 기대 row : 30~40 row 수준
    - 매우 작은 데이터량 → 처리 부담 없음
  - 전체 스캔 가능성
    - user_id, seat_id, status 조건을 사용하므로 인덱스 사용 시 Full Scan 발생하지 않음
  
**인덱스**
- 현재 reservations 인덱스
  | Index                          | Columns                        | Purpose         |
  | ------------------------------ | ------------------------------ | --------------- |
  | `PRIMARY`                      | id                             | PK              |
  | `uk_seat_status`               | (seat_id, status)              | 동일 좌석 중복 예약 방지  |
  | `idx_status_tempHoldExpiresAt` | (status, temp_hold_expires_at) | TEMP_HOLD 만료 처리 |
  | `FK_user_id`                   | user_id                        | 유저 단위 조회        |
- 추가 추천 인덱스
  - 유저 단위 활성 예약 조회 최적화
    ```sql
    CREATE INDEX idx_user_status 
    ON reservations (user_id, status);
    ```
  - 콘서트 단위 전체 활성 예약 조회 최적화
    ```sql
    CREATE INDEX idx_seat_status 
    ON reservations (seat_id, status);
    ```

**EXPLAIN**
- **유저 단위 활성 예약 조회**
  ```sql
  SELECT *
  FROM reservations
  WHERE user_id = :userId
  AND status = 'CONFIRMED';
  ```
  
  **EXPLAIN 요약**
  | 항목    | 값                                  |
  | ----- | ---------------------------------- |
  | type  | ref                                |
  | key   | FK_user_id                         |
  | rows  | 약 20                               |
  | Extra | Using index condition; Using where |
  
  **분석**
  - `user_id` 인덱스를 사용한 `ref` 접근
  - 특정 유저의 예약 범위만 탐색
  - 유저당 예약 수가 제한적이므로 성능 부담 없음
  
  **개선 효과 (idx_user_status 적용 시)**
  - user + status 동시 필터
  - 스캔 row 수 감소
  - 마이페이지 트래픽 증가에도 안정적

- **콘서트 날짜별 전체 활성 예약 조회 (좌석 UI)**
  ```sql
  SELECT r.*
  FROM reservations r
  JOIN seats s ON r.seat_id = s.id
  WHERE s.concert_date_id = :concertDateId
  AND r.status = 'CONFIRMED';
  ```
  
  **EXPLAIN 요약**
  | table        | type   | key                               | rows |
  | ------------ | ------ | --------------------------------- | ---- |
  | seats        | ref    | idx_concert_date_section_seat_row | ~50  |
  | reservations | eq_ref | uk_seat_status                    | 1    |

  **분석**
  - `concert_date_id` 기준으로 좌석 먼저 필터링
  - 좌석 수가 적어 탐색 비용이 매우 낮음
  - `seat_id + status` UNIQUE 인덱스로 1:1 정확 조인
  - 좌석 UI 실시간 조회에 매우 적합

- **콘서트별 활성 예약 집계 (통계용)**
  ```sql
  SELECT COUNT(*)
  FROM reservations r
  JOIN seats s ON r.seat_id = s.id
  WHERE s.concert_id = :concertId
  AND r.status = 'CONFIRMED';
  ```
  
  **EXPLAIN 요약**
  | table        | type   | key                               | rows     |
  | ------------ | ------ | --------------------------------- | -------- |
  | seats        | index  | idx_concert_date_section_seat_row | ~100,000 |
  | reservations | eq_ref | uk_seat_status                    | 1        |

  **분석**
  - seats 인덱스 기준 전체 스캔
  - 테이블 접근 없이 인덱스만 사용 (`Using index`)
  - 통계/집계 목적 쿼리로 허용 가능한 비용
  
**결론**
- `reservations` 테이블은 대량 데이터(수십만 ~ 수백만 row) 환경에서도 **Full Table Scan 없이 안정적으로 동작**

- 주요 조회 시나리오
  - 유저 단위 조회
  - 좌석 UI 조회
  - 콘서트별 통계
   
    모두 인덱스 기반 접근(`ref`, `eq_ref`)으로 처리

- `seat_id + status` UNIQUE 제약을 통해
  - 좌석 중복 예약 방지
  - 조인 성능 최적화
  - 데이터 무결성 확보

- idx_user_status 인덱스 추가 시 마이페이지 트래픽 증가에도 안정적 대응 가능

---

#### 3. 결제 상태 조회 (`payments`)

**쿼리 / 메서드**
- 조회 대상
  - 결제 상태 정의
    - 진행 중 결제 : `status = 'PENDING'`
    - 완료 결제 : `status = 'PAID'`
  - 결제는 `reservation` 기준으로 생성되며, `payments ↔ reservations`는 N:1 관계

- 도메인 메서드 예시
  - 예약 단위 결제 상태 조회
    ```java
    PaymentRepository.findByReservationId(reservationId);
    ```
  - 유저 결제 내역 조회 (마이페이지)
    ```java
    PaymentRepository.findAllByReservation_UserId(userId);
    ```
  - 상태별 결제 조회 (관리/정산용)
    ```java
    PaymentRepository.findAllByStatus(PAID)
    ```
  - 예약 + 결제 상태 동시 조회
    ```sql
    SELECT *
    FROM payments p
    JOIN reservations r ON p.reservation_id = r.id
    WHERE r.user_id = :userId
    AND p.status = 'PAID';
    ```

- 주요 사용처
  - 마이페이지 : 사용자 결제 내역 및 결제 상태 표시
  - 예약 상세 화면 : 결제 완료 여부 확인
  - 결제 실패 / 취소 처리
  - 관리자 페이지 : 결제 상태 모니터링
  - 통계/정산 : 기간별 결제 완료 건 집계

**데이터 규모**
- payments 테이블 전체 row
  - reservations 대비 1:1 또는 1:N (재시도 포함) 구조
  - 실제 서비스 가정 : 수십만 ~ 수백만 row
  - 벤치마크 기준 샘플 : **500,000 row 이상**
  
- 조회 기준별 예측 row 수
  - 예약 단위 조회
    - reservation_id 기준 : `1~N row (매우 소량)`
  - 유저 단위 결제 조회
    - 1명당 수십 ~ 수백 row
  - 상태별 조회 (PAID / PENDING)
    - 전체 대비 60~90% (PAID 비율이 높다고 가정)

- 전체 스캔 가능성
  - 주요 WHERE 조건
    - `reservation_id`
    - `status`
  - 인덱스 사용 시 Full Table Scan 발생 가능성 낮음
  - JOIN 시에도 `reservation_id` FK 기반으로 eq_ref / ref join 가능

**인덱스**
- 현재 payments 인덱스
  | Index              | Columns        | Purpose     |
  | ------------------ | -------------- | ----------- |
  | PRIMARY            | id             | PK          |
  | idx_reservation_id | reservation_id | 예약 단위 결제 조회 |
  | idx_status         | status         | 상태별 결제 조회   |

- 추가 추천 인덱스 (조회 패턴 확장 대비)
  - 예약 + 상태 동시 조회 최적화
    ```sql
    CREATE INDEX idx_reservation_status
    ON payments (reservation_id, status);
    ```
  - 결제 완료 시점 기준 통계 조회 대비
    ```sql
    CREATE INDEX idx_status_paidAt
    ON payments (status, paidAt);
    ```

  - 아래 쿼리의 빈도가 증가할 경우 고려 가능
    ```sql
    SELECT *
    FROM payments
    WHERE status = 'PAID'
    ORDER BY created_at DESC;
    ```
    추천 인덱스
    ```sql
    CREATE INDEX idx_status_created_at
    ON payments (status, created_at);
    ```
    - 상태 필터 + 최신순 정렬 동시 최적화
    - Filesort 방지

**EXPLAIN**
- **예약 단위 결제 조회**
  ```sql
  SELECT *
  FROM payments
  WHERE reservation_id = :reservationId;
  ```
  
  **EXPLAIN 요약**
  | 항목   | 값                    |
  | ----- | --------------------- |
  | type  | ref                   |
  | key   | idx_reservation_id    |
  | rows  | 1                     |
  | Extra | Using index condition |

  **분석**
  - `reservation_id` **기준 단건 조회**
  - `idx_reservation_id` 인덱스를 사용한 `ref` 접근
  - 정확히 1 row만 탐색
  - Full Table Scan 없음
 
  ✅ **O(1) 수준 성능, 최적 구조**

- **상태별 결제 조회**
  ```sql
  SELECT *
  FROM payments
  WHERE status = 'PAID';
  ```
  
  **EXPLAIN 요약**
  | 항목    | 값                   |
  | ----- | --------------------- |
  | type  | ref                   |
  | key   | idx_status            |
  | rows  | 약 49,990             |
  | Extra | Using index condition |


  **분석**
  - `status` 컬럼 기준 **Index Range Scan**
  - 결제 상태에 해당하는 row만 탐색
  - 데이터량은 많지만 의도된 범위 조회
  - Full Scan 아님, 인덱스 정상 활용

  ⚠️ 결과 수는 많으나 **상태별 결제 목록 조회 특성상 정상적인 비용**

- **유저 결제 내역 조회**
  ```sql
  SELECT p.*
  FROM reservations r
  JOIN payments p ON p.reservation_id = r.id
  WHERE r.user_id = :userId;
  ```
  
  **EXPLAIN 요약**
  | table        | type | key                | rows |
  | ------------ | ---- | ------------------ | ---- |
  | reservations | ref  | idx_user_status    | ~20  |
  | payments     | ref  | idx_reservation_id | 1    |

  **분석**
  - **1단계 : reservations**
    - user_id 기준 idx_user_status 인덱스 사용
    - 유저당 예약 수 ≈ 20건
    - 매우 제한된 범위 스캔
  - **2단계 : payments**
    - reservation_id 기준 단건 조회
    - 예약당 결제 1건

  ➡ Nested Loop Join 최적화 상태

  ✅ 유저 결제 내역 조회에 매우 효율적

**결론**
- 모든 결제 조회 쿼리에서 **ref / index 기반 접근**
- Full Table Scan 없음
- Join 순서 및 드라이빙 테이블 선택 적절
- 실서비스 트래픽 환경에서도 병목 가능성 낮음

### 2단계 : 부가 조회/통계

#### 잔여 좌석 수 조회 : TBD

#### 사용자별 예약 / 결제 내역 조회 : TBD

#### 통계 / 리포트 쿼리 : TBD

각 항목 모두 EXPLAIN 분석 및 벤치마크 필요

### 3단계 : 확장 / 예외 케이스

#### 검색 / 필터 기능 : TBD

#### 대기열, 토큰 발급, TEMP_HOLD 동시성 시나리오 : TBD

## 4. 분석 방법

**EXPLAIN 분석** : 쿼리 플랜 확인, 인덱스 사용 여부, 예상 rows 확인

**테스트 데이터** : 실제 사용량 기반 시뮬레이션 (수십~수백만 행 수준)

카디널리티 기반 인덱스 점검 및 필요 시 튜닝

**벤치마크** : latency 측정 및 반복 테스트 가능

## 5. 개선안 제안

**좌석 조회** : 단일 콘서트 기준 구조 유지 가능

**예약 / 결제** : EXPLAIN + 벤치마크 후 인덱스 / 쿼리 개선 필요

**데이터 증가 시** : 캐싱(CQRS, Redis) 또는 읽기 전용 레플리카 전략 고려
