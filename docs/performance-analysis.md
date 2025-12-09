# Performance Analysis

## 1. 목적
고빈도 조회 쿼리 및 주요 테이블 조회 성능을 분석하고, 개발 진행과 함께 점진적으로 기록하기 위한 문서.

---

## 2. 분석 대상
- **Tables :** seats, reservations, payments, concert_date 등  
- **Focus :** 조회 쿼리 성능, 인덱스 활용, row 수, 동시성 영향

---

## 3. 단계별 조회 성능 분석

### 1단계: 핵심 조회
- [x] **좌석 조회 (seats)**
  - 쿼리/메서드 : `findAllByConcertDateId`
  - 데이터 규모 : 콘서트당 50개
  - 인덱스 : uk_concert_date_section_seat_row_number / idx_concert_date_section_seat_row
  - EXPLAIN : Using index condition, ref, key_len=16
  - 결론 : 성능 문제 없음
  - 비고 : soft delete 미사용, 단일 콘서트 조회 기준 안전

- [ ] **활성 예약 조회 (reservations)**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료
  - 비고 : EXPLAIN + 벤치마크 필요

- [ ] **결제 상태 조회 (payments)**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료
  - 비고 : 동일

### 2단계: 부가 조회/통계
- [ ] **잔여 좌석 수 조회**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료

- [ ] **사용자별 예약/결제 내역 조회**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료

- [ ] **통계/리포트 쿼리**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료

### 3단계: 확장/예외 케이스
- [ ] **검색/필터 기능**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료

- [ ] **대기열, 토큰 발급, TEMP_HOLD 관련 동시성 시나리오**
  - 쿼리/메서드 : TBD
  - 데이터 규모 : TBD
  - 인덱스 : TBD
  - EXPLAIN : TBD
  - 결론 : 분석 미완료

---

## 4. 분석 방법
1. EXPLAIN 분석 : 쿼리 플랜 확인, 인덱스 사용 여부, rows 예측  
2. 테스트 데이터 : 실제 사용량을 가정한 시뮬레이션 (수십~수백만 행 수준)  
3. 카디널리티 기반 인덱스 확인 및 필요 시 튜닝  
4. 벤치마크 : latency 측정 및 반복 테스트 가능  

---

## 5. 개선안 제안
- **좌석 조회 :** 단일 콘서트 기준 구조 유지 가능  
- **예약/결제 :** EXPLAIN + 벤치마크 후 인덱스/쿼리 개선 필요  
- **데이터 증가 시 :** 캐싱(CQRS, Redis) 또는 읽기 전용 레플리카 전략 고려
