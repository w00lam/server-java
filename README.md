---

## 📘 Concert Reservation Platform

고가용성과 동시성 환경에서 “콘서트 좌석 예약”을 안정적으로 처리하기 위한 서버 애플리케이션입니다.

본 문서는 구현된 기능, 시스템 구성, 아키텍처의 핵심 의사결정(ADR), 개발 문서 링크를 요약합니다.

---

## 🧭 Overview

본 서비스는 다음과 같은 핵심 요구사항을 해결하기 위해 설계되었습니다.
- **대규모 동시 예약 트래픽** 처리
- **좌석 중복 예약 방지**
- **임시 좌석 배정(PENDING) + TTL 기반 자동 해제**
- **결제 실패/성공과 예약 상태 전환의 일관성 유지**
- **유저 포인트·대기열(Queue) 관리**
- **메시지 기반 비동기 후처리(Kafka)**
- **운영 환경에서의 모니터링·Auto Recovery·Failover 지원**

보다 상세한 API/ERD/Infra/시퀀스는 docs 디렉토리를 참고하세요.

---

## 🔧 Key Features

- 콘서트 조회 → 날짜 → 좌석 → 예약 플로우
- Redis 기반 Seat Lock 및 임시 배정
- Kafka 기반 결제/예약 후처리 비동기 이벤트
- 사용자 포인트 충전/조회
- 대기열 Token 기반 접속 제어
- PostgreSQL Primary/Replica 기반 읽기 분산
- EKS 기반 배포 + CI/CD + Canary/Blue-Green 옵션
- Prometheus/Grafana 기반 모니터링

---

## 🧱 Architecture

```pgsql
Client → API Gateway → Server(Java)
         ├── PostgreSQL (Primary/Replica)
         ├── Redis (Cache + Lock)
         ├── Kafka (Async Events)
         └── Object Storage (Logs/Artifacts)
```

---

## 📝 ADR – 핵심 의사결정 요약

새로운 팀원이 전체 시스템의 설계 의도를 빠르게 파악하고,

향후 설계 변경/트레이드오프 판단을 쉽게 하도록 **핵심 아키텍처 선택의 배경(ADR)** 을 요약했습니다.


### 1. Redis를 캐시/분산락으로 분리 사용한 이유

#### 대안
1. 하나의 Redis 클러스터에서 캐시 + 락 + 대기열까지 전부 처리
2. 캐시용 Redis, 락용 Redis를 분리
3. 완전히 다른 기술 선택 (예: Zookeeper, Redlock, Etcd)

#### 결정
**캐시 Redis와 Seat Lock Redis를 논리적으로 분리**(Cluster 내 분리 또는 별도 인스턴스).

#### 이유 & 장단점
- 시는 캐시 미스/TTL/부하 변화가 잦아 장애 위험이 높음
- Seat Lock은 예약 중복 방지의 핵심 요소이므로 절대 안정성이 필요
- 분리함으로써 캐시 장애가 좌석 락 기능에 영향을 주지 않음
- 운영 중 lock latency, lock eviction 발생 위험 감소

#### 영향 범위
- 장애 복원력 증가
- Lock용 Redis 성능 모니터링 필요
- 구성·비용 증가(작지만 의미 있는)

### 2. 메시지 큐(Kafka)를 도입한 이유 (결제·예약 후처리)

#### 대안
1. 동기식 처리 (API 내부에서 결제+예약 후처리 모두 수행)
2. Kafka 기반 비동기 처리
3. SQS/Kinesis 등 다른 MQ 도입

#### 결정
**Kafka 기반 비동기 이벤트 처리** 선택.

#### 이유 & 장단점
- 결제 성공/실패 이벤트를 동기 처리하면 요청 시간이 길어지고 장애 영향이 커짐
- Kafka는 높은 처리량, 재처리 가능성, 장애 복원력 우수
- 이벤트 기반으로 전환하면 서비스 간 의존성 감소
- 단점은 운영 복잡성(Kafka 모니터링, Lag 관리)

#### 영향 범위
- 예약 확정(Confirmed) 처리 안정성이 증가
- 서비스 간 coupling 해소
- 배포/구성/모니터링 책임 증가

### 3. DB Primary/Replica 구조 선택 이유

#### 대안
1. 단일 DB
2. Primary/Replica로 읽기 분산
3. Sharding

#### 결정
**Primary/Replica 구조로 확장성 확보**

#### 이유 & 장단점
- 좌석 조회, 날짜 조회는 읽기 비율이 매우 높음
- Replica로 읽기를 분산하면 예약 API의 Write 부담 감소
- Sharding은 트래픽 패턴에 비해 과도한 복잡성

#### 영향 범위
- Replica lag 모니터링이 필수
- 장애 시 Replica → Primary 승격 전략 필요
- 데이터 정합성(일시적 지연) 고려 필요

### 4. TTL 기반 임시 좌석 배정(PENDING) 도입 이유

#### 대안
1. DB에 임시 상태를 저장하고 Cron으로 만료 처리
2. Redis TTL 기반 seat hold
3. 메시지 기반 만료 처리

#### 결정
**Redis TTL 기반 임시 seat hold**

#### 이유 & 장단점
- 동시성 트래픽 대응 필요
- TTL 자동 만료 → 복잡한 Cron/Cleanup 불필요
- Latency 짧고 빠른 read/write
- 단점은 Redis 장애시 seat hold 정보 유실 가능 → Lock Redis를 안정적으로 구성해야 함

#### 영향 범위
- 예약 중복 방지의 핵심 로직
- Failover 시 복구 전략·모니터링 필요

### 5. 대기열(Queue)을 Redis Sorted Set으로 구현한 이유

#### 대안
1. DB 기반 Queue
2. Redis Sorted Set
3. Kafka 기반 Queue

#### 결정
**Redis Sorted Set 사용**

#### 이유 & 장단점

- 초당 수만 건의 요청을 순식간에 정렬하여 순번 확정 가능
- TTL, 빠른 조회 가능
- Kafka는 순번 보장 방식이 적합하지 않음
- DB Queue는 트랜잭션 Lock 폭증

#### 영향 범위
- Queue Token 발급 속도와 정확도 확보
- Redis HA 장애 시 대기열 유지 전략 필요

---

## 📂 Documentation Map

| 문서               |
| ---------------- |
| **[API 전체 스펙 정의  ](docs/openapi.yml)** |
| **[ERD, 스키마 제약, 인덱스 설계](docs/erd.md)** |
| **[Infra, 배포 전략, 운영/모니터링](docs/infra.md)** |
| **[예약·결제 시나리오](docs/ssd.md)** |
| **[마일스톤](docs/milestone.md)** |
| **[쿼리 성능 분석](docs/performance-analysis.md)** |
| **[좌석 예약 동시성 처리 보고서](docs/Seat_Reservation_Concurrency_Report_2025_12_25.md)** |
| **[Redis 캐싱 전략 적용을 통한 조회 성능 개선 보고서](docs/Redis_Caching_Strategy_for_Improving_Read_Performance_Report_2026_01_22.md)** |
---
