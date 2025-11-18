# Concert Reservation Service (Java)

---

## 프로젝트 개요
본 프로젝트는 **콘서트 예약 서비스**를 Java 기반으로 구현한 시스템입니다.  
주요 특징:
- 높은 동시성 및 트래픽 처리
- QueueToken 기반 접근 제어
- 좌석 임시 예약(TTL 5분) 및 분산 락 처리
- Wallet 포인트 결제 및 외부 PG 연동
- 예약/결제 트랜잭션 일관성 보장
- 확장 가능한 MSA 구조

---

## 주요 기능
- 사용자 로그인 및 인증 (JWT + QueueToken 발급)
- 콘서트 조회, 예약 가능한 날짜 조회
- 좌석 조회 (등급, 가격 포함)
- 좌석 임시 예약 및 만료 처리
- Wallet 기반 포인트 충전 및 결제
- 최종 예약 확정 및 결제 트랜잭션 관리

---

## 문서

[API 명세서](https://github.com/w00lam/server-java/blob/main/docs/openapi.yml)

[ERD](https://github.com/w00lam/server-java/blob/main/docs/erd.md)

[인프라 구성도](https://github.com/w00lam/server-java/blob/main/docs/infra.md)

[시퀀스 다이어그램](https://github.com/w00lam/server-java/blob/main/docs/ssd.md)

[마일스톤](https://github.com/w00lam/server-java/blob/main/docs/milestone.md)

---
